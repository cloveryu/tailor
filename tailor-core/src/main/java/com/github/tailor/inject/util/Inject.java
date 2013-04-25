package com.github.tailor.inject.util;

import com.github.tailor.inject.*;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;

import static com.github.tailor.inject.DIRuntimeException.NoSuchResourceException;
import static com.github.tailor.inject.Emergence.emergence;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 5:41 PM
 */
public final class Inject {

    public static Injector from(InjectronSource source) {
        return new SourcedInjector(source);
    }

    public static <T> Injectable<T> asInjectable(Supplier<? extends T> supplier, Injector injector) {
        return new SupplierToInjectable<T>(supplier, injector);
    }

    public static <T> Injectron<T> injectron(Injectable<T> injectable, Resource<T> resource,
                                             Demand<T> demand, Expiry expiry, Repository repository, Source source) {
        return new StaticInjectron<T>(resource, source, demand, expiry, repository, injectable);
    }

    private static class SupplierToInjectable<T> implements Injectable<T> {

        private final Supplier<? extends T> supplier;
        private final Injector context;

        SupplierToInjectable(Supplier<? extends T> supplier, Injector context) {
            super();
            this.supplier = supplier;
            this.context = context;
        }

        @Override
        public T instanceFor(Demand<T> demand) {
            return supplier.supply(demand.getDependency(), context);
        }
    }

    public static final class SourcedInjector implements Injector {

        private final Map<Class<?>, Injectron<?>[]> injectrons;

        SourcedInjector(InjectronSource source) {
            super();
            this.injectrons = initFrom(source);
        }

        private Map<Class<?>, Injectron<?>[]> initFrom(InjectronSource source) {
            Injectron<?>[] injectrons = source.exportTo(this);
            Map<Class<?>, Injectron<?>[]> map = new IdentityHashMap<Class<?>, Injectron<?>[]>(
                    injectrons.length);
            if (injectrons.length == 0) {
                return map;
            }
            Class<?> lastRawType = injectrons[0].getResource().getType().getRawType();
            int start = 0;
            for (int i = 0; i < injectrons.length; i++) {
                Class<?> rawType = injectrons[i].getResource().getType().getRawType();
                if (rawType != lastRawType) {
                    map.put(lastRawType, Arrays.copyOfRange(injectrons, start, i));
                    start = i;
                }
                lastRawType = rawType;
            }
            map.put(lastRawType, Arrays.copyOfRange(injectrons, start, injectrons.length));
            return map;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T resolve(Dependency<T> dependency) {
            Injectron<T> injectron = applicableInjectron(dependency);
            if (injectron != null) {
                return injectron.instanceFor(dependency);
            }
            throw noInjectronFor(dependency);
        }

        private <T> Injectron<T> applicableInjectron(Dependency<T> dependency) {
            return mostPreciseOf(typeInjectrons(dependency.getType()), dependency);
        }

        private static <T> Injectron<T> mostPreciseOf(Injectron<T>[] injectrons, Dependency<T> dependency) {
            if (injectrons == null) {
                return null;
            }
            for (Injectron<T> injectron : injectrons) {
                if (injectron.getResource().isApplicableFor(dependency)) {
                    return injectron;
                }
            }
            return null;
        }

        private <T> NoSuchResourceException noInjectronFor(Dependency<T> dependency) {
            return new NoSuchResourceException(dependency, typeInjectrons(dependency.getType()));
        }

        @SuppressWarnings("unchecked")
        private <T> Injectron<T>[] typeInjectrons(Type<T> type) {
            return (Injectron<T>[]) injectrons.get(type.getRawType());
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            for (Map.Entry<Class<?>, Injectron<?>[]> e : injectrons.entrySet()) {
                b.append(e.getKey()).append('\n');
                for (Injectron<?> i : e.getValue()) {
                    b.append('\t').append(i).append('\n');
                }
            }
            return b.toString();
        }
    }

    private static class StaticInjectron<T> implements Injectron<T> {

        private final Resource<T> resource;
        private final Source source;
        private final Demand<T> demand;
        private final Repository repository;
        private final Injectable<T> injectable;
        private final Expiry expiry;

        StaticInjectron(Resource<T> resource, Source source, Demand<T> demand, Expiry expiry,
                        Repository repository, Injectable<T> injectable) {
            super();
            this.resource = resource;
            this.source = source;
            this.demand = demand;
            this.expiry = expiry;
            this.repository = repository;
            this.injectable = injectable;
        }

        @Override
        public Resource<T> getResource() {
            return resource;
        }

        @Override
        public Source getSource() {
            return source;
        }

        @Override
        public T instanceFor(Dependency<? super T> dependency) {
            return repository.serve(demand.from(dependency.injectingInto(emergence(resource.getInstance(), expiry))), injectable);
        }

        @Override
        public String toString() {
            return demand.toString() + resource.getTarget().toString() + " " + source.toString();
        }
    }
}
