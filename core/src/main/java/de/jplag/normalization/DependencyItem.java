package de.jplag.normalization;

import de.jplag.semantics.Variable;

public record DependencyItem(DependencyType type, Variable cause) {
}
