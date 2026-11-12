package de.jplag.java_cpg.transformation.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;

/**
 * An intermediary neighbor for nodes moved within one translation, owned by its {@link GraphMutationContext}.
 */
public class DummyNeighbor extends Node {

    /**
     * Maps detached nodes to the edge that they were previously a source of.
     */
    private final Map<Node, List<PropertyEdge<Node>>> sourceMap;

    /**
     * Maps detached nodes to the edge that they were previously a target of.
     */
    private final Map<Node, List<PropertyEdge<Node>>> targetMap;

    /* package-private */ DummyNeighbor() {
        sourceMap = new IdentityHashMap<>();
        targetMap = new IdentityHashMap<>();
    }

    /**
     * Adds the given {@code edge} as a former incoming edge of {@code edge.end}.
     * @param edge the edge
     */
    public void saveOriginalTarget(PropertyEdge<Node> edge) {
        if (edge.getEnd() == this) {
            return;
        }
        targetMap.computeIfAbsent(edge.getEnd(), _ -> new ArrayList<>()).add(edge);
    }

    /**
     * Adds the given {@code edge} as a former outgoing edge of {@code edge.start}.
     * @param edge the edge
     */
    public void saveOriginalSource(PropertyEdge<Node> edge) {
        if (edge.getStart() == this) {
            return;
        }
        sourceMap.computeIfAbsent(edge.getStart(), _ -> new ArrayList<>()).add(edge);
    }

    /**
     * Gets the registered former outgoing edges of the given node.
     * @param origSource the node
     * @return the former outgoing edges
     */
    public List<PropertyEdge<Node>> getOriginalEdgeOfSource(Node origSource) {
        return List.copyOf(sourceMap.getOrDefault(origSource, Collections.emptyList()));
    }

    /**
     * Gets the registered former incoming edges of the given node.
     * @param origTarget the node
     * @return the former incoming edges
     */
    public List<PropertyEdge<Node>> getOriginalEdgeOfTarget(Node origTarget) {
        return List.copyOf(targetMap.getOrDefault(origTarget, Collections.emptyList()));
    }

    /**
     * Clears the registered former incoming edges of the given node.
     * @param target the node
     */
    public void clearOriginalEdgesOfTarget(Node target) {
        List<PropertyEdge<Node>> edges = targetMap.getOrDefault(target, List.of());
        if (!edges.isEmpty()) {
            edges.clear();
        }
    }

    /**
     * Clears the saved edges.
     */
    public void clear() {
        targetMap.clear();
        sourceMap.clear();
    }

}
