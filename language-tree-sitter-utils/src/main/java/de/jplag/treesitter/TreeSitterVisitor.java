package de.jplag.treesitter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.treesitter.jtreesitter.Node;

/**
 * Abstract base class for visiting Tree-sitter syntax tree nodes during traversal.
 * <p>
 * This class provides the visitor pattern for Tree-sitter AST nodes. Subclasses can override the {@link #enter(Node)}
 * and {@link #exit(Node)} methods to perform actions when traversing the syntax tree, such as token collection,
 * analysis, or transformation.
 * </p>
 * <p>
 * The class provides handler maps for node types, allowing subclasses to register specific handlers for entering and
 * exiting different node types without needing to override the main enter/exit methods.
 * </p>
 * <p>
 * Subclasses must implement {@link #initializeHandlers()} to populate these maps.
 * </p>
 */
public abstract class TreeSitterVisitor {
    /**
     * Map of node type names to handlers called when entering a node of that type.
     */
    protected final Map<String, Consumer<Node>> enterHandlers = new HashMap<>();

    /**
     * Map of node type names to handlers called when exiting a node of that type.
     */
    protected final Map<String, Consumer<Node>> exitHandlers = new HashMap<>();

    /**
     * Constructor that calls {@link #initializeHandlers()} to set up the handler maps.
     */
    protected TreeSitterVisitor() {
        initializeHandlers();
    }

    /**
     * Traverses a Tree-sitter syntax tree using depth-first traversal.
     * <p>
     * This method recursively visits all nodes in the syntax tree, calling the visitor's {@link #enter(Node)} method when
     * entering a node and {@link #exit(Node)} method when exiting a node. The traversal order ensures that parent nodes are
     * visited before their children, and exit callbacks occur after all children have been processed.
     * </p>
     * @param node The root node to start traversal from
     */
    public final void traverse(Node node) {
        enter(node);
        for (int i = 0; i < node.getChildCount(); i++) {
            node.getChild(i).ifPresent(this::traverse);
        }
        exit(node);
    }

    /**
     * Initialize the handler maps for entering and exiting different node types.
     * <p>
     * Subclasses must implement this method to populate the {@link #enterHandlers} and {@link #exitHandlers} maps with the
     * appropriate node type handlers.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>{@code
     * protected void initializeHandlers() {
     *     enterHandlers.put("class_definition", node -> addToken(CLASS_BEGIN, node));
     *     exitHandlers.put("class_definition", node -> addToken(CLASS_END, node));
     * }
     * }</pre>
     * </p>
     */
    protected abstract void initializeHandlers();

    /**
     * Called when entering a node during tree traversal.
     * <p>
     * Default implementation checks the {@link #enterHandlers} map for a handler registered for this node type and calls it
     * if found. Override this method to perform additional actions when entering a node.
     * </p>
     * @param node The current node being visited
     */
    protected void enter(Node node) {
        String nodeType = node.getType();
        Consumer<Node> handler = enterHandlers.get(nodeType);
        if (handler != null) {
            handler.accept(node);
        }
    }

    /**
     * Called when exiting a node during tree traversal.
     * <p>
     * Default implementation checks the {@link #exitHandlers} map for a handler registered for this node type and calls it
     * if found. Override this method to perform additional actions when exiting a node.
     * </p>
     * @param node The current node being exited
     */
    protected void exit(Node node) {
        String nodeType = node.getType();
        Consumer<Node> handler = exitHandlers.get(nodeType);
        if (handler != null) {
            handler.accept(node);
        }
    }
}
