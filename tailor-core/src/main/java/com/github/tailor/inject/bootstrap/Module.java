package com.github.tailor.inject.bootstrap;

import com.github.tailor.inject.Inspector;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:10 PM
 */
public interface Module {

    void declare(Bindings bindings, Inspector inspector);

}
