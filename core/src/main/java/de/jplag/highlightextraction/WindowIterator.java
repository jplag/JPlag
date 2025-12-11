package de.jplag.highlightextraction;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator for sublists of constant lengths.
 * @param <T> is the type of items.
 */
public class WindowIterator<T> implements Iterator<List<T>> {

    private final List<T> items;
    private final int windowLength;
    private int index;

    /**
     * Constructs a new {@link WindowIterator} over the given list of items using the given window length.
     * @param items are the items.
     * @param windowLength is the window length.
     */
    public WindowIterator(List<T> items, int windowLength) {
        this.items = items;
        this.index = 0;
        this.windowLength = windowLength;
    }

    @Override
    public boolean hasNext() {
        return index + windowLength <= items.size();
    }

    @Override
    public List<T> next() {
        return items.subList(index, index++ + windowLength);
    }
}
