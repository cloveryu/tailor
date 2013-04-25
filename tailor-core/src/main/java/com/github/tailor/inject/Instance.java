package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:12 PM
 */
public final class Instance<T> implements Parameter {

    public static final Instance<?> ANY = anyOf(Type.WILDCARD);

    public static <T> Instance<T> defaultInstanceOf(Type<T> type) {
        return instance(Name.DEFAULT, type);
    }

    public static <T> Instance<T> anyOf(Type<T> type) {
        return instance(Name.ANY, type);
    }

    public static <T> Instance<T> instance(Name name, Type<T> type) {
        return new Instance<T>(name, type);
    }

    private final Name name;
    private final Type<T> type;

    private Instance(Name name, Type<T> type) {
        super();
        this.name = name;
        this.type = type;
    }

    public boolean equalTo(Instance<?> other) {
        return type.equalTo(other.type) && name.equals(other.name);
    }

    public Type<T> getType() {
        return type;
    }

    @Override
    public boolean isAssignableTo(Type<?> type) {
        return getType().isAssignableTo(type);
    }

    public Name getName() {
        return name;
    }

    public boolean isAny() {
        return name.isAny() && type.equalTo(ANY.type);
    }

    public <E> Instance<E> typed( Type<E> type ) {
        return new Instance<E>( name, type );
    }
}
