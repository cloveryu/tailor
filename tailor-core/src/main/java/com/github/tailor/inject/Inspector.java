package com.github.tailor.inject;

import java.lang.reflect.Constructor;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:29 PM
 */
public interface Inspector {

    <T> Constructor<T> constructorFor(Class<T> type);
}
