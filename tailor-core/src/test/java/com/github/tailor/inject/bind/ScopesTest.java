package com.github.tailor.inject.bind;

import com.github.tailor.inject.Demand;
import com.github.tailor.inject.Injectable;
import com.github.tailor.inject.Repository;
import com.github.tailor.inject.util.Scoped;
import org.junit.Test;

import static com.github.tailor.inject.Demand.demand;
import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.Resource.resource;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 9:50 PM
 */
public class ScopesTest {

    private static class ConstantInjectable<T> implements Injectable<T> {

        private final T instance;

        ConstantInjectable(T instance) {
            super();
            this.instance = instance;
        }

        @Override
        public T instanceFor(Demand<T> demand) {
            return instance;
        }
    }

    static class A {
    }

    static class B {
    }

    @Test
    public void thatDependencyTypeScopeEnsuresSingletonPerExactGenericType() {
        Repository r = Scoped.DEPENDENCY_TYPE.init();
        Demand<A> da = demand(resource(A.class), dependency(A.class), 1, 2);
        Demand<B> db = demand(resource(B.class), dependency(B.class), 2, 2);
        A a = new A();
        B b = new B();
        Injectable<A> ia = new ConstantInjectable<A>(a);
        Injectable<B> ib = new ConstantInjectable<B>(b);
        assertThat(r.serve(da, ia), sameInstance(a));
        assertThat(r.serve(db, ib), sameInstance(b));
    }
}
