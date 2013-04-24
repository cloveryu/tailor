package com.github.tailor.inject;

import java.util.Arrays;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:16 PM
 */
public final class Packages {

    public static final Packages ALL = new Packages(new String[0], true);

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

    private Packages(String[] roots, boolean includingSubpackages) {
        super();
        this.roots = roots;
        this.includingSubpackages = includingSubpackages;
        this.rootDepth = rootDepth(roots);
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
}
