package com.github.tailor.inject.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 4:17 PM
 */
public final class Metaclass {

    public final static Metaclass metaclass(Class<?> cls) {
        return new Metaclass(cls);
    }

    private final Class<?> cls;

    private Metaclass(Class<?> cls) {
        super();
        this.cls = cls;
    }

    public static <T extends AccessibleObject> T accessible(T obj) {
        obj.setAccessible(true);
        return obj;
    }

    public final boolean undeterminable() {
        return cls.isInterface() || cls.isEnum() || cls.isPrimitive() || cls.isArray()
                || Modifier.isAbstract(cls.getModifiers()) || cls == String.class
                || Number.class.isAssignableFrom(cls) || cls == Boolean.class
                || cls == Void.class || cls == void.class
                || Collection.class.isAssignableFrom(cls);
    }

}
