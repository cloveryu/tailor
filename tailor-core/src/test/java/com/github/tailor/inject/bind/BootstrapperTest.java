package com.github.tailor.inject.bind;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.bootstrap.Bootstrap;
import org.junit.Test;

import static com.github.tailor.inject.DIRuntimeException.DependencyCycleException;
import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.Type.raw;
import static org.junit.Assert.fail;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 7:15 PM
 */
public class BootstrapperTest {

    private static class ClashingBindsModule extends BinderModule {

        @Override
        protected void declare() {
            bind(Integer.class).to(42);
            bind(Integer.class).to(8);
        }

    }

    @SuppressWarnings("unused")
    private static class Foo {

        Foo(Bar bar) {
        }
    }

    @SuppressWarnings("unused")
    private static class Bar {

        public Bar(Foo foo) {
        }
    }

    @SuppressWarnings("unused")
    private static class A {

        A(B b) {
        }
    }

    @SuppressWarnings("unused")
    private static class B {

        B(C c) {
        }
    }

    @SuppressWarnings("unused")
    private static class C {

        C(A a) {
        }
    }

    private static class CyclicBindsModule extends BinderModule {

        @Override
        protected void declare() {
            bind(Foo.class).toConstructor(raw(Bar.class));
            bind(Bar.class).toConstructor(raw(Foo.class));
        }

    }

    private static class CircularBindsModule extends BinderModule {

        @Override
        protected void declare() {
            bind(A.class).toConstructor(raw(B.class));
            bind(B.class).toConstructor(raw(C.class));
            bind(C.class).toConstructor(raw(A.class));
        }

    }

    @Test(expected = IllegalStateException.class)
    public void thatNonUniqueResourcesThrowAnException() {
        Bootstrap.injector(ClashingBindsModule.class);
    }

    @Test(expected = DependencyCycleException.class)
    public void thatDependencyCyclesAreDetected() {
        Injector injector = Bootstrap.injector(CyclicBindsModule.class);
        Foo foo = injector.resolve(dependency(Foo.class));
        fail("foo should not be resolvable but was: " + foo);
    }

    @Test(expected = DependencyCycleException.class)
    public void thatDependencyCyclesInCirclesAreDetected() {
        Injector injector = Bootstrap.injector(CircularBindsModule.class);
        A a = injector.resolve(dependency(A.class));
        fail("A should not be resolvable but was: " + a);
    }

}
