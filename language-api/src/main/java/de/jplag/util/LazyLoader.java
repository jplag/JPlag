package de.jplag.util;

import java.util.function.Supplier;

public class LazyLoader<T> {
    private final Supplier<T> supplier;
    private T value;
    private boolean retrieved;

    public LazyLoader(Supplier<T> supplier) {
        this.supplier = supplier;
        this.retrieved = false;
    }

    public T get() {
        if (!retrieved) {
            this.value = supplier.get();
            this.retrieved = true;
        }

        return this.value;
    }
}
