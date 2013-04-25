package com.github.tailor.inject.bind;

import com.github.tailor.inject.*;

import java.lang.reflect.Constructor;

import static com.github.tailor.inject.Instance.defaultInstanceOf;
import static com.github.tailor.inject.Instance.instance;
import static com.github.tailor.inject.Type.*;
import static com.github.tailor.inject.util.Metaclass.metaclass;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:42 PM
 */
public class Binder {

    final RootBinder root;
    private final Bind bind;

    Binder(RootBinder root, Bind bind) {
        super();
        this.root = root == null ? (RootBinder) this : root;
        this.bind = bind;
    }

    Bind bind() {
        return bind;
    }

    public <T> TypedBinder<T> bind(Class<T> type) {
        return bind(raw(type));
    }

    public <T> TypedBinder<T> bind(Type<T> type) {
        return bind(defaultInstanceOf(type));
    }

    public <T> TypedBinder<T> bind(Instance<T> instance) {
        return new TypedBinder<T>(this, instance);
    }

    public <T> TypedBinder<T> bind(Name name, Class<T> type) {
        return bind(name, raw(type));
    }

    public <T> TypedBinder<T> bind(Name name, Type<T> type) {
        return bind(instance(name, type));
    }

    protected final <T> void bind(Resource<T> resource, Supplier<? extends T> supplier) {
        Bind b = bind();
        b.bindings.add(resource, supplier, b.scope, b.source);
    }

    protected Binder on(Bind bind) {
        return new Binder(root, bind);
    }

    public <T> TypedBinder<T> multibind( Class<T> type ) {
        return multibind( raw(type) );
    }

    public <T> TypedBinder<T> multibind( Type<T> type ) {
        return multibind( Instance.defaultInstanceOf(type) );
    }

    public <T> TypedBinder<T> multibind( Instance<T> instance ) {
        return on( bind().asMulti() ).bind( instance );
    }

    public <T> TypedBinder<T> multibind( Name name, Class<T> type ) {
        return multibind( instance( name, Type.raw( type ) ) );
    }

    public void construct( Class<?> type ) {
        construct( ( defaultInstanceOf( raw( type ) ) ) );
    }

    public void construct( Instance<?> instance ) {
        bind( instance ).toConstructor();
    }

    protected final <I> void implicitBindToConstructor( Instance<I> instance ) {
        Class<I> impl = instance.getType().getRawType();
        if ( metaclass( impl ).undeterminable() ) {
            return;
        }
        Constructor<I> constructor = bind().inspector.constructorFor( impl );
        if ( constructor != null ) {
            implicit().with( Target.ANY ).bind( instance ).to( constructor );
        }
    }

    protected final Binder implicit() {
        return on( bind().asImplicit() );
    }

    protected Binder with( Target target ) {
        return new Binder( root, bind().with( target ) );
    }

    public static class TypedBinder<T> {

        private final Binder binder;
        private final Resource<T> resource;

        TypedBinder(Binder binder, Instance<T> instance) {
            this(binder, new Resource<T>(instance, binder.bind().target));
        }

        TypedBinder(Binder binder, Resource<T> resource) {
            super();
            this.binder = binder;
            this.resource = resource;
        }

        public void to(T constant) {
            toConstant(constant);
        }

        public void to(Constructor<? extends T> constructor, Parameter... parameters) {
            to(SuppliedBy.costructor(constructor, parameters));
        }

        public <I extends T> void to( Name name, Class<I> type ) {
            to( instance( name, raw( type ) ) );
        }

        public <I extends T> void to( Instance<I> instance ) {
            to( supply( instance ) );
        }

        private TypedBinder<T> toConstant(T constant) {
            to(SuppliedBy.constant(constant));
            return this;
        }

        public void to(Supplier<? extends T> supplier) {
            binder.bind(resource, supplier);
        }

        public void toConstructor() {
            to(binder.bind().inspector.constructorFor(resource.getType().getRawType()));
        }

        public void toConstructor(Parameter... parameters) {
            toConstructor(getType().getRawType(), parameters);
        }

        protected final Type<T> getType() {
            return resource.getType();
        }

        public void toConstructor(Class<? extends T> impl, Parameter... parameters) {
            if (metaclass(impl).undeterminable()) {
                throw new IllegalArgumentException("Not a constructable type: " + impl);
            }
            to(SuppliedBy.costructor(binder.bind().inspector.constructorFor(impl), parameters));
        }

        <I> Supplier<I> supply( Instance<I> instance ) {
            if ( !resource.getInstance().equalTo( instance ) ) {
                implicitBindToConstructor( instance );
                return SuppliedBy.instance( instance );
            }
            if ( instance.getType().getRawType().isInterface() ) {
                throw new IllegalArgumentException( "Interface type linked in a loop: "
                        + resource.getInstance() + " > " + instance );
            }
            return SuppliedBy.costructor( binder.bind().inspector.constructorFor( instance.getType().getRawType() ) );
        }

        private <I> void implicitBindToConstructor( Instance<I> instance ) {
            binder.implicitBindToConstructor( instance );
        }
    }

}
