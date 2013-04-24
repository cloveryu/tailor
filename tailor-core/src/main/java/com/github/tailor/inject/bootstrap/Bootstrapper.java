package com.github.tailor.inject.bootstrap;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 4:35 PM
 */
public interface Bootstrapper {

    void install(Class<? extends Bundle> bundle);

    void uninstall(Class<? extends Bundle> bundle);

    void install(Module module);

}
