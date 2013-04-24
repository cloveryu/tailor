package com.github.tailor.inject;

import static com.github.tailor.inject.Type.raw;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:10 PM
 */
public final class Target {

    public static final Target ANY = targeting(Instance.ANY);

    public static Target targeting( Class<?> type ) {
        return targeting( raw( type ) );
    }

    public static Target targeting( Type<?> type ) {
        return targeting( Instance.anyOf( type ) );
    }

    public static Target targeting(Instance<?> instance) {
        return new Target(Instances.ANY, instance, Packages.ALL);
    }

    private final Instances parents;
    private final Instance<?> instance;
    private final Packages packages;

    private Target(Instances parents, Instance<?> instance, Packages packages) {
        super();
        this.parents = parents;
        this.instance = instance;
        this.packages = packages;
    }

    public boolean equalTo(Target other) {
        return this == other || packages.equalTo(other.packages)
                && instance.equalTo(other.instance) && parents.equalTo(other.parents);
    }

    public boolean isApplicableFor(Dependency<?> dependency) {
        return isAccessibleFor(dependency) && isAdequateFor(dependency);
    }

    public boolean isAccessibleFor(Dependency<?> dependency) {
        return packages.contains(dependency.target().getType());
    }

    public boolean isAdequateFor(Dependency<?> dependency) {
        if (!areParentsAdequateFor(dependency)) {
            return false;
        }
        if (instance.isAny()) {
            return true;
        }
        final Instance<?> target = dependency.target();
        return instance.getName().isApplicableFor(target.getName()) && injectable(instance.getType(), target.getType());
    }

    private boolean areParentsAdequateFor(Dependency<?> dependency) {
        if (parents.isAny()) {
            return true;
        }
        int pl = parents.depth();
        int il = dependency.injectionDepth() - 1;
        if (pl > il) {
            return false;
        }
        int pi = 0;
        while (pl <= il && pl > 0) {
            if (injectable(parents.at(pi).getType(), dependency.target(il).getType())) {
                pl--;
                pi++;
            }
            il--;
        }
        return pl == 0;
    }

    private static boolean injectable(Type<?> type, Type<?> targetType) {
        return type.isInterface() || type.isAbstract() ? targetType.isAssignableTo(type) : targetType.equalTo(type);
    }
}
