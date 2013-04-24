package com.github.tailor.inject;

import java.util.Arrays;
import java.util.Collection;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 5:14 PM
 */
public final class Array {

    public static <T> T[] append(T[] array, T value) {
        T[] copy = Arrays.copyOf(array, array.length + 1);
        copy[array.length] = value;
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newInstance(Class<T> componentType, int length) {
        return (T[]) java.lang.reflect.Array.newInstance(componentType, length);
    }

    public static <T> T[] of(Collection<? extends T> list, Class<T> type) {
        return list.toArray(newInstance(type, list.size()));
    }
}
