package com.github.tailor.inject.bind;

import com.github.tailor.inject.Inspector;
import com.github.tailor.inject.bootstrap.Bindings;
import com.github.tailor.inject.bootstrap.Bootstrapper;
import com.github.tailor.inject.bootstrap.Bundle;
import com.github.tailor.inject.bootstrap.Module;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:09 PM
 */
public abstract class BinderModule extends InitializedBinder implements Bundle, Module {

    protected BinderModule() {
        super();
    }

    @Override
    public final void bootstrap(Bootstrapper bootstrap) {
        bootstrap.install(this);
    }

    @Override
    public final void declare(Bindings bindings, Inspector inspector) {
        init(bindings, inspector);
        declare();
    }

    @Override
    public String toString() {
        return "module " + getClass().getSimpleName();
    }

    protected abstract void declare();

}
