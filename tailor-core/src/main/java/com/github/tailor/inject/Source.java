package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:28 PM
 */
public class Source {

    public static Source source(Class<?> module) {
        return new Source(module, DeclarationType.EXPLICIT);
    }

    private final Class<?> ident;
    private final DeclarationType declarationType;

    private Source(Class<?> ident, DeclarationType declarationType) {
        super();
        this.ident = ident;
        this.declarationType = declarationType;
    }

    public DeclarationType getType() {
        return declarationType;
    }

    public Source typed(DeclarationType type) {
        return declarationType == type ? this : new Source(ident, type);
    }

    public Class<?> getIdent() {
        return ident;
    }
}
