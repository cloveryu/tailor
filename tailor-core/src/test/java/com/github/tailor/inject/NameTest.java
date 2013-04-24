package com.github.tailor.inject;

import org.junit.Test;

import static com.github.tailor.inject.Name.named;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 10:13 AM
 */
public class NameTest {

    @Test
    public void shouldEqualToTheExactSameName() {
        assertTrue(named("foo").equalTo(named("foo")));
    }

    @Test
    public void shouldNotEqualToTheDifferentName() {
        assertFalse(named("foo").equalTo(named("bar")));
    }

    @Test
    public void shouldBeApplicableToAnyNameGivenWildcard() {
        assertTrue(named("foo").isApplicableFor(Name.ANY));
    }

    @Test
    public void shouldBeApplicableGivenExactSameNameFollowedByWildcard() {
        assertTrue(named("foo").isApplicableFor(named("foo*")));
    }

    @Test
    public void shouldBeApplicableGivenFirstLetterFollowedByWildcard() {
        assertTrue(named("foo").isApplicableFor(named("f*")));
    }

    @Test
    public void shouldBeApplicableGivenStartOfNameFollowedByWildcard() {
        assertTrue(named("foo").isApplicableFor(named("fo*")));
    }

    @Test
    public void shouldBeApplicableToAnyNameGivenDefaultName() {
        assertTrue(Name.DEFAULT.isApplicableFor(Name.ANY));
    }

    @Test
    public void shouldBeApplicableToDefaultNameGivenAnyName() {
        assertTrue(Name.ANY.isApplicableFor(Name.DEFAULT));
    }

    @Test
    public void shouldBeApplicableToWhateverNameGivenAnyName() {
        assertTrue(Name.ANY.isApplicableFor(named("foo")));
    }

}
