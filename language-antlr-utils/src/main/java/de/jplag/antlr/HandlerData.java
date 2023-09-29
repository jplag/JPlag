package de.jplag.antlr;

import de.jplag.semantics.VariableRegistry;

/**
 * Holds the data passed to the (quasi-static) listeners.
 */
record HandlerData<T>(T entity, VariableRegistry variableRegistry, TokenCollector collector) {
}
