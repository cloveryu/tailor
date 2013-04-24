package com.github.tailor.inject.util;

import com.github.tailor.inject.Demand;
import com.github.tailor.inject.Injectable;
import com.github.tailor.inject.Repository;
import com.github.tailor.inject.Scope;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:52 PM
 */
public class Scoped {

    public static final Scope APPLICATION = new ApplicationScope();

    public static final Scope INJECTION = new InjectionScope();

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

}
