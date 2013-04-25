package com.github.tailor.inject.bind;

import com.github.tailor.inject.*;
import com.github.tailor.inject.bootstrap.Invoke;
import com.github.tailor.inject.util.Argument;
import com.github.tailor.inject.util.Metaclass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.Type.parameterTypes;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:45 PM
 */
public class SuppliedBy {

    private static final Object[] NO_ARGS = new Object[0];

    public static <T> Supplier<T> method(Type<T> returnType, Method factory, Object instance, Parameter... parameters) {
        if (!Type.returnType(factory).isAssignableTo(returnType)) {
            throw new IllegalArgumentException("The factory methods methods return type `"
                    + Type.returnType(factory) + "` is not assignable to: " + returnType);
        }
        if (instance != null && factory.getDeclaringClass() != instance.getClass()) {
            throw new IllegalArgumentException(
                    "The factory method and the instance it is invoked on have to be the same class.");
        }
        Argument<?>[] arguments = Argument.arguments(Type.parameterTypes(factory), parameters);
        return new FactoryMethodSupplier<T>(returnType, factory, instance, arguments);
    }

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

    public static <T> Supplier<T> instance(Instance<T> instance) {
        return new InstanceSupplier<T>(instance);
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

    private static final class InstanceSupplier<T> implements Supplier<T> {

        private final Instance<? extends T> instance;

        InstanceSupplier(Instance<? extends T> instance) {
            super();
            this.instance = instance;
        }

        @Override
        public T supply(Dependency<? super T> dependency, Injector injector) {
            return injector.resolve(dependency.instanced(instance));
        }

        @Override
        public String toString() {
            return instance.toString();
        }
    }

    private static final class FactoryMethodSupplier<T> implements Supplier<T> {

        private final Type<T> returnType;
        private final Method factory;
        private final Object instance;
        private final Argument<?>[] arguments;
        private final boolean instanceMethod;

        FactoryMethodSupplier(Type<T> returnType, Method factory, Object instance, Argument<?>[] arguments) {
            super();
            this.returnType = returnType;
            this.factory = Metaclass.accessible(factory);
            this.instance = instance;
            this.arguments = arguments;
            this.instanceMethod = !Modifier.isStatic(factory.getModifiers());
        }

        @Override
        public T supply(Dependency<? super T> dependency, Injector injector) {
            Object owner = instance;
            if (instanceMethod && owner == null) {
                owner = injector.resolve(dependency(factory.getDeclaringClass()));
            }
            final Object[] args = Argument.resolve(dependency, injector, arguments);
            return returnType.getRawType().cast(Invoke.method(factory, owner, args));
        }

    }

}
