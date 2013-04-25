package com.github.tailor.inject.bind;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.Type;
import com.github.tailor.inject.bootstrap.Bootstrap;
import com.github.tailor.inject.bootstrap.BootstrapperBundle;
import org.junit.Test;

import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.Type.raw;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 4:21 PM
 */
public class InstanceTest {

    private static class InstanceBindsModule1 extends BinderModule {

        @Override
        protected void declare() {
            bind( CharSequence.class ).to( "bar" );
            bind( Integer.class ).to( 42 );
        }

    }

    private static class InstanceBindsModule2 extends BinderModule {

        @Override
        protected void declare() {
            bind( String.class ).to( "foobar" );
            bind( Float.class ).to( 42.0f );
        }

    }

    private static class InstanceBindsBundle extends BootstrapperBundle {

        @Override
        protected void bootstrap() {
            install( InstanceBindsModule1.class );
            install( InstanceBindsModule2.class );
        }

    }

    private final Injector injector = Bootstrap.injector(InstanceBindsBundle.class);

    @Test
    public void shouldBeInjectedBasedOnTheDependencyType() {
        assertInjects( "bar", raw( CharSequence.class ) );
        assertInjects( "foobar", raw( String.class ) );
        assertInjects( 42, raw( Integer.class ) );
    }

    private <T> void assertInjects( T expected, Type<? extends T> dependencyType ) {
        assertThat( injector.resolve( dependency( dependencyType ) ), is( expected ) );
    }
}
