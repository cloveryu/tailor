package com.github.tailor.inject;

import java.lang.reflect.*;
import java.util.*;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 10:55 AM
 */
public final class Type<T> implements Parameter {

    public static final Type<Object> OBJECT = Type.raw(Object.class);
    public static final Type<?> WILDCARD = OBJECT.asLowerBound();

    public static Type<?>[] parameterTypes(Constructor<?> constructor) {
        return parameterTypes(constructor.getGenericParameterTypes());
    }

    private static Type<?>[] parameterTypes(java.lang.reflect.Type[] genericParameterTypes) {
        Type<?>[] res = new Type<?>[genericParameterTypes.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = type(genericParameterTypes[i]);
        }
        return res;
    }

    private final Class<T> rawType;
    private final Type<?>[] params;
    private final boolean lowerBound;

    private Type(boolean lowerBound, Class<T> rawType, Type<?>[] parameters) {
        assert (rawType != null);
        this.rawType = primitiveAsWrapper(rawType);
        this.params = parameters;
        this.lowerBound = lowerBound;
    }

    private Type(Class<T> rawType, Type<?>[] parameters) {
        this(false, rawType, parameters);
    }

    private Type(Class<T> rawType) {
        this(false, rawType, new Type<?>[0]);
    }

    public static <T> Type<T> raw(Class<T> type) {
        return new Type<T>(type);
    }

    public boolean equalTo(Type<?> other) {
        if (this == other) {
            return true;
        }
        if (rawType != other.rawType) {
            return false;
        }
        if (params.length != other.params.length) {
            return false;
        }
        for (int i = 0; i < params.length; i++) {
            if (!params[i].equalTo(other.params[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return rawType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Type<?> && equalTo((Type<?>) obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> primitiveAsWrapper(Class<T> primitive) {
        if (!primitive.isPrimitive()) {
            return primitive;
        }
        if (primitive == int.class) {
            return (Class<T>) Integer.class;
        }
        if (primitive == boolean.class) {
            return (Class<T>) Boolean.class;
        }
        if (primitive == long.class) {
            return (Class<T>) Long.class;
        }
        if (primitive == char.class) {
            return (Class<T>) Character.class;
        }
        if (primitive == void.class) {
            return (Class<T>) Void.class;
        }
        if (primitive == float.class) {
            return (Class<T>) Float.class;
        }
        if (primitive == double.class) {
            return (Class<T>) Double.class;
        }
        if (primitive == byte.class) {
            return (Class<T>) Byte.class;
        }
        if (primitive == short.class) {
            return (Class<T>) Short.class;
        }
        throw new UnsupportedOperationException("The primitive " + primitive + " cannot be wrapped yet!");
    }

    public Type<T> parameterized(Class<?>... arguments) {
        Type<?>[] typeArgs = new Type<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            typeArgs[i] = raw(arguments[i]);
        }
        return parameterized(typeArgs);
    }

    public Type<T> parameterized(Type<?>... parameters) {
        // should check parameters
        return new Type<T>(lowerBound, rawType, parameters);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        toString(b);
        return b.toString();
    }

    private void toString(StringBuilder b) {
        if (isLowerBound()) {
            if (rawType == Object.class) {
                b.append("?");
                return;
            }
            b.append("? extends ");
        }
        String canonicalName = rawType.getCanonicalName();
        b.append(rawType.isArray() ? canonicalName.substring(0, canonicalName.indexOf('[')) : canonicalName);
        if (isParameterized()) {
            b.append('<');
            params[0].toString(b);
            for (int i = 1; i < params.length; i++) {
                b.append(',');
                params[i].toString(b);
            }
            b.append('>');
        }
        if (rawType.isArray()) {
            b.append(canonicalName.substring(canonicalName.indexOf('[')));
        }
    }

    public boolean isLowerBound() {
        return lowerBound;
    }

    public boolean isParameterized() {
        return params.length > 0;
    }

    public Type<? extends T> asLowerBound() {
        return lowerBound(true);
    }

    public Type<? extends T> lowerBound(boolean lowerBound) {
        return new Type<T>(lowerBound, rawType, params);
    }

    public static Type<?> fieldType(Field field) {
        return type(field.getGenericType());
    }

    private static Type<?> type(java.lang.reflect.Type type) {
        return type(type, Collections.<String, Type<?>>emptyMap());
    }

    private static Type<?> type(java.lang.reflect.Type type, Map<String, Type<?>> actualTypeArguments) {
        if (type instanceof Class<?>) {
            return raw((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            return parameterizedType((ParameterizedType) type, actualTypeArguments);
        }
        throw new UnsupportedOperationException("Type has no support yet: " + type);
    }

    private static <T> Type<T> parameterizedType(ParameterizedType type, Map<String, Type<?>> actualTypeArguments) {
        @SuppressWarnings("unchecked")
        Class<T> rawType = (Class<T>) type.getRawType();
        return new Type<T>(rawType, types(type.getActualTypeArguments(), actualTypeArguments));
    }

    private static Type<?>[] types(java.lang.reflect.Type[] parameters, Map<String, Type<?>> actualTypeArguments) {
        Type<?>[] args = new Type<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            args[i] = type(parameters[i], actualTypeArguments);
        }
        return args;
    }

    @Override
    public boolean isAssignableTo(Type<?> other) {
        if (!other.rawType.isAssignableFrom(rawType)) {
            return false;
        }
        if (!isParameterized() || other.isRawType()) {
            return true;
        }
        if (other.rawType == rawType) {
            return allParametersAreAssignableTo(other);
        }
        @SuppressWarnings("unchecked")
        Class<? super T> commonRawType = (Class<? super T>) other.getRawType();
        Type<?> asOther = supertype(commonRawType, this);
        return asOther.allParametersAreAssignableTo(other);
    }

    public boolean isRawType() {
        return !isParameterized() && rawType.getTypeParameters().length > 0;
    }

    private boolean allParametersAreAssignableTo(Type<?> other) {
        for (int i = 0; i < params.length; i++) {
            if (!params[i].asParameterAssignableTo(other.params[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean asParameterAssignableTo(Type<?> other) {
        if (rawType == other.rawType) {
            return !isParameterized() || allParametersAreAssignableTo(other);
        }
        return other.isLowerBound() && isAssignableTo(other.asExactType());
    }

    private Type<? extends T> asExactType() {
        return new Type<T>(false, rawType, params);
    }

    public Class<T> getRawType() {
        return rawType;
    }

    @SuppressWarnings("unchecked")
    public static <S> Type<? extends S> supertype(Class<S> supertype, Type<? extends S> type) {
        if (supertype.getTypeParameters().length == 0) {
            return raw(supertype);
        }
        for (Type<?> s : type.supertypes()) {
            if (s.getRawType() == supertype) {
                return (Type<? extends S>) s;
            }
        }
        throw new IllegalArgumentException("`" + supertype + "` is not a supertype of: `" + type + "`");
    }

    @SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
    public Type<? super T>[] supertypes() {
        Set<Type<?>> res = new LinkedHashSet<Type<?>>();
        Class<?> supertype = rawType;
        java.lang.reflect.Type genericSupertype = null;
        Type<?> type = this;
        Map<String, Type<?>> actualTypeArguments = actualTypeArguments(type);
        if (isInterface()) {
            res.add(OBJECT);
        }
        while (supertype != null) {
            if (genericSupertype != null) {
                type = type(genericSupertype, actualTypeArguments);
                res.add(type);
            }
            actualTypeArguments = actualTypeArguments(type);
            addSuperInterfaces(res, supertype, actualTypeArguments);
            genericSupertype = supertype.getGenericSuperclass();
            supertype = supertype.getSuperclass();
        }
        return (Type<? super T>[]) res.toArray(new Type<?>[res.size()]);
    }

    public boolean isInterface() {
        return rawType.isInterface();
    }

    private static <V> Map<String, Type<?>> actualTypeArguments(Type<V> type) {
        Map<String, Type<?>> actualTypeArguments = new HashMap<String, Type<?>>();
        TypeVariable<Class<V>>[] typeParameters = type.rawType.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            actualTypeArguments.put(typeParameters[i].getName(), type.parameter(i));
        }
        return actualTypeArguments;
    }

    public Type<?> parameter(int index) {
        if (index < 0 || index >= rawType.getTypeParameters().length) {
            throw new IndexOutOfBoundsException("The type " + this + " has no type parameter at index: " + index);
        }
        return isRawType() ? WILDCARD : params[index];
    }

    private void addSuperInterfaces(Set<Type<?>> res, Class<?> type,
                                    Map<String, Type<?>> actualTypeArguments) {
        Class<?>[] interfaces = type.getInterfaces();
        java.lang.reflect.Type[] genericInterfaces = type.getGenericInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Type<?> interfaceType = Type.type(genericInterfaces[i], actualTypeArguments);
            if (!res.contains(interfaceType)) {
                res.add(interfaceType);
                addSuperInterfaces(res, interfaces[i], actualTypeArguments(interfaceType));
            }
        }
    }

    public Type<?> elementType() {
        Type<?> elemRawType = elementRawType();
        return elemRawType == this ? this : elemRawType.parameterized(params);
    }

    private Type<?> elementRawType() {
        return asElementRawType(rawType.getComponentType());
    }

    private <E> Type<?> asElementRawType(Class<E> elementType) {
        return rawType.isArray() ? new Type<E>(lowerBound, elementType, params) : this;
    }

    @SuppressWarnings("unchecked")
    public Type<T[]> getArrayType() {
        Object proto = Array.newInstance(rawType, 0);
        return new Type<T[]>(lowerBound, (Class<T[]>) proto.getClass(), params);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(rawType.getModifiers());
    }

    public boolean isUnidimensionalArray() {
        return rawType.isArray() && !rawType.getComponentType().isArray();
    }
}


