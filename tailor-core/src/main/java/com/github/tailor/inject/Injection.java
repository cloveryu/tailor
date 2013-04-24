package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:50 PM
 */
public final class Injection {

    private final Instance<?> dependency;
    private final Emergence<?> target;

    Injection(Instance<?> dependency, Emergence<?> target) {
        super();
        this.dependency = dependency;
        this.target = target;
    }

    public Emergence<?> getTarget() {
        return target;
    }

    public boolean equalTo(Injection other) {
        return this == other || dependency.equalTo(other.dependency)
                && target.getInstance().equalTo(other.target.getInstance());
    }

    @Override
    public String toString() {
        return "(" + dependency + "->" + target + ")";
    }
}
