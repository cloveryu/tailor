package com.github.tailor.inject;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:29 PM
 */
public interface Inspector {

    <T> Constructor<T> constructorFor(Class<T> type);

    Parameter[] parametersFor(AccessibleObject obj);

    <T> Method[] methodsIn(Class<T> implementor);

    Name nameFor(AccessibleObject obj);
}
