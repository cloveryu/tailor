package com.github.tailor.inject;

import org.junit.Test;

import java.text.Format;
import java.text.spi.DateFormatProvider;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.tailor.inject.Packages.packageOf;
import static com.github.tailor.inject.Type.raw;
import static org.junit.Assert.*;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 11:31 PM
 */
public class PackagesTest {

    @Test
    public void shouldNotInPackageJavaLangGivenLowerBoundType() {
        Packages javaLang = packageOf(String.class);
        assertFalse(javaLang.contains(Type.WILDCARD));
        assertFalse(javaLang.contains(raw(List.class).asLowerBound()));
    }

    @Test
    public void shouldContainsAllTypesGivenPackageAll() {
        Packages all = Packages.ALL;
        assertTrue(all.contains(raw(AtomicBoolean.class)));
        assertTrue(all.contains(raw(List.class).asLowerBound()));
    }

    @Test
    public void shouldContainsTypeGivenTheExactPackage() {
        Packages javaUtil = packageOf(String.class);
        assertTrue(javaUtil.contains(raw(String.class)));
        assertFalse(javaUtil.contains(raw(ConcurrentLinkedQueue.class)));
    }

    @Test
    public void shouldContainTypeGivenTheExactSubpackages() {
        Packages javaUtil = Packages.subPackagesOf(List.class);
        assertFalse(javaUtil.contains(raw(List.class)));
        assertTrue(javaUtil.contains(raw(ConcurrentLinkedQueue.class)));
        assertTrue(javaUtil.contains(raw(AtomicBoolean.class)));
        assertFalse(javaUtil.contains(raw(Format.class)));
    }

    @Test
    public void shouldContainTypeGivenTheExactPackageAndSubpackages() {
        Packages javaUtil = Packages.packageAndSubPackagesOf(List.class);
        assertTrue(javaUtil.contains(raw(List.class)));
        assertTrue(javaUtil.contains(raw(ConcurrentLinkedQueue.class)));
        assertTrue(javaUtil.contains(raw(AtomicBoolean.class)));
        assertFalse(javaUtil.contains(raw(Format.class)));
    }

    @Test
    public void shouldBeCherryPickedGivenIndividualPackages() {
        Packages cherries = packageOf(List.class, AtomicBoolean.class, String.class);
        assertTrue(cherries.contains(raw(List.class)));
        assertTrue(cherries.contains(raw(AtomicBoolean.class)));
        assertTrue(cherries.contains(raw(String.class)));
        assertTrue(cherries.contains(raw(Long.class)));
        assertFalse(cherries.contains(raw(Format.class)));
    }

    @Test
    public void shouldBeCombinedGivenMultipleRootSubpackagesOfSameDepth() {
        Packages subs = Packages.subPackagesOf(List.class, Format.class);
        assertFalse(subs.contains(raw(List.class)));
        assertTrue(subs.contains(raw(AtomicBoolean.class)));
        assertFalse(subs.contains(raw(Format.class)));
        assertTrue(subs.contains(raw(DateFormatProvider.class)));
    }

    @Test
    public void shouldBeCombinedGivenMultipleRootPackagesAndSubpackagesOfSameDepth() {
        Packages subs = Packages.packageAndSubPackagesOf(List.class, Format.class);
        assertTrue(subs.contains(raw(List.class)));
        assertTrue(subs.contains(raw(AtomicBoolean.class)));
        assertTrue(subs.contains(raw(Format.class)));
        assertTrue(subs.contains(raw(DateFormatProvider.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeCombinedGivenMultipleRootSubpackagesOfDifferentDepth() {
        Packages.subPackagesOf(List.class, DateFormatProvider.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeCombinedAndExceptionGivenMultipleRootPackagesAndSubpackagesOfDifferentDepth() {
        Packages.packageAndSubPackagesOf(List.class, DateFormatProvider.class);
    }

    @Test
    public void shouldBeSameKindOfSetGivenParentPackages() {
        assertEquals(Packages.subPackagesOf(Map.class), Packages.subPackagesOf(ConcurrentMap.class).parents());
        assertEquals(packageOf(Map.class), packageOf(ConcurrentMap.class).parents());
        assertEquals(Packages.packageAndSubPackagesOf(Map.class), Packages.packageAndSubPackagesOf(ConcurrentMap.class).parents());
    }

    @Test
    public void shouldBeAllPackagesGivenParentOfAllPackages() {
        assertEquals(Packages.ALL, Packages.ALL.parents());
    }

    @Test
    public void shouldBeDefaultPackageGivenParentOfDefaultPackage() {
        assertEquals(Packages.DEFAULT, Packages.DEFAULT.parents());
    }
}
