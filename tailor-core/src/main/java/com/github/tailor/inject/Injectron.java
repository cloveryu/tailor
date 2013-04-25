package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:55 PM
 */
public interface Injectron<T> {

    Resource<T> getResource();

    Source getSource();

    T instanceFor(Dependency<? super T> dependency);

}
