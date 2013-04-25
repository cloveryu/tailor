package com.github.tailor.inject.bind;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.Name;
import com.github.tailor.inject.bootstrap.Bootstrap;
import org.junit.Test;

import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.util.Scoped.TARGET_INSTANCE;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 8:49 PM
 */
public class RobotLegsProblemBindsTest {

    private static class Foot {
    }

    private static class Leg {

        final Foot foot;

        @SuppressWarnings("unused")
        Leg(Foot foot) {
            this.foot = foot;

        }
    }

    static Name left = Name.named("left");
    static Name right = Name.named("right");

    private static class RobotLegsProblemBindsModule extends BinderModule {

        @Override
        protected void declare() {
            per(TARGET_INSTANCE).construct(Foot.class);
            bind(left, Leg.class).toConstructor();
            bind(right, Leg.class).toConstructor();
        }
    }

    @Test
    public void thatRobotHasDifferentLegsWhenUsingInjectingIntoClause() {
        assertRobotHasDifferentLegsWithDifferentFoots(Bootstrap.injector(RobotLegsProblemBindsModule.class));
    }

    private static void assertRobotHasDifferentLegsWithDifferentFoots(Injector injector) {
        Leg leftLeg = injector.resolve(dependency(Leg.class).named(left));
        Leg rightLeg = injector.resolve(dependency(Leg.class).named(right));
        assertThat("same leg", leftLeg, not(sameInstance(rightLeg)));
        assertThat("same foot", leftLeg.foot, not(sameInstance(rightLeg.foot)));
    }
}
