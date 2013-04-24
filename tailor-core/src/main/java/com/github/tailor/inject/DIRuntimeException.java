package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:48 PM
 */
public class DIRuntimeException extends RuntimeException {

    public DIRuntimeException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }

    public static String injectionStack(Dependency<?> dependency) {
        StringBuilder b = new StringBuilder();
        for (Injection i : dependency) {
            b.append(i.getTarget().getInstance()).append(" -> ");
        }
        return b.toString();
    }

    public static String describe(Injectron<?>... injectrons) {
        if (injectrons == null || injectrons.length == 0) {
            return "none";
        }
        StringBuilder b = new StringBuilder();
        for (Injectron<?> i : injectrons) {
            b.append('\n').append(i.getResource().toString()).append(" defined ").append(i.getSource());
        }
        return b.toString();
    }

    public static final class NoSuchResourceException extends DIRuntimeException {

        public <T> NoSuchResourceException(Dependency<T> dependency, Injectron<T>[] available) {
            super("No resource for dependency: " + injectionStack(dependency)
                    + dependency.getInstance() + "\navailable are (for same raw type): "
                    + describe(available));
        }

    }

    public static final class MoreFrequentExpiryException extends DIRuntimeException {

        public MoreFrequentExpiryException(Injection parent, Injection injection) {
            super("Cannot inject " + injection.getTarget() + " into " + parent.getTarget());
        }

    }

    public static final class DependencyCycleException extends DIRuntimeException {

        public DependencyCycleException(Dependency<?> dependency, Instance<?> cycleTarget) {
            super("Cycle detected: " + injectionStack(dependency) + cycleTarget);
        }

    }
}
