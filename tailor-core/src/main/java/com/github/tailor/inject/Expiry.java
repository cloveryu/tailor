package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 5:20 PM
 */
public final class Expiry {

    public static final Expiry NEVER = new Expiry(0);
    public static final Expiry IGNORE = new Expiry(Integer.MAX_VALUE);

    public static Expiry expires(int frequency) {
        if (frequency < 0) {
            throw new IllegalArgumentException(Expiry.class.getSimpleName()
                    + " frequency cannot be negative but was: " + frequency);
        }
        if (frequency == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(Expiry.class.getSimpleName()
                    + "frequency cannot be Imteger.MAX_VALUE. This is reserved for IGNORE");
        }
        return new Expiry(frequency);
    }

    private final int frequency;

    public Expiry(int frequency) {
        super();
        this.frequency = frequency;
    }

    public boolean equalTo(Expiry other) {
        return frequency == other.frequency;
    }

    public boolean moreFrequent(Expiry other) {
        return frequency > other.frequency;
    }

    @Override
    public String toString() {
        return isIgnore() ? "x" : isNever() ? "���" : String.valueOf(frequency);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Expiry && ((Expiry) obj).frequency == frequency;
    }

    @Override
    public int hashCode() {
        return frequency;
    }

    public boolean isNever() {
        return frequency == 0;
    }

    public boolean isIgnore() {
        return frequency == IGNORE.frequency;
    }
}
