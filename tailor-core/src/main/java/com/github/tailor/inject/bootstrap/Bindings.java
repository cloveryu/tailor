package com.github.tailor.inject.bootstrap;

import com.github.tailor.inject.Resource;
import com.github.tailor.inject.Scope;
import com.github.tailor.inject.Source;
import com.github.tailor.inject.Supplier;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:20 PM
 */
public interface Bindings {

    <T> void add(Resource<T> resource, Supplier<? extends T> supplier, Scope scope, Source source);

}
