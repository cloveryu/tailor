package com.github.tailor.inject.bind;

import com.github.tailor.inject.*;
import com.github.tailor.inject.bootstrap.Bindings;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:48 PM
 */
public final class Bind {

    public static Bindings autobinding(Bindings delegate) {
        return new AutobindBindings(delegate);
    }

    public static Bind create(Bindings bindings, Inspector inspector, Source source, Scope scope) {
        return new Bind(bindings, inspector, source, scope, Target.ANY);
    }

    final Bindings bindings;
    final Inspector inspector;
    final Source source;
    final Scope scope;
    final Target target;

    private Bind(Bindings bindings, Inspector inspector, Source source, Scope scope, Target target) {
        super();
        this.bindings = bindings;
        this.inspector = inspector;
        this.source = source;
        this.scope = scope;
        this.target = target;
    }

    public Bind into(Bindings bindings) {
        return new Bind(bindings, inspector, source, scope, target);
    }

    public Bind using(Inspector inspector) {
        return new Bind(bindings, inspector, source, scope, target);
    }

    public Bind with(Source source) {
        return new Bind(bindings, inspector, source, scope, target);
    }

    public Bind asDefault() {
        return as(DeclarationType.DEFAULT);
    }

    public Bind as(DeclarationType type) {
        return with(source.typed(type));
    }

    public Bind per(Scope scope) {
        return new Bind(bindings, inspector, source, scope, target);
    }

    public Bind autobinding() {
        return into(autobinding(bindings));
    }

    public Bind asAuto() {
        return as(DeclarationType.AUTO);
    }

    private static class AutobindBindings implements Bindings {

        private final Bindings delegate;

        AutobindBindings(Bindings delegate) {
            super();
            this.delegate = delegate;
        }

        @Override
        public <T> void add(Resource<T> resource, Supplier<? extends T> supplier, Scope scope, Source source) {
            delegate.add(resource, supplier, scope, source);
            Type<T> type = resource.getType();
            for (Type<? super T> supertype : type.supertypes()) {
                if (supertype.getRawType() != Object.class) {
                    delegate.add(resource.typed(supertype), supplier, scope, source);
                }
            }
        }
    }
}
