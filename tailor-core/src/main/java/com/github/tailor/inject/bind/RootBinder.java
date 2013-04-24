package com.github.tailor.inject.bind;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:48 PM
 */
public class RootBinder extends Binder {

    RootBinder(Bind bind) {
        this(null, bind);
    }

    RootBinder(RootBinder root, Bind bind) {
        super(root, bind);
    }
}
