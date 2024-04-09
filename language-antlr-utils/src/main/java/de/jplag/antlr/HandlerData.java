package de.jplag.antlr;

import de.jplag.semantics.VariableRegistry;

import java.util.function.Function;

/**
 * Holds the data passed to the (quasi-static) listeners.
 */
public record HandlerData<T>(T entity, VariableRegistry variableRegistry, TokenCollector collector) {
    public <V> HandlerData<V> derive(Function<T, V> mapper) {
        return new HandlerData<>(mapper.apply(entity), variableRegistry, collector);
    }
}
