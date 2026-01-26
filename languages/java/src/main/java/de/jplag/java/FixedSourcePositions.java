package de.jplag.java;

import com.sun.source.tree.CompilationUnitTree;
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
    public long getStartPosition(CompilationUnitTree compilationUnitTree, Tree tree) {
        return this.base.getStartPosition(compilationUnitTree, tree);
    }

    @Override
    public long getEndPosition(CompilationUnitTree compilationUnitTree, Tree tree) {
        return Math.max(this.getStartPosition(compilationUnitTree, tree), this.base.getEndPosition(compilationUnitTree, tree));
    }
}
