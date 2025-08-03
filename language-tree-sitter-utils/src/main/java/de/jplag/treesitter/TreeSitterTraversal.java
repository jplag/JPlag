package de.jplag.treesitter;

import io.github.treesitter.jtreesitter.Node;

/**
 * Utility class for traversing Tree-sitter syntax trees using the visitor pattern.
 * <p>
 * This class provides a depth-first traversal mechanism for Tree-sitter AST nodes. It implements the visitor pattern by
 * calling the appropriate methods on a {@link TreeSitterVisitor} as it traverses the syntax tree.
 * </p>
 */
public final class TreeSitterTraversal {
    /**
     * Utility class constructor.
     */
    private TreeSitterTraversal() {
    }

    /**
     * Traverses a Tree-sitter syntax tree using depth-first traversal.
     * <p>
     * This method recursively visits all nodes in the syntax tree, calling the visitor's
     * {@link TreeSitterVisitor#enter(Node)} method when entering a node and {@link TreeSitterVisitor#exit(Node)} method
     * when exiting a node. The traversal order ensures that parent nodes are visited before their children, and exit
     * callbacks occur after all children have been processed.
     * </p>
     * @param node The root node to start traversal from
     * @param visitor The visitor to call for each node during traversal
     */
    public static void traverse(Node node, TreeSitterVisitor visitor) {
        visitor.enter(node);
        for (int i = 0; i < node.getChildCount(); i++) {
            node.getChild(i).ifPresent(child -> traverse(child, visitor));
        }
        visitor.exit(node);
    }
}
