package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:27 PM
 */
public interface Injectable<T> {

    T instanceFor(Demand<T> demand);
}
