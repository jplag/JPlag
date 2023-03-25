package de.jplag.normalization;

import java.util.HashSet;
import java.util.Set;

import de.jplag.semantics.Variable;

class Edge {
    private Set<EdgeItem> items;
    private boolean isVariableFlow;
    private boolean isVariableReverseFlow;

    Edge() {
        items = new HashSet<>();
        isVariableFlow = false;
    }

    boolean isVariableFlow() {
        return isVariableFlow;
    }

    boolean isVariableReverseFlow() {
        return isVariableReverseFlow;
    }

    void addItem(EdgeType type, Variable cause) {
        if (type == EdgeType.VARIABLE_FLOW)
            isVariableFlow = true;
        if (type == EdgeType.VARIABLE_REVERSE_FLOW)
            isVariableReverseFlow = true;
        items.add(new EdgeItem(type, cause));
    }
}
