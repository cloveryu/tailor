package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:22 PM
 */
public final class Resource<T> {

    private final Instance<T> instance;
    private final Target target;

    public Resource(Instance<T> instance, Target target) {
        super();
        this.instance = instance;
        this.target = target;
    }

    public Type<T> getType() {
        return instance.getType();
    }

    public boolean equalTo(Resource<?> other) {
        return this == other || instance.equalTo(other.instance) && target.equalTo(other.target);
    }

    public boolean isApplicableFor(Dependency<? super T> dependency) {
        return isAdequateFor(dependency) && isAvailableFor(dependency) && isAssignableTo(dependency);
    }

    public boolean isAdequateFor(Dependency<? super T> dependency) {
        return instance.getName().isApplicableFor(dependency.getName());
    }

    public boolean isAvailableFor(Dependency<? super T> dependency) {
        return target.isApplicableFor(dependency);
    }

    public boolean isAssignableTo(Dependency<? super T> dependency) {
        return instance.getType().isAssignableTo(dependency.getType());
    }

    public Instance<T> getInstance() {
        return instance;
    }

    public Target getTarget() {
        return target;
    }

}
