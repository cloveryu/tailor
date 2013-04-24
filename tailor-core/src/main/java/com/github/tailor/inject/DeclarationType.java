package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 3:23 PM
 */
public enum DeclarationType {

    IMPLICIT,
    DEFAULT,
    PROVIDED,
    AUTO,
    MULTI,
    EXPLICIT,
    REQUIRED;

    public boolean clashesWith(DeclarationType other) {
        return ordinal() + other.ordinal() > MULTI.ordinal() * 2 && this != REQUIRED
                || (this == DEFAULT && other == DEFAULT);
    }

    public boolean replacedBy(DeclarationType other) {
        return other.ordinal() > ordinal() || (this == IMPLICIT && other == IMPLICIT);
    }

    public boolean nullifiedBy(DeclarationType other) {
        return this == other && (this == AUTO || this == PROVIDED);
    }
}
