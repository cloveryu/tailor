package com.github.tailor.inject;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertTrue;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 10:13 AM
 */
public class NameTest {

    @Test
    public void shouldEqualToTheExactSameName() {
        assertTrue( Name.named( "foo" ).equalTo( Name.named( "foo" ) ) );
    }

}
