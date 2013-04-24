package com.github.tailor.inject;

import java.util.Arrays;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:15 PM
 */
public final class Instances {

    public static final Instances ANY = new Instances();

    private final Instance<?>[] hierarchy;

    private Instances(Instance<?>... hierarchy) {
        super();
        this.hierarchy = hierarchy;
    }

    public boolean equalTo(Instances other) {
        return this == other || Arrays.equals(other.hierarchy, hierarchy);
    }

    public boolean isAny() {
        return hierarchy.length == 0;
    }

    public int depth() {
        return hierarchy.length;
    }

    public Instance<?> at(int depth) {
        return hierarchy[depth];
    }
}
