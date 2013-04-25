package com.github.tailor.inject.bootstrap;

import com.github.tailor.inject.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.tailor.inject.bootstrap.Link.ListBindings;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 5:23 PM
 */
public final class Binding<T> {

    public final Resource<T> resource;
    public final Supplier<? extends T> supplier;
    public final Scope scope;
    public final Source source;

    Binding(Resource<T> resource, Supplier<? extends T> supplier, Scope scope, Source source) {
        super();
        this.resource = resource;
        this.supplier = supplier;
        this.scope = scope;
        this.source = source;
    }

    @Override
    public String toString() {
        return resource + " / " + scope + " / " + source;
    }

    public static Binding<?>[] disambiguate(Binding<?>[] bindings) {
        if (bindings.length <= 1) {
            return bindings;
        }
        List<Binding<?>> uniques = new ArrayList<Binding<?>>(bindings.length);
        // should sort uniques
        uniques.add(bindings[0]);
        int lastDistinctIndex = 0;
        for (int i = 1; i < bindings.length; i++) {
            Binding<?> one = bindings[lastDistinctIndex];
            Binding<?> other = bindings[i];
            final boolean equalResource = one.resource.equalTo(other.resource);
            DeclarationType oneType = one.source.getType();
            DeclarationType otherType = other.source.getType();
            if (equalResource && oneType.clashesWith(otherType)) {
                throw new IllegalStateException("Duplicate binds:\n" + one + "\n" + other);
            }
            if (equalResource && oneType.nullifiedBy(otherType)) {
                if (i - 1 == lastDistinctIndex) {
                    uniques.remove(uniques.size() - 1);
                }
            } else if (!equalResource || !otherType.replacedBy(oneType)) {
                uniques.add(other);
                lastDistinctIndex = i;
            }
        }
        return Array.of(uniques, Binding.class);
    }

    public static Binding<?>[] expand(Inspector inspector, Module... modules) {
        Set<Class<?>> declared = new HashSet<Class<?>>();
        ListBindings bindings = new ListBindings();
        for (Module m : modules) {
            Class<? extends Module> ns = m.getClass();
            final boolean hasBeenDeclared = declared.contains(ns);
            if (!hasBeenDeclared) {
                m.declare(bindings, inspector);
                declared.add(ns);
            }
        }
        return Array.of(bindings.list, Binding.class);
    }
}
