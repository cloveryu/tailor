package com.github.tailor.inject.bind;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.bootstrap.Bootstrap;
import org.hamcrest.Factory;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.github.tailor.inject.DIRuntimeException.NoSuchResourceException;
import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.bootstrap.Inspect.all;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 10:36 PM
 */
public class InspectorBindsTest {

    @Target({METHOD, PARAMETER})
    @Retention(RUNTIME)
    public static @interface Resource {
        String value();
    }

    static final StringBuffer STATE = new StringBuffer();

    static class InspectorBindsModule extends BinderModule {

        @Override
        protected void declare() {
            bind(all().methods()).inModule();
            bind(all().methods().annotatedWith(Factory.class).namedBy(Resource.class)).in(InspectorBindsImplementor1.class);
            bind(all().methods()).in( new InspectorBindsImplementor2( STATE ) );
        }

        static int staticFactoryMethod() {
            return 42;
        }

        static long staticFactoryMethodWithParameters(int factor) {
            return factor * 2L;
        }
    }

    static class InspectorBindsImplementor1 {

        @Factory
        float instanceFactoryMethod() {
            return 42f;
        }

        @Factory
        @Resource("twentyone")
        float instanceFactoryMethodWithName() {
            return 21f;
        }

        @Factory
        @Resource("Foo")
        double instanceFactoryMethodWithParameters(@Resource("twentyone") float factor) {
            return factor * 2d;
        }

        float shouldNotBeBoundSinceItIsNotAnnotated() {
            return 0f;
        }
    }

    static class InspectorBindsImplementor2 {

        final StringBuffer state;

        InspectorBindsImplementor2(StringBuffer state) {
            this.state = state;
        }

        StringBuffer valueFromStatefullInspectedObject() {
            return state;
        }
    }

    private final Injector injector = Bootstrap.injector(InspectorBindsModule.class);

    @Test
    public void thatInstanceFactoryMethodIsAvailable() {
        assertEquals( 42f, injector.resolve( dependency( float.class ) ).floatValue(), 0.01f );
    }

    @Test
    public void thatStaticFactoryMethodIsAvailable() {
        assertEquals( 42, injector.resolve( dependency( int.class ) ).intValue() );
    }

    @Test
    public void thatInstanceFactoryMethodWithParametersIsAvailable() {
        assertEquals( 42d, injector.resolve( dependency( double.class ) ).doubleValue(), 0.01d );
    }

    @Test
    public void thatStaticFactoryMethodWithParametersIsAvailable() {
        assertEquals( 84L, injector.resolve( dependency( long.class ) ).longValue() );
    }

    @Test
    public void thatNamedWithAnnotationCanBeUsedToGetNamedResources() {
        assertEquals( 42d, injector.resolve(dependency( double.class ).named( "foo" ) ).doubleValue(), 0.01d );
    }

    @Test ( expected = NoSuchResourceException.class )
    public void thatNoMethodsAreBoundThatAreNotAssignableToSpecifiedType() {
        injector.resolve( dependency( String.class ) );
    }

    @Test
    public void thatMethodsAreBoundToSpecificInstance() {
        assertSame( STATE, injector.resolve( dependency( StringBuffer.class ) ) );
    }

}
