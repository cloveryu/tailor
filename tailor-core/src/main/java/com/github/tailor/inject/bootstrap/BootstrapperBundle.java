package com.github.tailor.inject.bootstrap;

/**
 * User: Clover Yu
 * Date: 4/25/13
 * Time: 4:23 PM
 */
public abstract class BootstrapperBundle implements Bundle, Bootstrapper {

    private Bootstrapper bootstrap;

    @Override
    public final void bootstrap(Bootstrapper bootstrap) {
        this.bootstrap = bootstrap;
        bootstrap();
    }

    @Override
    public final void install(Class<? extends Bundle> bundle) {
        bootstrap.install(bundle);
    }

    @Override
    public final void install(Module module) {
        bootstrap.install(module);
    }

    @Override
    public final void uninstall(Class<? extends Bundle> bundle) {
        bootstrap.uninstall(bundle);
    }

    @Override
    public String toString() {
        return "bundle " + getClass().getSimpleName();
    }

    protected abstract void bootstrap();

}
