package com.github.tailor.inject.bootstrap;

import com.github.tailor.inject.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:19 PM
 */
public class Inspect implements Inspector {

    public static final Inspect DEFAULT = all().constructors();
    private static final Parameter[] NO_PARAMETERS = new Parameter[0];
    private static final Method[] NO_METHODS = new Method[0];

    public static Inspect all() {
        return new Inspect(false, true, true, Packages.ALL, Type.OBJECT, null, null);
    }

    private final boolean statics;
    private final boolean methods;
    private final boolean constructors;
    private final Packages packages;
    private final Type<?> assignable;
    private final Class<? extends Annotation> accessible;
    private final Class<? extends Annotation> namedby;

    private Inspect(boolean statics, boolean methods, boolean constructors, Packages packages,
                    Type<?> assignable, Class<? extends Annotation> accessible,
                    Class<? extends Annotation> namedBy) {
        super();
        this.methods = methods;
        this.constructors = constructors;
        this.statics = statics;
        this.accessible = accessible;
        this.packages = packages;
        this.namedby = namedBy;
        this.assignable = assignable;
    }

    public Inspect constructors() {
        return new Inspect(statics, false, true, packages, assignable, accessible, namedby);
    }

    public Inspect methods() {
        return new Inspect(statics, true, false, packages, assignable, accessible, namedby);
    }

    public Inspect annotatedWith(Class<? extends Annotation> annotation) {
        return new Inspect(statics, methods, constructors, packages, assignable, annotation, namedby);
    }

    public Inspect namedBy(Class<? extends Annotation> annotation) {
        return new Inspect(statics, methods, constructors, packages, assignable, accessible, annotation);
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

    @Override
    public Parameter[] parametersFor(AccessibleObject obj) {
        if (namedby == null) {
            return NO_PARAMETERS;
        }
        if (obj instanceof Method) {
            Method method = (Method) obj;
            return parametersFor(Type.parameterTypes(method), method.getParameterAnnotations());
        }
        if (obj instanceof Constructor<?>) {
            Constructor<?> constructor = (Constructor<?>) obj;
            return parametersFor(Type.parameterTypes(constructor),
                    constructor.getParameterAnnotations());
        }
        return NO_PARAMETERS;
    }

    @Override
    public <T> Method[] methodsIn(Class<T> implementor) {
        if (!methods) {
            return NO_METHODS;
        }
        List<Method> res = new ArrayList<Method>();
        for (Method m : implementor.getDeclaredMethods()) {
            if (matches(m)) {
                res.add(m);
            }
        }
        return Array.of(res, NO_METHODS);
    }

    private boolean matches(Method m) {
        Type<?> returnType = Type.returnType(m);
        return packages.contains(returnType) && returnType.isAssignableTo(assignable)
                && (!statics || Modifier.isStatic(m.getModifiers()))
                && (accessible == null || m.isAnnotationPresent(accessible));
    }

    private Parameter[] parametersFor(Type<?>[] types, Annotation[][] annotations) {
        List<Parameter> res = new ArrayList<Parameter>();
        for (int i = 0; i < annotations.length; i++) {
            Name name = Name.namedBy(namedby, annotations[i]);
            if (name != Name.DEFAULT) {
                res.add(Instance.instance(name, types[i]));
            }
        }
        return Array.of(res, NO_PARAMETERS);
    }

    @Override
    public Name nameFor(AccessibleObject obj) {
        return Name.namedBy(namedby, obj);
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
