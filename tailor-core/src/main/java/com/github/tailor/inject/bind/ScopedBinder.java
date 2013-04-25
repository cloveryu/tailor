package com.github.tailor.inject.bind;

import com.github.tailor.inject.*;

import static com.github.tailor.inject.Instance.defaultInstanceOf;
import static com.github.tailor.inject.Type.raw;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 7:24 PM
 */
public class ScopedBinder extends TargetedBinder {

    ScopedBinder(RootBinder root, Bind bind) {
        super(root, bind);
    }

    public TargetedBinder injectingInto(Name name, Class<?> type) {
        return injectingInto(name, raw(type));
    }

    public TargetedBinder injectingInto(Name name, Type<?> type) {
        return injectingInto(Instance.instance(name, type));
    }

    public TargetedBinder injectingInto(Instance<?> target) {
        return new TargetedBinder(root, bind().with(Target.targeting(target)));
    }

    public TargetedBinder injectingInto( Class<?> target ) {
        return injectingInto( raw(target) );
    }

    public TargetedBinder injectingInto( Type<?> target ) {
        return injectingInto( defaultInstanceOf( target ) );
    }

    public InspectBinder bind( Inspector inspector ) {
        return new InspectBinder( inspector, root, bind().scope );
    }
}
