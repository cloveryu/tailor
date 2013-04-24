package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:51 PM
 */
public final class Emergence<T> {

    public static <T> Emergence<T> emergence(Instance<T> instance, Expiry expiry) {
        return new Emergence<T>(instance, expiry);
    }

    private final Instance<T> instance;
    private final Expiry expiry;

    private Emergence(Instance<T> instance, Expiry expiry) {
        super();
        this.instance = instance;
        this.expiry = expiry;
    }

    public Instance<T> getInstance() {
        return instance;
    }

    public Expiry getExpiry() {
        return expiry;
    }

    @Override
    public String toString() {
        return expiry + " " + instance;
    }
}
