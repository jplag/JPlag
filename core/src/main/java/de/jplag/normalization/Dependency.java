package de.jplag.normalization;

import java.util.HashSet;
import java.util.Set;

import de.jplag.semantics.Variable;

// only purpose is debugging/explainability, edges could be anonymous otherwise
// not a record because JGraphT wants unique edges and we don't...
public class Dependency {
    private Set<DependencyItem> items;

    public Dependency() {
        items = new HashSet<>();
    }

    public void addItem(DependencyType type, Variable cause) {
        items.add(new DependencyItem(type, cause));
    }
}
