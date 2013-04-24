package com.github.tailor.inject.bind;

import com.github.tailor.inject.Dependency;
import com.github.tailor.inject.Injector;
import com.github.tailor.inject.Parameter;
import com.github.tailor.inject.Supplier;
import com.github.tailor.inject.bootstrap.Invoke;
import com.github.tailor.inject.util.Argument;
import com.github.tailor.inject.util.Metaclass;

import java.lang.reflect.Constructor;

import static com.github.tailor.inject.Type.parameterTypes;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:45 PM
 */
public class SuppliedBy {

    private static final Object[] NO_ARGS = new Object[0];

    public static <T> Supplier<T> costructor(Constructor<T> constructor, Parameter... parameters) {
        final Class<?>[] params = constructor.getParameterTypes();
        if (params.length == 0) {
            return new StaticConstructorSupplier<T>(constructor, NO_ARGS);
        }
        Argument<?>[] arguments = Argument.arguments(parameterTypes(constructor), parameters);
        return Argument.allConstants(arguments)
                ? new StaticConstructorSupplier<T>(constructor, Argument.constantsFrom(arguments))
                : new ConstructorSupplier<T>(constructor, arguments);
    }

    public static <T> Supplier<T> constant(T constant) {
        return new ConstantSupplier<T>(constant);
    }

    private static final class ConstantSupplier<T> implements Supplier<T> {

        private final T instance;

        ConstantSupplier(T instance) {
            super();
            this.instance = instance;
        }

        @Override
        public T supply(Dependency<? super T> dependency, Injector injector) {
            return instance;
        }

        @Override
        public String toString() {
            return instance.toString();
        }

    }

    private static final class StaticConstructorSupplier<T> implements Supplier<T> {

        private final Constructor<T> constructor;
        private final Object[] arguments;

        StaticConstructorSupplier(Constructor<T> constructor, Object[] arguments) {
            super();
            this.constructor = Metaclass.accessible(constructor);
            this.arguments = arguments;
        }

        @Override
        public T supply(Dependency<? super T> dependency, Injector injector) {
            return Invoke.constructor(constructor, arguments);
        }

    }

    private static final class ConstructorSupplier<T> implements Supplier<T> {

        private final Constructor<T> constructor;
        private final Argument<?>[] arguments;

        ConstructorSupplier(Constructor<T> constructor, Argument<?>[] arguments) {
            super();
            this.constructor = Metaclass.accessible(constructor);
            this.arguments = arguments;
        }

        @Override
        public T supply(Dependency<? super T> dependency, Injector injector) {
            return Invoke.constructor(constructor, Argument.resolve(dependency, injector, arguments));
        }

    }

}
