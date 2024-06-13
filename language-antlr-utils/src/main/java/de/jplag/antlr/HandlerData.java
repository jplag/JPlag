package de.jplag.antlr;

import java.util.function.Function;

import de.jplag.semantics.VariableRegistry;

/**
 * Holds the data passed to the (quasi-static) listeners.
 */
public record HandlerData<T>(T entity, VariableRegistry variableRegistry, TokenCollector collector) {
    public <V> HandlerData<V> derive(Function<T, V> mapper) {
        return new HandlerData<>(mapper.apply(entity), variableRegistry, collector);
    }
}
