package de.jplag.normalization;

import de.jplag.semantics.Variable;

/**
 * Models multiple edges between two nodes in the normalization graph.
 */
class MultipleEdge {
    private boolean isVariableFlow;
    private boolean isVariableReverseFlow;

    MultipleEdge() {
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
    }
}
