package de.jplag.highlightextraction;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator for all sublists down to a minimum length.
 * @param <T> is the type of the list items.
 */
public class SublistIterator<T> implements Iterator<List<T>> {

    private final List<T> items;
    private final int minLength;
    private int index;
    private int length;

    /**
     * Creates a new {@link SublistIterator} for the given list of items and the given minimum sublist length.
     * @param items are the items.
     * @param minLength is the minimum sublist length.
     */
    public SublistIterator(List<T> items, int minLength) {
        this.items = items;
        this.index = 0;
        this.length = items.size();
        this.minLength = minLength;
    }

    @Override
    public boolean hasNext() {
        return index >= 0;
    }

    @Override
    public List<T> next() {

        List<T> next = items.subList(index, index + length);
        if (index > 0) {
            index--;
        } else if (length > minLength) {
            length--;
            index = items.size() - length;
        } else {
            index = -1;
        }
        return next;
    }
}
