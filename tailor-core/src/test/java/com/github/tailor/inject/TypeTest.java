package com.github.tailor.inject;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.github.tailor.inject.Type.fieldType;
import static com.github.tailor.inject.Type.raw;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 10:53 AM
 */
public class TypeTest {

    @SuppressWarnings ( "unused" )
    private Set<String> aStringSet;

    @Test
    public void shouldReturnCorrectTypeName() throws Exception {
        Type<List> listType = raw(List.class).parameterized( String.class );
        assertThat(listType.toString(), is("java.util.List<java.lang.String>"));

        listType = raw(List.class).parameterized(raw(String.class).asLowerBound());
        assertThat( listType.toString(), is( "java.util.List<? extends java.lang.String>" ) );

        Field stringSet = TypeTest.class.getDeclaredField( "aStringSet" );
        assertThat( fieldType(stringSet).toString(), is("java.util.Set<java.lang.String>" ) );
    }

    @Test
    public void shouldBeAssignableToNumberGivenInteger() {
        Type<Integer> integer = raw( Integer.class );
        Type<Number> number = raw( Number.class );
        assertTrue(integer.isAssignableTo(number));
    }

    @Test
    public void shouldNotBeAssignableToIntegerGivenNumber() {
        Type<Integer> integer = raw( Integer.class );
        Type<Number> number = raw( Number.class );
        assertFalse( number.isAssignableTo( integer ) );
    }

    @Test
    public void shouldNotBeAssignableBetweenGenericWithExactRawType() {
        Type<List> listOfIntegers = raw( List.class ).parameterized(Integer.class);
        Type<List> listOfNumbers = raw( List.class ).parameterized(Number.class);
        assertFalse( listOfIntegers.isAssignableTo( listOfNumbers ) );
        assertFalse( listOfNumbers.isAssignableTo( listOfIntegers ) );
    }

    @Test
    public void shouldReturnCorrectTypeNameGivenGenericArrays() {
        Type<Class[]> classArray = raw( Class[].class ).parameterized( String.class );
        assertThat( classArray.elementType().toString(), is( "java.lang.Class<java.lang.String>" ) );
        assertThat( classArray.toString(), is( "java.lang.Class<java.lang.String>[]" ) );
    }

    @Test
    public void shouldReturnCorrectTypeNameGivenArrayType() {
        Type<? extends Number> type = raw( Number.class ).asLowerBound();
        assertThat( type.getArrayType().toString(), is( "? extends java.lang.Number[]" ) );
    }

    @Test
    public void shouldContainsGenericSuperInterfacesAsSupertype() {
        Type<? super Integer>[] integerSupertypes = Type.raw( Integer.class ).supertypes();
        assertContains( integerSupertypes, Type.raw(Comparable.class ).parameterized(Integer.class) );
    }

    private static void assertContains( Type<?>[] actual, Type<?> expected ) {
        for ( Type<?> type : actual ) {
            if ( type.equalTo( expected ) ) {
                return;
            }
        }
        fail( Arrays.toString( actual ) + " should have contained: " + expected );
    }

}
