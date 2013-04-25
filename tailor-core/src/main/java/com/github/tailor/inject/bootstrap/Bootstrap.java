package com.github.tailor.inject.bootstrap;

import com.github.tailor.inject.Array;
import com.github.tailor.inject.Injector;
import com.github.tailor.inject.Inspector;
import com.github.tailor.inject.util.Inject;
import com.github.tailor.inject.util.Metaclass;
import com.github.tailor.inject.util.Suppliable;

import java.util.*;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:27 PM
 */
public class Bootstrap {

    public static Injector injector(Class<? extends Bundle> root) {
        return injector(root, Inspect.DEFAULT);
    }

    public static Injector injector(Class<? extends Bundle> root, Inspector inspector) {
        return injector(modulariser().modularise(root), inspector, Link.BUILDIN);
    }

    public static Injector injector(Module[] modules, Inspector inspector, Linker<Suppliable<?>> linker) {
        return Inject.from(Suppliable.source(linker.link(inspector, modules)));
    }

    public static Modulariser modulariser() {
        return new BuildinBootstrapper();
    }

    public static <T> T instance(Class<T> type) {
        return Invoke.constructor(Metaclass.accessible(Inspect.noArgsConstructor(type)));
    }

    public static Suppliable<?>[] suppliables(Class<? extends Bundle> root) {
        return Link.BUILDIN.link(Inspect.DEFAULT, modulariser().modularise(root));
    }

    private static class BuildinBootstrapper implements Bootstrapper, Bundler, Modulariser {

        private final Map<Class<? extends Bundle>, Set<Class<? extends Bundle>>> bundleChildren = new IdentityHashMap<Class<? extends Bundle>, Set<Class<? extends Bundle>>>();
        private final Map<Class<? extends Bundle>, List<Module>> bundleModules = new IdentityHashMap<Class<? extends Bundle>, List<Module>>();
        private final Set<Class<? extends Bundle>> uninstalled = new HashSet<Class<? extends Bundle>>();
        private final Set<Class<? extends Bundle>> installed = new HashSet<Class<? extends Bundle>>();
        private final LinkedList<Class<? extends Bundle>> stack = new LinkedList<Class<? extends Bundle>>();

        BuildinBootstrapper() {
            super();
        }

        @Override
        public void install(Class<? extends Bundle> bundle) {
            if (uninstalled.contains(bundle) || installed.contains(bundle)) {
                return;
            }
            installed.add(bundle);
            if (!stack.isEmpty()) {
                final Class<? extends Bundle> parent = stack.peek();
                Set<Class<? extends Bundle>> children = bundleChildren.get(parent);
                if (children == null) {
                    children = new LinkedHashSet<Class<? extends Bundle>>();
                    bundleChildren.put(parent, children);
                }
                children.add(bundle);
            }
            stack.push(bundle);
            Bootstrap.instance(bundle).bootstrap(this);
            if (stack.pop() != bundle) {
                throw new IllegalStateException(bundle.getCanonicalName());
            }
        }

        @Override
        public void install(Module module) {
            Class<? extends Bundle> bundle = stack.peek();
            if (uninstalled.contains(bundle)) {
                return;
            }
            List<Module> modules = bundleModules.get(bundle);
            if (modules == null) {
                modules = new ArrayList<Module>();
                bundleModules.put(bundle, modules);
            }
            modules.add(module);
        }

        @Override
        public Module[] modularise(Class<? extends Bundle> root) {
            return modulesOf(bundle(root));
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<? extends Bundle>[] bundle(Class<? extends Bundle> root) {
            if (!installed.contains(root)) {
                install(root);
            }
            Set<Class<? extends Bundle>> installed = new LinkedHashSet<Class<? extends Bundle>>();
            addAllInstalledIn(root, installed);
            return Array.of(installed, Class.class);
        }

        private Module[] modulesOf(Class<? extends Bundle>[] bundles) {
            List<Module> installed = new ArrayList<Module>(bundles.length);
            for (Class<? extends Bundle> b : bundles) {
                List<Module> modules = bundleModules.get(b);
                if (modules != null) {
                    installed.addAll(modules);
                }
            }
            return Array.of(installed, Module.class);
        }

        @Override
        public void uninstall(Class<? extends Bundle> bundle) {
            if (uninstalled.contains(bundle)) {
                return;
            }
            uninstalled.add(bundle);
            installed.remove(bundle);
            for (Set<Class<? extends Bundle>> c : bundleChildren.values()) {
                c.remove(bundle);
            }
            bundleModules.remove(bundle);
        }

        private void addAllInstalledIn(Class<? extends Bundle> bundle, Set<Class<? extends Bundle>> accu) {
            accu.add(bundle);
            Set<Class<? extends Bundle>> children = bundleChildren.get(bundle);
            if (children == null) {
                return;
            }
            for (Class<? extends Bundle> c : children) {
                if (!accu.contains(c)) {
                    addAllInstalledIn(c, accu);
                }
            }
        }

    }

}
