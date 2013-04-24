package com.github.tailor.inject.util;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.Injectron;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 5:41 PM
 */
public interface InjectronSource {

    Injectron<?>[] exportTo(Injector injector);
}
