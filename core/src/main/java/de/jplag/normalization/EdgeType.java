package de.jplag.normalization;

/**
 * Enum for types of edges in normalization graph. Given two statements A and B, A comes before B, there is such an edge
 * between A and B if...
 */
enum EdgeType {
    /**
     * B reads from a variable A writes.
     */
    VARIABLE_FLOW,
    /**
     * A reads from a variable B writes, and A and B are in the same bidirectional block.
     */
    VARIABLE_REVERSE_FLOW,
    /**
     * A and B access the same variable, and at least one of the two accesses is not a read.
     */
    VARIABLE_ORDER,
    /**
     * A or B have full position significance, and there is no statement C with full position significance between them.
     */
    POSITION_SIGNIFICANCE_FULL,
    /**
     * A and B have partial position significance, and there is no statement C with partial position significance between
     * them.
     */
    POSITION_SIGNIFICANCE_PARTIAL
}
