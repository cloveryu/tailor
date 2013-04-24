package com.github.tailor.inject.util;

import java.lang.reflect.AccessibleObject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 4:17 PM
 */
public final class Metaclass {

    public static <T extends AccessibleObject> T accessible(T obj) {
        obj.setAccessible(true);
        return obj;
    }

}
