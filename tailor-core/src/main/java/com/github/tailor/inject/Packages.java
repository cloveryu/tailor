package com.github.tailor.inject;

import java.util.Arrays;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:16 PM
 */
public final class Packages {

    public static final Packages ALL = new Packages(new String[0], true);

    public static final Packages DEFAULT = new Packages(new String[0], false);

    public static Packages packageAndSubPackagesOf(Class<?> type) {
        return new Packages(packageNameOf(type), true);
    }

    public static Packages packageAndSubPackagesOf(Class<?> type, Class<?>... types) {
        commonPackageDepth(type, types);
        return new Packages(packageNamesOf(type, "", types), true);
    }

    public static Packages packageOf(Class<?> type) {
        return new Packages(packageNameOf(type), false);
    }

    public static Packages packageOf(Class<?> type, Class<?>... types) {
        return new Packages(packageNamesOf(type, "", types), false);
    }

    public static Packages subPackagesOf(Class<?> type) {
        return new Packages(packageNameOf(type) + ".", true);
    }

    public static Packages subPackagesOf(Class<?> type, Class<?>... types) {
        commonPackageDepth(type, types);
        return new Packages(packageNamesOf(type, ".", types), true);
    }

    private static String[] packageNamesOf(Class<?> packageOf, String suffix, Class<?>... packagesOf) {
        String[] names = new String[packagesOf.length + 1];
        names[0] = packageNameOf(packageOf) + suffix;
        for (int i = 1; i <= packagesOf.length; i++) {
            names[i] = packageNameOf(packagesOf[i - 1]) + suffix;
        }
        return names;
    }

    private static String packageNameOf(Type<?> packageOf) {
        return packageOf.isLowerBound() ? "-NONE-" : packageNameOf(packageOf.getRawType());
    }

    private static String packageNameOf(Class<?> packageOf) {
        Package pkg = packageOf.getPackage();
        return pkg == null ? "(default)" : pkg.getName();
    }

    private final String[] roots;
    private final boolean includingSubpackages;
    private final int rootDepth;

    private Packages(String root, boolean includingSubpackages) {
        this(new String[]{root}, includingSubpackages);
    }

    private Packages(String[] roots, boolean includingSubpackages) {
        super();
        this.roots = roots;
        this.includingSubpackages = includingSubpackages;
        this.rootDepth = rootDepth(roots);
    }

    private static void commonPackageDepth(Class<?> type, Class<?>[] types) {
        int p0 = dotsIn(type.getPackage().getName());
        for (Class<?> type1 : types) {
            if (dotsIn(type1.getPackage().getName()) != p0) {
                throw new IllegalArgumentException("All classes of a packages set have to be on same depth level.");
            }
        }
    }

    private static int rootDepth(String[] roots) {
        return roots.length == 0 ? 0 : dotsIn(roots[0]);
    }

    private static int dotsIn(String s) {
        int c = 0;
        for (int i = s.length() - 1; i > 0; i--) {
            if (s.charAt(i) == '.') {
                c++;
            }
        }
        return c;
    }

    public boolean contains(Type<?> type) {
        if (includesAll()) {
            return true;
        }
        final String packageNameOfType = packageNameOf(type);
        for (String root : roots) {
            if (regionEqual(root, packageNameOfType, includingSubpackages
                    ? root.length()
                    : packageNameOfType.length())) {
                return true;
            }
        }
        return false;
    }

    public boolean includesAll() {
        return roots.length == 0 && includingSubpackages;
    }

    public static boolean regionEqual(String p1, String p2, int length) {
        if (p1.length() < length || p2.length() < length) {
            return false;
        }
        if (p1.equals(p2)) {
            return true;
        }
        for (int i = length - 1; i > 0; i--) {
            if (p1.charAt(i) != p2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean equalTo(Packages other) {
        return other.includingSubpackages == includingSubpackages
                && other.rootDepth == rootDepth
                && other.roots.length == roots.length
                && Arrays.equals(roots, other.roots);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Packages && equalTo(((Packages) obj));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(roots);
    }

    public Packages parents() {
        if (rootDepth == 0) {
            return this;
        }
        if (rootDepth == 1) {
            return includingSubpackages ? ALL : DEFAULT;
        }
        String[] parentRoots = new String[roots.length];
        for (int i = 0; i < roots.length; i++) {
            parentRoots[i] = parent(roots[i]);
        }
        return new Packages(parentRoots, includingSubpackages);
    }

    private static String parent(String root) {
        return root.substring(0, root.lastIndexOf('.', root.length() - 2) + (root.endsWith(".") ? 1 : 0));
    }
}
