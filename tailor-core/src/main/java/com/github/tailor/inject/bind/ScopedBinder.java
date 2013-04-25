package com.github.tailor.inject.bind;

import com.github.tailor.inject.Inspector;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 7:24 PM
 */
public class ScopedBinder extends TargetedBinder {

    ScopedBinder(RootBinder root, Bind bind) {
        super(root, bind);
    }

    public InspectBinder bind(Inspector inspector) {
        return new InspectBinder(inspector, root, bind().scope);
    }
}
