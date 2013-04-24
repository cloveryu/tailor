package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:46 PM
 */
public interface Supplier<T> {

    T supply(Dependency<? super T> dependency, Injector injector);
}
