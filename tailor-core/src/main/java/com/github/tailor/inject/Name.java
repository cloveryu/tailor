package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 10:24 AM
 */
public final class Name {

    public static final Name DEFAULT = new Name("");
    public static final String WILDCARD = "*";
    public static final Name ANY = new Name(WILDCARD);

    private final String value;

    private Name(String value) {
        this.value = value.intern();
    }

    public static Name named(String name) {
        if (name == null || name.trim().isEmpty()) {
            return DEFAULT;
        }
        return new Name(name.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean equalTo(Name other) {
        return value.equals(other.value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Name && equalTo((Name) obj);
    }

    public boolean isApplicableFor(Name other) {
        return isAny() || other.isAny() || other.value.equals(value) || (value.matches(other.value.replace(WILDCARD, ".*")));
    }

    public boolean isAny() {
        return value.equals(ANY.value);
    }

}
