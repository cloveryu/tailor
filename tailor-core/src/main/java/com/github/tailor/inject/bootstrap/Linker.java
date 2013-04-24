package com.github.tailor.inject.bootstrap;

import com.github.tailor.inject.Inspector;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 5:18 PM
 */
public interface Linker<T> {

    T[] link(Inspector inspector, Module... modules);
}
