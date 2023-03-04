package de.jplag.semantics;

/**
 * Enumerates how the order of an item in a sequence relative to other items may be relevant.
 */
public enum Ordering {
    /**
     * The order of the item relative to other items in the sequence is not relevant.
     */
    NONE(0),
    /**
     * The order of the item relative to other items that also have partial ordering is relevant.
     */
    PARTIAL(1),
    /**
     * The order of the item to all other items is relevant.
     */
    FULL(2);

    private final int strength;

    Ordering(int strength) {
        this.strength = strength;
    }

    boolean isStrongerThan(Ordering other) {
        return this.strength > other.strength;
    }
}
