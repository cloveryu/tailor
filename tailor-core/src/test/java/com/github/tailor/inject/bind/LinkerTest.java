package com.github.tailor.inject.bind;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.bootstrap.Bootstrap;
import com.github.tailor.inject.bootstrap.BootstrapperBundle;
import org.junit.Test;

import static com.github.tailor.inject.Dependency.dependency;
import static org.junit.Assert.assertEquals;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 8:01 PM
 */
public class LinkerTest {

    static class TwiceInstalledModule extends BinderModule {

        @Override
        protected void declare() {
            bind(Integer.class).to(42);
        }

    }

    private static class LinkerBundle extends BootstrapperBundle {

        @Override
        protected void bootstrap() {
            install(new TwiceInstalledModule());
        }

    }

    @Test
    public void thatMonomodalModulesCanBeInstalledTwice() {
        Injector injector = Bootstrap.injector(LinkerBundle.class);
        assertEquals(42, injector.resolve(dependency(Integer.class)).intValue());
    }
}
