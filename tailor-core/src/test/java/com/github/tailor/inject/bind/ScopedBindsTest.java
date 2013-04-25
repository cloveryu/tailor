package com.github.tailor.inject.bind;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.bootstrap.Bootstrap;
import com.github.tailor.inject.util.Scoped;
import org.junit.Test;

import static com.github.tailor.inject.DIRuntimeException.MoreFrequentExpiryException;
import static com.github.tailor.inject.Dependency.dependency;
import static org.junit.Assert.fail;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 9:10 PM
 */
public class ScopedBindsTest {

    private static class Foo {

        @SuppressWarnings("unused")
        Foo(Bar bar) {
        }
    }

    private static class Bar {
    }

    private static class ScopedBindsModule extends BinderModule {

        @Override
        protected void declare() {
            per(Scoped.APPLICATION).construct(Foo.class);
            per(Scoped.INJECTION).construct(Bar.class);
        }
    }

    @Test(expected = MoreFrequentExpiryException.class)
    public void thatInjectingAnInjectionScopedInstanceIntoAppScopedInstanceThrowsAnException() {
        Injector injector = Bootstrap.injector(ScopedBindsModule.class);
        Foo foo = injector.resolve(dependency(Foo.class));
        fail("It should not be possible to create a foo but got one: " + foo);
    }
}
