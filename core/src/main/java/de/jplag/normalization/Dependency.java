package de.jplag.normalization;

import java.util.HashSet;
import java.util.Set;

import de.jplag.semantics.Variable;

// not a record because JGraphT wants unique edges and we don't...
public class Dependency {
    private Set<DependencyItem> items;
    private boolean isData;
    private boolean isDataThroughLoop;

    public Dependency() {
        items = new HashSet<>();
        isData = false;
    }

    public boolean isData() {
        return isData;
    }

    public boolean isDataThroughLoop() {
        return isDataThroughLoop;
    }

    public void addItem(DependencyType type, Variable cause) {
        if (type == DependencyType.DATA)
            isData = true;
        if (type == DependencyType.DATA_THROUGH_LOOP)
            isDataThroughLoop = true;
        items.add(new DependencyItem(type, cause));
    }
}
