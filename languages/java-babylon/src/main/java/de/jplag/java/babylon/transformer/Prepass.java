package de.jplag.java.babylon.transformer;

import com.sun.source.tree.CompilationUnitTree;

import com.sun.source.tree.TreeVisitor;

/**
 * Represents an operation to be performed before the main pass
 * ({@link de.jplag.java.babylon.TokenGeneratingTreeScannerBabylon}) is performed.
 * @param <Context> the type of the returned context
 */
public interface Prepass<Context> extends TreeVisitor<Void, CompilationUnitTree> {
    /**
     * Build a final context object to be consumed by the main pass.
     * @return the built context
     */
    Context finalizeContext();
}
