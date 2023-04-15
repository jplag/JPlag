package de.jplag.normalization;

/**
 * Enum for types of edges in normalization graph. Given two statements S and T, S comes before T, there is such an edge
 * between S and T if...
 */
enum EdgeType {
    /**
     * S writes a variable T reads.
     */
    VARIABLE_FLOW,
    /**
     * S reads a variable T writes, and S and T are in the same bidirectional block.
     */
    VARIABLE_REVERSE_FLOW,
    /**
     * S and T access the same variable, and at least one of the two accesses is not a read.
     */
    VARIABLE_ORDER,
    /**
     * S or T have full position significance, and there is no statement C with full position significance between them.
     */
    POSITION_SIGNIFICANCE_FULL,
    /**
     * S and T have partial position significance, and there is no statement C with partial position significance between
     * them.
     */
    POSITION_SIGNIFICANCE_PARTIAL
}
