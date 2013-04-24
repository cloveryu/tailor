package com.github.tailor.inject.bind;

import com.github.tailor.inject.Inspector;
import com.github.tailor.inject.Scope;
import com.github.tailor.inject.bootstrap.Bindings;
import com.github.tailor.inject.bootstrap.Inspect;
import com.github.tailor.inject.util.Scoped;

import static com.github.tailor.inject.Source.source;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:41 PM
 */
public class InitializedBinder extends RootBinder {

    private Bind bind;

    protected InitializedBinder() {
        this(Scoped.APPLICATION);
    }

    protected InitializedBinder(Scope initial) {
        super(Bind.create(null, Inspect.DEFAULT, source(InitializedBinder.class), initial));
        this.bind = super.bind();
    }

    @Override
    final Bind bind() {
        return bind;
    }

    protected final void init(Bindings bindings, Inspector inspector) {
        this.bind = super.bind().into(bindings).using(inspector).with(source(getClass()));
    }
}
