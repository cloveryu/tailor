package com.github.tailor.inject.util;

import com.github.tailor.inject.*;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:52 PM
 */
public class Scoped {

    public interface KeyDeduction {

        <T> String deduceKey(Demand<T> demand);
    }

    public static final KeyDeduction DEPENDENCY_TYPE_KEY = new DependencyTypeAsKey();
    public static final KeyDeduction TARGET_INSTANCE_KEY = new TargetInstanceAsKey();

    public static final Scope APPLICATION = new ApplicationScope();
    public static final Scope INJECTION = new InjectionScope();
    public static final Scope TARGET_INSTANCE = uniqueBy(TARGET_INSTANCE_KEY);
    public static final Scope DEPENDENCY_TYPE = uniqueBy( DEPENDENCY_TYPE_KEY );

    public static Scope uniqueBy(KeyDeduction keyDeduction) {
        return new KeyDeductionScope(keyDeduction);
    }

    private static final class ApplicationScope implements Scope {

        ApplicationScope() {
        }

        @Override
        public Repository init() {
            return new ResourceRepository();
        }

        @Override
        public String toString() {
            return "(--app)";
        }
    }

    private static final class ResourceRepository implements Repository {

        private Object[] instances;

        ResourceRepository() {
            super();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T serve(Demand<T> demand, Injectable<T> injectable) {
            if (instances == null) {
                instances = new Object[demand.envCardinality()];
            }
            T res = (T) instances[demand.envSerialNumber()];
            if (res != null) {
                return res;
            }
            synchronized (instances) {
                res = (T) instances[demand.envSerialNumber()];
                if (res == null) {
                    res = injectable.instanceFor(demand);
                    instances[demand.envSerialNumber()] = res;
                }
            }
            return res;
        }

    }

    private static final class InjectionScope implements Scope, Repository {

        InjectionScope() {
        }

        @Override
        public Repository init() {
            return this;
        }

        @Override
        public <T> T serve(Demand<T> demand, Injectable<T> injectable) {
            return injectable.instanceFor(demand);
        }

        @Override
        public String toString() {
            return "(default)";
        }

    }

    private static final class TargetInstanceAsKey implements KeyDeduction {

        TargetInstanceAsKey() {
        }

        @Override
        public <T> String deduceKey(Demand<T> demand) {
            Dependency<? super T> dependency = demand.getDependency();
            StringBuilder b = new StringBuilder();
            for (int i = dependency.injectionDepth() - 1; i >= 0; i--) {
                b.append(dependency.target(i));
            }
            return b.toString();
        }

        @Override
        public String toString() {
            return "target-instance";
        }

    }

    private static final class DependencyTypeAsKey implements KeyDeduction {

        DependencyTypeAsKey() {
        }

        @Override
        public <T> String deduceKey( Demand<T> demand ) {
            return demand.getDependency().getType().toString();
        }

        @Override
        public String toString() {
            return "dependendy-type";
        }

    }

    private static final class KeyDeductionScope implements Scope {

        private final KeyDeduction keyDeduction;

        KeyDeductionScope(KeyDeduction keyDeduction) {
            super();
            this.keyDeduction = keyDeduction;
        }

        @Override
        public Repository init() {
            return new KeyDeductionRepository(keyDeduction);
        }

        @Override
        public String toString() {
            return "(per-" + keyDeduction + ")";
        }

    }

    private static final class KeyDeductionRepository implements Repository {

        private final Map<String, Object> instances = new HashMap<String, Object>();
        private final KeyDeduction injectionKey;

        KeyDeductionRepository(KeyDeduction injectionKey) {
            super();
            this.injectionKey = injectionKey;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T serve(Demand<T> demand, Injectable<T> injectable) {
            final String key = injectionKey.deduceKey(demand);
            T instance = (T) instances.get(key);
            if (instance != null) {
                return instance;
            }
            synchronized (instances) {
                instance = (T) instances.get(key);
                if (instance == null) {
                    instance = injectable.instanceFor(demand);
                    instances.put(key, instance);
                }
            }
            return instance;
        }

    }

}
