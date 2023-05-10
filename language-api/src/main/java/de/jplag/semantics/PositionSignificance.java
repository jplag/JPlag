package de.jplag.semantics;

/**
 * Enumerates how the position of an item in a sequence relative to other items may be significant.
 */
enum PositionSignificance {
    /**
     * The position of the item relative to other items is insignificant.
     */
    NONE,
    /**
     * The position of the item relative to other items with partial position significance is significant.
     */
    PARTIAL,
    /**
     * The position of the item to all other items is significant.
     */
    FULL
}
