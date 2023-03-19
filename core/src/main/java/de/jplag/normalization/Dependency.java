package de.jplag.normalization;

import java.util.HashSet;
import java.util.Set;

import de.jplag.semantics.Variable;

class Dependency {
    private Set<DependencyItem> items;
    private boolean isVariableFlow;
    private boolean isVariableReverseFlow;

    Dependency() {
        items = new HashSet<>();
        isVariableFlow = false;
    }

    boolean isVariableFlow() {
        return isVariableFlow;
    }

    boolean isVariableReverseFlow() {
        return isVariableReverseFlow;
    }

    void addItem(DependencyType type, Variable cause) {
        if (type == DependencyType.VARIABLE_FLOW)
            isVariableFlow = true;
        if (type == DependencyType.VARIABLE_REVERSE_FLOW)
            isVariableReverseFlow = true;
        items.add(new DependencyItem(type, cause));
    }
}
