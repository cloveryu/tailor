package com.github.tailor.inject.bootstrap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 4:19 PM
 */
public final class Invoke {

    public static <T> T constructor(Constructor<T> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object method(Method method, Object owner, Object... args) {
        try {
            return method.invoke(owner, args);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException) e).getTargetException();
                if (t instanceof Exception) {
                    e = (Exception) t;
                }
            }
            throw new RuntimeException("Failed to invoke method: " + method + " \n" + e.getMessage(), e);
        }
    }
}
