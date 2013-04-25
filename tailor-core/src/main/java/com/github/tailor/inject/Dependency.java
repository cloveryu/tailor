package com.github.tailor.inject;

import java.util.Arrays;
import java.util.Iterator;

import static com.github.tailor.inject.DIRuntimeException.DependencyCycleException;
import static com.github.tailor.inject.DIRuntimeException.MoreFrequentExpiryException;
import static com.github.tailor.inject.Emergence.emergence;
import static com.github.tailor.inject.Instance.defaultInstanceOf;
import static com.github.tailor.inject.Instance.instance;
import static com.github.tailor.inject.Type.raw;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:07 PM
 */
public final class Dependency<T> implements Parameter, Iterable<Injection> {

    private static final Injection[] UNTARGETED = new Injection[0];

    public Dependency<T> named(String name) {
        return named(Name.named(name));
    }

    public Dependency<T> named(Name name) {
        return dependency(instance(name, getType()), hierarchy);
    }

    public static <T> Dependency<T> dependency(Class<T> type) {
        return dependency(raw(type));
    }

    public static <T> Dependency<T> dependency(Type<T> type) {
        return dependency(type, UNTARGETED);
    }

    public <E> Dependency<E> instanced(Instance<E> instance) {
        return dependency(instance, hierarchy);
    }

    private static <T> Dependency<T> dependency(Instance<T> instance, Injection[] hierarchy) {
        return new Dependency<T>(instance, hierarchy);
    }

    public static <T> Dependency<T> dependency(Instance<T> instance) {
        return dependency(instance, UNTARGETED);
    }

    private final Injection[] hierarchy;
    private final Instance<T> instance;

    private Dependency(Instance<T> instance, Injection... hierarchy) {
        this.instance = instance;
        this.hierarchy = hierarchy;
    }

    @Override
    public Iterator<Injection> iterator() {
        return Arrays.asList(hierarchy).iterator();
    }

    public Instance<T> getInstance() {
        return instance;
    }

    public Type<T> getType() {
        return instance.getType();
    }

    @Override
    public boolean isAssignableTo(Type<?> type) {
        return getType().isAssignableTo(type);
    }

    private static <T> Dependency<T> dependency(Type<T> type, Injection[] hierarchy) {
        return dependency(instance(Name.ANY, type), hierarchy);
    }

    public Name getName() {
        return instance.getName();
    }

    public Instance<?> target() {
        return target(0);
    }

    public Instance<?> target(int level) {
        return isUntargeted() ? Instance.ANY : hierarchy[hierarchy.length - 1 - level].getTarget().getInstance();
    }

    public boolean isUntargeted() {
        return hierarchy.length == 0;
    }

    public int injectionDepth() {
        return hierarchy.length;
    }

    public Dependency<T> injectingInto(Class<?> target) {
        return injectingInto(raw(target));
    }

    public Dependency<T> injectingInto(Type<?> target) {
        return injectingInto(defaultInstanceOf(target));
    }

    public Dependency<T> injectingInto(Instance<?> target) {
        return injectingInto(emergence(target, Expiry.NEVER));
    }

    public Dependency<T> injectingInto(Emergence<?> target) {
        Injection injection = new Injection(instance, target);
        if (hierarchy.length == 0) {
            return new Dependency<T>(instance, injection);
        }
        ensureNotMoreFrequentExpiry(injection);
        ensureNoCycle(injection);
        return new Dependency<T>(instance, Array.append(hierarchy, injection));
    }

    private void ensureNoCycle(Injection injection) throws DependencyCycleException {
        for (Injection parent : hierarchy) {
            if (parent.equalTo(injection)) {
                throw new DependencyCycleException(this, injection.getTarget().getInstance());
            }
        }
    }

    private void ensureNotMoreFrequentExpiry(Injection injection) {
//        final Expiry expiry = injection.getTarget().getExpiry();
//        for (Injection parent : hierarchy) {
//            if (expiry.moreFrequent(parent.getTarget().getExpiry())) {
//                throw new MoreFrequentExpiryException(parent, injection);
//            }
//        }
    }
}
