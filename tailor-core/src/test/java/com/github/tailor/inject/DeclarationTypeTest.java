package com.github.tailor.inject;

import org.junit.Test;

import static com.github.tailor.inject.DeclarationType.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 11:16 PM
 */
public class DeclarationTypeTest {

    @Test
    public void shouldNotReplacedByExplicitGivenExplicit() {
        assertFalse(EXPLICIT.replacedBy(EXPLICIT));
    }

    @Test
    public void shouldClashesWithExplicitGivenExplicit() {
        assertTrue(EXPLICIT.clashesWith(EXPLICIT));
    }

    @Test
    public void shouldClashesWithMultipleGivenExplicit() {
        assertTrue(EXPLICIT.clashesWith(MULTI));
        assertTrue(MULTI.clashesWith(EXPLICIT));
    }

    @Test
    public void shouldBeReplacedByMultiGivenImplicit() {
        assertTrue(IMPLICIT.replacedBy(MULTI));
    }

    @Test
    public void shouldBeReplacedByMultiGivenDefault() {
        assertTrue(DEFAULT.replacedBy(MULTI));
    }

    @Test
    public void shouldBeReplacedByMultiGivenAuto() {
        assertTrue(AUTO.replacedBy(MULTI));
    }

    @Test
    public void shouldReplacedByAutoGivenDefault() {
        assertTrue(DEFAULT.replacedBy(AUTO));
    }

    @Test
    public void shouldReplacedByMultiGivenDefault() {
        assertTrue(DEFAULT.replacedBy(MULTI));
    }

    @Test
    public void shouldReplacedByExplicitGivenDefault() {
        assertTrue(DEFAULT.replacedBy(EXPLICIT));
    }

    @Test
    public void shouldNotReplacedByExplicitGivenRequired() {
        assertFalse(REQUIRED.replacedBy(EXPLICIT));
    }

    @Test
    public void shouldNotClashesWithRequiredGivenRequired() {
        assertFalse(REQUIRED.clashesWith(REQUIRED));
    }

    @Test
    public void shouldClashesWithDefaultGivenDefault() {
        assertTrue(DEFAULT.clashesWith(DEFAULT));
    }

    @Test
    public void shouldNotClashesWithAutoGivenAuto() {
        assertFalse(AUTO.clashesWith(AUTO));
    }

    @Test
    public void shouldReplacedByImplicitGivenImplicit() {
        assertTrue(IMPLICIT.replacedBy(IMPLICIT));
    }
}
