package com.github.tailor.inject.bootstrap;

import com.github.tailor.inject.*;
import com.github.tailor.inject.util.Scoped;
import com.github.tailor.inject.util.Suppliable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 5:17 PM
 */
public final class Link {

    public static final Linker<Suppliable<?>> BUILDIN = linker(defaultExpiration());

    private static Linker<Suppliable<?>> linker(Map<Scope, Expiry> expiryByScope) {
        return new SuppliableLinker(expiryByScope);
    }

    private static IdentityHashMap<Scope, Expiry> defaultExpiration() {
        IdentityHashMap<Scope, Expiry> map = new IdentityHashMap<Scope, Expiry>();
        map.put(Scoped.APPLICATION, Expiry.NEVER);
        map.put(Scoped.INJECTION, Expiry.expires(1000));
        map.put(Scoped.DEPENDENCY_TYPE, Expiry.NEVER);
        map.put(Scoped.TARGET_INSTANCE, Expiry.NEVER);
        return map;
    }

    private static class SuppliableLinker implements Linker<Suppliable<?>> {

        private final Map<Scope, Expiry> expiryByScope;

        SuppliableLinker(Map<Scope, Expiry> expiryByScope) {
            super();
            this.expiryByScope = expiryByScope;
        }

        @Override
        public Suppliable<?>[] link(Inspector inspector, Module... modules) {
            return link(Binding.disambiguate(Binding.expand(inspector, modules)));
        }

        private Suppliable<?>[] link(Binding<?>[] bindings) {
            Map<Scope, Repository> repositories = initRepositories(bindings);
            Suppliable<?>[] suppliables = new Suppliable<?>[bindings.length];
            for (int i = 0; i < bindings.length; i++) {
                Binding<?> binding = bindings[i];
                Scope scope = binding.scope;
                Expiry expiry = expiryByScope.get(scope);
                if (expiry == null) {
                    expiry = Expiry.NEVER;
                }
                suppliables[i] = suppliableOf(binding, repositories.get(scope), expiry);
            }
            return suppliables;
        }

        private static <T> Suppliable<T> suppliableOf(Binding<T> binding, Repository repository, Expiry expiration) {
            return new Suppliable<T>(binding.resource, binding.supplier, repository, expiration, binding.source);
        }

        private static Map<Scope, Repository> initRepositories(Binding<?>[] bindings) {
            Map<Scope, Repository> repositories = new IdentityHashMap<Scope, Repository>();
            for (Binding<?> i : bindings) {
                Repository repository = repositories.get(i.scope);
                if (repository == null) {
                    repositories.put(i.scope, i.scope.init());
                }
            }
            return repositories;
        }

    }

    static class ListBindings implements Bindings {

        final List<Binding<?>> list = new ArrayList<Binding<?>>(100);

        ListBindings() {
        }

        @Override
        public <T> void add(Resource<T> resource, Supplier<? extends T> supplier, Scope scope, Source source) {
            list.add(new Binding<T>(resource, supplier, scope, source));
        }

    }
}
