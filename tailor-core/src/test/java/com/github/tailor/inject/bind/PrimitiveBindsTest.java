package com.github.tailor.inject.bind;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.Name;
import com.github.tailor.inject.bootstrap.Bootstrap;
import org.junit.Test;

import static com.github.tailor.inject.DIRuntimeException.NoSuchResourceException;
import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.Name.named;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 1:58 PM
 */
public class PrimitiveBindsTest {

    private static class PrimitiveBindsModule extends BinderModule {

        @Override
        protected void declare() {
            bind(int.class).to(1);
            bind(named("pi"), float.class).to(3.1415f);
            bind(Circle.class).toConstructor();
        }

    }

    private static class Circle {

        final Integer r;
        final float pi;

        Circle(Integer r, float pi) {
            super();
            this.r = r;
            this.pi = pi;
        }

    }

    private final Injector injector = Bootstrap.injector(PrimitiveBindsModule.class);

    @Test
    public void shouldWorkAsWrapperClassGivenPrimitiveInt() {
        assertThat(injector.resolve(dependency(Integer.class)), is(1));
        assertThat(injector.resolve(dependency(int.class)), is(1));
    }

    @Test
    public void shouldWorkAsWrapperClassGivenPrimitiveFloat() {
        assertThat(injector.resolve(dependency(Float.class).named("pi")), is(3.1415f));
        assertThat(injector.resolve(dependency(float.class).named("pi")), is(3.1415f));
    }

    @Test
    public void shouldWorkAsWrapperClassGivenPrimitiveWhenInjected() {
        Circle circle = injector.resolve(dependency(Circle.class));
        assertThat(circle.r, is(1));
        assertThat(circle.pi, is(3.1415f));
    }

    @Test(expected = NoSuchResourceException.class)
    public void shouldThrowExceptionWhenResolvingAnUnboundDependency() {
        injector.resolve(dependency(String.class));
    }

    @Test(expected = NoSuchResourceException.class)
    public void shouldThrowExceptionWhenResolvingAnUnboundDependencyWithBoundRawType() {
        injector.resolve(dependency(float.class).named(Name.DEFAULT));
    }

}
