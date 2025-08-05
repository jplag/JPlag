package de.jplag.antlr;

import java.util.function.Function;

/**
 * Delegates visiting of a given antlr entity to a visitor for a different antlr entity.
 * @param <T> The original antlr type visited
 * @param <V> The target antlr type
 */
public class DelegateVisitor<T, V> {
    private final AbstractVisitor<V> delegate;
    private boolean mapOnExit;
    protected final Function<T, V> mapper;

    /**
     * @param delegate The target visitor to use
     * @param mapper The mapper function used to derive the target entity
     */
    public DelegateVisitor(AbstractVisitor<V> delegate, Function<T, V> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
        this.mapOnExit = false;
    }

    /**
     * Delegates entering the original context.
     * @param parentData The data of the original visitor
     */
    public void delegateEnter(HandlerData<T> parentData) {
        if (!this.mapOnExit) {
            this.delegate.enter(parentData.derive(this.mapper));
        }
    }

    /**
     * Makes this visitor map exit events to enter events. Used mostly for mapping exit events to terminal nodes, which only
     * provide enter events.
     */
    public void mapOnExit() {
        this.mapOnExit = true;
    }

    /**
     * Delegates exiting the original context.
     * @param parentData The data of the original visitor
     */
    public void delegateExit(HandlerData<T> parentData) {
        if (this.mapOnExit) {
            this.delegate.enter(parentData.derive(this.mapper));
        }
    }

    /**
     * Checks if the target entity is present in the given antlr entity.
     * @param entity The original antlr entity
     * @return is present
     */
    public boolean isPresent(T entity) {
        try {
            return this.mapper.apply(entity) != null;
        } catch (Exception e) { // If something goes wrong during mapping, the delegate is not present
            return false;
        }
    }
}
