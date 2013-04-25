package com.github.tailor.inject.bind;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.Instance;
import com.github.tailor.inject.bootstrap.Bootstrap;
import com.github.tailor.inject.util.Argument;
import com.github.tailor.inject.util.Suppliable;
import org.junit.Test;

import java.io.Serializable;

import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.Instance.instance;
import static com.github.tailor.inject.Name.named;
import static com.github.tailor.inject.Type.raw;
import static com.github.tailor.inject.bootstrap.Bootstrap.suppliables;
import static com.github.tailor.inject.util.Argument.constant;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 12:00 PM
 */
public class ConstructorParameterBindsTest {

    private static class Foo {
        @SuppressWarnings("unused")
        Foo(String bar, Integer baz) {
        }
    }

    private static class Bar {
        final String foo;
        @SuppressWarnings("unused")
        Bar(String foo, Integer baz) {
            this.foo = foo;
        }
    }

    private static class Baz {
        final String foo;
        final String bar;
        @SuppressWarnings("unused")
        Baz(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    private static class Qux {
        final Serializable value;
        final CharSequence sequence;
        @SuppressWarnings("unused")
        Qux(Serializable value, CharSequence sequence) {
            this.value = value;
            this.sequence = sequence;
        }
    }

    private static class ParameterConstructorBindsModule extends BinderModule {
        @Override
        protected void declare() {
            Instance<String> y = instance(named("y"), raw(String.class));
            bind(String.class).to("should not be resolved");
            bind(named("x"), String.class).to("x");
            bind(y).to("y");
            bind(Integer.class).to(42);
            bind(Foo.class).toConstructor(raw(String.class));
            bind(Bar.class).toConstructor(raw(Integer.class), y);
            bind(Baz.class).toConstructor(y, y);
            bind(Qux.class).toConstructor(Argument.asType(CharSequence.class, y), constant(Number.class, 1987));
        }
    }

    private static class ConstantParameterConstructorBindsModule extends BinderModule {
        @Override
        protected void declare() {
            bind(named("const"), Baz.class).toConstructor(constant(String.class, "1st"), constant(String.class, "2nd"));
        }
    }

    private static class WrongParameterConstructorBindsModule extends BinderModule {
        @Override
        protected void declare() {
            bind(Bar.class).toConstructor(raw(Float.class));
        }
    }

    private final Injector injector = Bootstrap.injector(ParameterConstructorBindsModule.class);

    @Test
    public void shouldBeUnderstoodGivenClassParameter() {
        assertThat(injector.resolve(dependency(Foo.class)), notNullValue());
    }

    @Test
    public void shouldBeUnderstoodGivenTypeParameter() {
        assertThat(injector.resolve(dependency(Bar.class)), notNullValue());
    }

    @Test
    public void shouldBeUnderstoodGivenInstanceParameter() {
        Bar bar = injector.resolve(dependency(Bar.class));
        assertThat(bar.foo, is("y"));
    }

    @Test
    public void shouldBeUnderstoodGivenParameterAsAnotherType() {
        Qux qux = injector.resolve(dependency(Qux.class));
        assertEquals("y", qux.sequence);
    }

    @Test
    public void shouldBeUnderstoodGivenConstantParameter() {
        Qux qux = injector.resolve(dependency(Qux.class));
        assertEquals(1987, qux.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotUnderstoodAndThrowsExceptionGivenParameters() {
        Bootstrap.injector(WrongParameterConstructorBindsModule.class);
    }

    @Test
    public void shouldBeUsedAsConstructorArgumentsWhenPossibleGivenConstants() {
        Suppliable<?>[] suppliables = suppliables(ConstantParameterConstructorBindsModule.class);
        assertThat(suppliables.length, is(1));
        assertTrue(suppliables[0].supplier.getClass().getSimpleName().contains("Static"));
    }

}
