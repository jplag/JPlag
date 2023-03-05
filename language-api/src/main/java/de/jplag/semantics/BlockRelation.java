package de.jplag.semantics;

/**
 * Enumerates the relationships a code snippet can have with a code block.
 */
enum BlockRelation {
    /**
     * This code snippet begins the block.
     */
    BEGINS_BLOCK,
    /**
     * This code snippet ends the block.
     */
    ENDS_BLOCK,
    /**
     * This code snippet neither begins nor ends the block.
     */
    NONE
}
