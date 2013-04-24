package com.github.tailor.inject.util;

import com.github.tailor.inject.*;

import static com.github.tailor.inject.Demand.demand;
import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.util.Inject.asInjectable;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 5:19 PM
 */
public final class Suppliable<T> {

    public static InjectronSource source(Suppliable<?>[] suppliables) {
        return new SuppliableSource(suppliables);
    }

    public final Resource<T> resource;
    public final Supplier<? extends T> supplier;
    public final Repository repository;
    public final Source source;
    public final Expiry expiry;

    public Suppliable(Resource<T> resource, Supplier<? extends T> supplier, Repository repository, Expiry expiry, Source source) {
        super();
        this.resource = resource;
        this.supplier = supplier;
        this.repository = repository;
        this.expiry = expiry;
        this.source = source;
    }

    private static class SuppliableSource implements InjectronSource {

        private final Suppliable<?>[] suppliables;

        SuppliableSource(Suppliable<?>[] suppliables) {
            super();
            this.suppliables = suppliables;
        }

        @Override
        public Injectron<?>[] exportTo(Injector injector) {
            return injectrons(suppliables, injector);
        }

        public static Injectron<?>[] injectrons(Suppliable<?>[] suppliables, Injector injector) {
            final int total = suppliables.length;
            if (total == 0) {
                return new Injectron<?>[0];
            }
            Injectron<?>[] res = new Injectron<?>[total];
            for (int i = 0; i < total; i++) {
                res[i] = injectron(suppliables[i], injector, total, i);
            }
            return res;
        }

        private static <T> Injectron<T> injectron(Suppliable<T> s, Injector injector, int cardinality, int serialNumber) {
            Resource<T> resource = s.resource;
            Dependency<T> dependency = dependency(resource.getInstance());
            Demand<T> demand = demand(resource, dependency, serialNumber, cardinality);
            Injectable<T> injectable = asInjectable(s.supplier, injector);
            return Inject.injectron(injectable, resource, demand, s.expiry, s.repository, s.source);
        }

    }
}
