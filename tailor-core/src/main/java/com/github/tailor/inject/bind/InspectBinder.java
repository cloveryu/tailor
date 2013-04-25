package com.github.tailor.inject.bind;

import com.github.tailor.inject.*;
import com.github.tailor.inject.util.Scoped;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 10:42 PM
 */
public class InspectBinder {

    private final Inspector inspector;
    private final ScopedBinder binder;

    InspectBinder( Inspector inspector, RootBinder binder, Scope scope ) {
        super();
        this.inspector = inspector;
        this.binder = binder.on( binder.bind().asAuto() ).per( scope );
    }

    public void in( Class<?> implementor ) {
        in( implementor, new Parameter[0] );
    }

    public void in( Object implementingInstance, Parameter... parameters ) {
        bindMethodsIn( implementingInstance.getClass(), implementingInstance, parameters );
    }

    public void in( Class<?> implementor, Parameter... parameters ) {
        boolean instanceMethods = bindMethodsIn( implementor, null, parameters );
        Constructor<?> c = inspector.constructorFor( implementor );
        if ( c == null ) {
            if ( instanceMethods ) {
                binder.root.per( Scoped.APPLICATION ).implicit().construct( implementor );
            }
        } else {
            if ( parameters.length == 0 ) {
                parameters = inspector.parametersFor( c );
            }
            bind( (Constructor<?>) c, parameters );
        }
    }

    private boolean bindMethodsIn( Class<?> implementor, Object instance, Parameter[] parameters) {
        boolean instanceMethods = false;
        for ( Method method : inspector.methodsIn( implementor ) ) {
            Type<?> returnType = Type.returnType( method );
            if ( !Type.VOID.equalTo( returnType ) ) {
                if ( parameters.length == 0 ) {
                    parameters = inspector.parametersFor( method );
                }
                bind( returnType, method, instance, parameters );
                instanceMethods = instanceMethods || !Modifier.isStatic(method.getModifiers());
            }
        }
        return instanceMethods;
    }

    private <T> void bind( Type<T> returnType, Method method, Object instance, Parameter[] parameters) {
        binder.bind( inspector.nameFor( method ), returnType ).to(SuppliedBy.method( returnType, method, instance, parameters ) );
    }

    private <T> void bind( Constructor<T> constructor, Parameter... parameters ) {
        Name name = inspector.nameFor( constructor );
        Class<T> implementation = constructor.getDeclaringClass();
        if ( name.isDefault() ) {
            binder.autobind( implementation ).to( constructor, parameters );
        } else {
            binder.bind( name, implementation ).to( constructor, parameters );
            for ( Type<? super T> st : Type.raw( implementation ).supertypes() ) {
                if ( st.isInterface() ) {
                    binder.implicit().bind( name, st ).to( name, implementation );
                }
            }
        }
    }

    public void in( Class<?> implementor, Class<?>... implementors ) {
        in( implementor );
        for ( Class<?> i : implementors ) {
            in( i );
        }
    }

    public void inModule() {
        in( binder.bind().source.getIdent() );
    }
}
