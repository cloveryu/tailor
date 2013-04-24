package com.github.tailor.inject;

import org.junit.Test;

import java.util.List;

import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.Target.targeting;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 12:00 AM
 */
public class TargetTest {

    @Test
    public void shouldBeMatchedByDependenciesGivenTargetInstances() {
        Target target = targeting(List.class);
        Dependency<String> dependency = dependency(String.class);
        assertFalse(target.isApplicableFor(dependency));
        assertTrue(target.isAccessibleFor(dependency.injectingInto(List.class)));
    }

}
