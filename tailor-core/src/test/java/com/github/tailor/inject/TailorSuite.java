package com.github.tailor.inject;

import com.github.tailor.inject.bind.BindSuite;
import com.github.tailor.inject.demo.PizzaStoreTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 12:07 AM
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(value = {DeclarationTypeTest.class, NameTest.class, PackagesTest.class, TargetTest.class, TypeTest.class,
        BindSuite.class, PizzaStoreTest.class})
public class TailorSuite {
}

