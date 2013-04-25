package com.github.tailor.inject.bind;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 12:46 PM
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({PrimitiveBindsTest.class, ConstructorParameterBindsTest.class, BootstrapperTest.class, LinkerTest.class,
        InstanceTest.class, RobotLegsProblemBindsTest.class, ScopedBindsTest.class, ScopesTest.class, InspectorBindsTest.class})
public class BindSuite {
}
