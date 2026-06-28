package de.jplag.java;

import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;

/**
 * Fixes the source positions, so that the end position is always at least the same as the start position.
 */
public class FixedSourcePositions implements SourcePositions {
    private final SourcePositions base;

    /**
     * New instance.
     * @param base The source positions to use as the base.
     */
    public FixedSourcePositions(SourcePositions base) {
        this.base = base;
    }

    @Override
    public long getStartPosition(Tree tree) {
        return this.base.getStartPosition(tree);
    }

    @Override
    public long getEndPosition(Tree tree) {
        // Add one to assert start <= end (one is subtracted later)
        return Math.max(this.getStartPosition(tree) + 1, this.base.getEndPosition(tree));
    }
}
