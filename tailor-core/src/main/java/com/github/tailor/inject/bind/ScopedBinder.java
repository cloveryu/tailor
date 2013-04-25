package com.github.tailor.inject.bind;

import com.github.tailor.inject.Instance;
import com.github.tailor.inject.Name;
import com.github.tailor.inject.Target;
import com.github.tailor.inject.Type;

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

    public TargetedBinder injectingInto( Name name, Class<?> type ) {
        return injectingInto( name, raw( type ) );
    }

    public TargetedBinder injectingInto( Name name, Type<?> type ) {
        return injectingInto( Instance.instance(name, type) );
    }

    public TargetedBinder injectingInto( Instance<?> target ) {
        return new TargetedBinder( root, bind().with( Target.targeting(target) ) );
    }
}
