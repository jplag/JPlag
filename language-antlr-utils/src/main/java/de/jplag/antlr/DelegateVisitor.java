package de.jplag.antlr;

import java.util.function.Function;

public class DelegateVisitor<T, V> {
    private final AbstractVisitor<V> delegate;
    protected final Function<T, V> mapper;
    private boolean mapOnExit;

    public DelegateVisitor(AbstractVisitor<V> delegate, Function<T, V> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
        this.mapOnExit = false;
    }

    public void delegateEnter(HandlerData<T> parentData) {
        if (!this.mapOnExit) {
            this.delegate.enter(parentData.derive(this.mapper));
        }
    }

    public void mapOnExit() {
        this.mapOnExit = true;
    }

    public void delegateExit(HandlerData<T> parentData) {
        if (this.mapOnExit) {
            this.delegate.enter(parentData.derive(this.mapper));
        }
    }

    public boolean isPresent(T entity) {
        try {
            return this.mapper.apply(entity) != null;
        } catch (Exception e) { //If something goes wrong during mapping, the delegate is not present
            return false;
        }
    }
}
