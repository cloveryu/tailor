package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:24 PM
 */
public interface Repository {

    <T> T serve(Demand<T> demand, Injectable<T> injectable);
}
