package com.github.tailor.inject.bind;

import com.github.tailor.inject.Inspector;
import com.github.tailor.inject.Scope;
import com.github.tailor.inject.Source;
import com.github.tailor.inject.Target;
import com.github.tailor.inject.bootstrap.Bindings;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:48 PM
 */
public final class Bind {

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

    public Bind with( Target target ) {
        return new Bind( bindings, inspector, source, scope, target );
    }
}
