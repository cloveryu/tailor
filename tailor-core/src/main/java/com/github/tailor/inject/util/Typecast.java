package com.github.tailor.inject.util;

import com.github.tailor.inject.Type;

import java.util.List;
import java.util.Set;

import static com.github.tailor.inject.Type.raw;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 8:17 PM
 */
public final class Typecast {

    public static <T> Type<List<T>> listTypeOf( Class<T> elementType ) {
        return listTypeOf( raw( elementType ) );
    }

    @SuppressWarnings ( { "unchecked", "rawtypes" } )
    public static <T> Type<List<T>> listTypeOf( Type<T> elementType ) {
        Type raw = raw( List.class ).parameterized(elementType);
        return raw;
    }

    public static <T> Type<Set<T>> setTypeOf( Class<T> elementType ) {
        return setTypeOf( raw( elementType ) );
    }

    @SuppressWarnings ( { "unchecked", "rawtypes" } )
    public static <T> Type<Set<T>> setTypeOf( Type<T> elementType ) {
        Type raw = raw( Set.class ).parameterized( elementType );
        return raw;
    }
}
