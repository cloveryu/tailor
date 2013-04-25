package com.github.tailor.inject;

import static com.github.tailor.inject.DIRuntimeException.*;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:47 PM
 */
public interface Injector {

    <T> T resolve(Dependency<T> dependency) throws NoSuchResourceException, MoreFrequentExpiryException, DependencyCycleException;
}
