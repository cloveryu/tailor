package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 12:04 PM
 */
public interface Parameter<T> {

    boolean isAssignableTo( Type<?> type );

}
