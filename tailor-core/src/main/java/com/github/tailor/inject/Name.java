package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 10:24 AM
 */
public final class Name {

    private String value;

    private Name(String value) {
        this.value = value.intern();
    }

    public static Name named(String name) {
        return new Name( name.toLowerCase() );
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean equalTo( Name other ) {
        return value.equals(other.value);
    }

    @Override
    public boolean equals( Object obj ) {
        return obj instanceof Name && equalTo( (Name) obj );
    }
}
