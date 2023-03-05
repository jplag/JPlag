package de.jplag.normalization;

import java.util.HashSet;
import java.util.Set;

import de.jplag.semantics.Variable;

class Dependency {
    private Set<DependencyItem> items;
    private boolean isData;
    private boolean isReverseData;

    Dependency() {
        items = new HashSet<>();
        isData = false;
    }

    boolean isData() {
        return isData;
    }

    boolean isReverseData() {
        return isReverseData;
    }

    void addItem(DependencyType type, Variable cause) {
        if (type == DependencyType.VARIABLE_DATA)
            isData = true;
        if (type == DependencyType.VARIABLE_REVERSE_DATA)
            isReverseData = true;
        items.add(new DependencyItem(type, cause));
    }
}
