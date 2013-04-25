package com.github.tailor.inject;

import static com.github.tailor.inject.Type.raw;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:22 PM
 */
public final class Resource<T> {

    public static <T> Resource<T> resource( Class<T> type ) {
        return new Resource<T>( Instance.anyOf( raw( type ) ) );
    }

    private final Instance<T> instance;
    private final Target target;

    public Resource( Instance<T> instance ) {
        this( instance, Target.ANY );
    }

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

    public <E> Resource<E> typed(Type<E> type) {
        return new Resource<E>(instance.typed(type), target);
    }

}
