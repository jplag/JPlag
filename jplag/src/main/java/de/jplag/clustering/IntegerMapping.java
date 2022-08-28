package de.jplag.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a mapping of any set to integers
 */
public class IntegerMapping<T> {

    private final Map<T, Integer> mapping;
    private final List<T> backMapping;
    private int size = 0;

    public IntegerMapping(int initialCapacity) {
        mapping = new HashMap<>(initialCapacity);
        backMapping = new ArrayList<>(initialCapacity);
    }

    /**
     * @param key is added to the mapping (if not already present)
     * @return the associated integer
     */
    public int map(T key) {
        mapping.computeIfAbsent(key, it -> {
            backMapping.add(it);
            return size++;
        });
        return mapping.get(key);
    }

    /**
     * Maps the integer back to the original set.
     * @param index the integer
     * @return the original value
     */
    public T unmap(int index) {
        return backMapping.get(index);
    }

    /**
     * @return Number of unique values in the mapping
     */
    public int size() {
        return size;
    }
}
