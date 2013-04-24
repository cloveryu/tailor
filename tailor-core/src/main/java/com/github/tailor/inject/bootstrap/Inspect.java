package com.github.tailor.inject.bootstrap;

import com.github.tailor.inject.Inspector;
import com.github.tailor.inject.Packages;
import com.github.tailor.inject.Type;

import java.lang.reflect.Constructor;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:19 PM
 */
public class Inspect implements Inspector {

    public static final Inspect DEFAULT = all().constructors();

    public static Inspect all() {
        return new Inspect(false, true, true, Packages.ALL, Type.OBJECT);
    }

    private final boolean statics;
    private final boolean methods;
    private final boolean constructors;
    private final Packages packages;
    private final Type<?> assignable;

    private Inspect(boolean statics, boolean methods, boolean constructors, Packages packages, Type<?> assignable) {
        super();
        this.methods = methods;
        this.constructors = constructors;
        this.statics = statics;
        this.packages = packages;
        this.assignable = assignable;
    }

    public Inspect constructors() {
        return new Inspect(statics, false, true, packages, assignable);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Constructor<T> constructorFor(Class<T> type) {
        if (constructors && packages.contains(Type.raw(type)) && Type.raw(type).isAssignableTo(assignable)) {
            try {
                return Inspect.defaultConstructor(type);
            } catch (RuntimeException e) {
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> defaultConstructor(Class<T> declaringClass) {
        Constructor<?>[] constructors = declaringClass.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new RuntimeException(new NoSuchMethodException(declaringClass.getCanonicalName()));
        }
        int noArgsIndex = 0;
        for (int i = 0; i < constructors.length; i++) {
            if (constructors[i].getParameterTypes().length == 0) {
                noArgsIndex = i;
            }
        }
        return (Constructor<T>) constructors[noArgsIndex];
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> noArgsConstructor(Class<T> declaringClass) {
        if (declaringClass.isInterface()) {
            throw new IllegalArgumentException("Interfaces don't have constructors: " + declaringClass);
        }
        Constructor<T> c;
        try {
            c = declaringClass.getDeclaredConstructor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return c;
    }
}
