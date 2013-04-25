package com.github.tailor.inject;

import com.github.tailor.inject.bind.ConstructorParameterBindsTest;
import com.github.tailor.inject.bind.PrimitiveBindsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 12:07 AM
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DeclarationTypeTest.class, NameTest.class, PackagesTest.class, TargetTest.class, TypeTest.class,
        PrimitiveBindsTest.class, ConstructorParameterBindsTest.class })
public class TailorSuite {
}
