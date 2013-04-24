package com.github.tailor.inject;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

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
        assertTrue( named("foo").equalTo(named("foo")) );
    }

    @Test
    public void shouldNotEqualToTheDifferentName() {
        assertFalse( named("foo").equalTo(named("bar")) );
    }

}
