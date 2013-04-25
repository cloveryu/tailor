package com.github.tailor.inject.bind;

import com.github.tailor.inject.Scope;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:48 PM
 */
public class RootBinder extends ScopedBinder {

    RootBinder(Bind bind) {
        this(null, bind);
    }

    RootBinder(RootBinder root, Bind bind) {
        super(root, bind);
    }

    public RootBinder asDefault() {
        return on(bind().asDefault());
    }

    protected RootBinder on(Bind bind) {
        return new RootBinder(bind);
    }

    public ScopedBinder per(Scope scope) {
        return new ScopedBinder(root, bind().per(scope));
    }
}
