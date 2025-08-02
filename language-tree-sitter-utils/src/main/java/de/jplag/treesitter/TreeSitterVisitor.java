package de.jplag.treesitter;

import io.github.treesitter.jtreesitter.Node;

/**
 * Interface for visiting Tree-sitter syntax tree nodes during traversal.
 * <p>
 * This interface defines the visitor pattern for Tree-sitter AST nodes. Implementations can use the
 * {@link #enter(Node)} and {@link #exit(Node)} callbacks to perform actions when traversing the syntax tree, such as
 * token collection, analysis, or transformation.
 * </p>
 */
public interface TreeSitterVisitor {
    /**
     * Called when entering a node during tree traversal.
     * @param node The current node being visited
     */
    void enter(Node node);

    /**
     * Called when exiting a node during tree traversal.
     * @param node The current node being exited
     */
    void exit(Node node);
}
