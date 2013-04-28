package com.github.tailor.inject.demo;

import com.github.tailor.inject.Injector;
import com.github.tailor.inject.bind.BinderModule;
import com.github.tailor.inject.bootstrap.Bootstrap;
import com.github.tailor.inject.bootstrap.BootstrapperBundle;
import org.hamcrest.Factory;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.github.tailor.inject.Dependency.dependency;
import static com.github.tailor.inject.Instance.instance;
import static com.github.tailor.inject.Name.named;
import static com.github.tailor.inject.Type.raw;
import static com.github.tailor.inject.bootstrap.Inspect.all;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: Clover Yu
 * Date: 4/26/13
 * Time: 12:37 AM
 */
public class PizzaStoreTest {

    @Target({METHOD, PARAMETER})
    @Retention(RUNTIME)
    public static @interface Resource {
        String value();
    }

    static class ChengduPizzaStoreBindsModule extends BinderModule {

        @Override
        protected void declare() {
            bind(all().methods().namedBy(Resource.class)).inModule();
            bind(PizzaStore.class).toConstructor(raw(Pizza.class), instance(named("storeName"), raw(String.class)));
        }

        @Resource("type")
        static String type() {
            return "cheese";
        }

        @Resource("storeName")
        static String storeName() {
            return "Chengdu store";
        }

    }

    static class XianPizzaStoreBindsModule extends BinderModule {

        @Override
         protected void declare() {
            bind(all().methods().namedBy(Resource.class)).inModule();
            bind(PizzaStore.class).toConstructor(raw(Pizza.class), instance(named("storeName"), raw(String.class)));
        }

        @Resource("type")
        static String type() {
            return "clam";
        }

        @Resource("storeName")
        static String storeName() {
            return "Xian store";
        }

    }

    static class PizzaBindsModule extends BinderModule {

        @Override
        protected void declare() {
            bind(all().methods().annotatedWith(Factory.class).namedBy(Resource.class)).in(Pizza.class);
        }

    }

    private static class ChengduPizzaStoreBindsBundle extends BootstrapperBundle {

        @Override
        protected void bootstrap() {
            install(ChengduPizzaStoreBindsModule.class);
            install(PizzaBindsModule.class);
        }

    }

    private static class XianPizzaStoreBindsBundle extends BootstrapperBundle {

        @Override
        protected void bootstrap() {
            install(XianPizzaStoreBindsModule.class);
            install(PizzaBindsModule.class);
        }

    }

    static class PizzaStore {

        private String name;
        private Pizza pizza;

        public PizzaStore(String name, Pizza pizza) {
            this.name = name;
            this.pizza = pizza;
        }

        public Pizza orderPizza() {
            pizza.make(name);
            return pizza;
        }

    }

    static abstract class Pizza {

        protected String name;
        protected String status;

        Pizza(String name) {
            this.name = name;
        }

        protected void make(String storeName) {
            this.status = storeName + " had made " + this.name + " pizza for you.";
        }

        @Factory
        @Resource("pizza")
        public static Pizza createPizza(@Resource("type") String type) {
            Pizza pizza = null;
            if ("cheese".equals(type)) {
                pizza = new CheesePizza(type);
            } else if ("clam".equals(type)) {
                pizza = new ClamPizza(type);
            }
            return pizza;
        }
    }

    static class CheesePizza extends Pizza {

        public CheesePizza(String name) {
            super(name);
        }
    }

    static class ClamPizza extends Pizza {

        public ClamPizza(String name) {
            super(name);
        }
    }

    private final Injector cdInjector = Bootstrap.injector(ChengduPizzaStoreBindsBundle.class);
    private final Injector xaInjector = Bootstrap.injector(XianPizzaStoreBindsBundle.class);

    @Test
    public void thatInstanceFactoryMethodIsAvailable() {
        String type = cdInjector.resolve(dependency(String.class).named("type"));
        assertThat(type, is("cheese"));
    }

    @Test
    public void thatStaticFactoryMethodIsAvailable() {
        Pizza pizza = cdInjector.resolve(dependency(Pizza.class).named("pizza"));
        assertThat(pizza.name, is("cheese"));
    }

    @Test
    public void shouldBeUsedAsConstructorArguments() {
        PizzaStore pizzaStore = cdInjector.resolve(dependency(PizzaStore.class));
        assertThat(pizzaStore.orderPizza().status, is("Chengdu store had made cheese pizza for you."));
    }

    @Test
    public void thatInstanceFactoryMethodIsAvailableGivenXianStore() {
        String type = xaInjector.resolve(dependency(String.class).named("type"));
        assertThat(type, is("clam"));
    }

    @Test
    public void thatStaticFactoryMethodIsAvailableGivenXianStore() {
        Pizza pizza = xaInjector.resolve(dependency(Pizza.class).named("pizza"));
        assertThat(pizza.name, is("clam"));
    }

    @Test
    public void shouldBeUsedAsConstructorArgumentsGivenXianStore() {
        PizzaStore pizzaStore = xaInjector.resolve(dependency(PizzaStore.class));
        assertThat(pizzaStore.orderPizza().status, is("Xian store had made clam pizza for you."));
    }

}
