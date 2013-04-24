package com.github.tailor.inject.bootstrap;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 4:48 PM
 */
public interface Modulariser {

    Module[] modularise(Class<? extends Bundle> root);
}
