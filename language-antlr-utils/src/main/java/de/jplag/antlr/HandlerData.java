package de.jplag.antlr;

import java.util.function.Function;

import de.jplag.semantics.VariableRegistry;

/**
 * Holds the data passed to the (quasi-static) listeners.
 * @param <T> The type of the entity stored in this handler data.
 * @param entity The current entity being processed.
 * @param variableRegistry Registry of variables used for parsing or analysis.
 * @param collector Collector used to accumulate tokens or related parsing data.
 */
public record HandlerData<T>(T entity, VariableRegistry variableRegistry, TokenCollector collector) {

    /**
     * Transforms the data of type {@code T} into data of type {@code V} using the provided mapper.
     * @param <V> The target type of the transformed data.
     * @param mapper A function that maps the current data to a new one.
     * @return A new {@code HandlerData} instance containing the transformed data.
     */
    public <V> HandlerData<V> derive(Function<T, V> mapper) {
        return new HandlerData<>(mapper.apply(entity), variableRegistry, collector);
    }
}
