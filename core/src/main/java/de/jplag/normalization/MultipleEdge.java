package de.jplag.normalization;

import java.util.HashSet;
import java.util.Set;

import de.jplag.semantics.Variable;

/**
 * Models multiple edges between two nodes in the normalization graph.
 */
class MultipleEdge {
    private final Set<Edge> edges;
    private boolean isVariableFlow;
    private boolean isVariableReverseFlow;

    MultipleEdge() {
        edges = new HashSet<>();
        isVariableFlow = false;
    }

    boolean isVariableFlow() {
        return isVariableFlow;
    }

    boolean isVariableReverseFlow() {
        return isVariableReverseFlow;
    }

    void addEdge(EdgeType type, Variable cause) {
        if (type == EdgeType.VARIABLE_FLOW) {
            isVariableFlow = true;
        }
        if (type == EdgeType.VARIABLE_REVERSE_FLOW) {
            isVariableReverseFlow = true;
        }
        edges.add(new Edge(type, cause));
    }
}
