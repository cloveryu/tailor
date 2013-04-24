package com.github.tailor.inject.bootstrap;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 4:57 PM
 */
public interface Bundler {

    Class<? extends Bundle>[] bundle(Class<? extends Bundle> root);
}
