package com.github.tailor.inject.bind;

import com.github.tailor.inject.*;

import java.lang.reflect.Constructor;

import static com.github.tailor.inject.Instance.defaultInstanceOf;
import static com.github.tailor.inject.Instance.instance;

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
        return bind(Type.raw(type));
    }

    public <T> TypedBinder<T> bind(Type<T> type) {
        return bind(defaultInstanceOf(type));
    }

    public <T> TypedBinder<T> bind(Instance<T> instance) {
        return new TypedBinder<T>(this, instance);
    }

    public <T> TypedBinder<T> bind(Name name, Class<T> type) {
        return bind(name, Type.raw(type));
    }

    public <T> TypedBinder<T> bind(Name name, Type<T> type) {
        return bind(instance(name, type));
    }

    protected final <T> void bind(Resource<T> resource, Supplier<? extends T> supplier) {
        Bind b = bind();
        b.bindings.add(resource, supplier, b.scope, b.source);
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
    }
}
