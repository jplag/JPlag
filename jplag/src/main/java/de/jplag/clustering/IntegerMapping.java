package de.jplag.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Creates a mapping of any set to integers
 */
public class IntegerMapping<T> {

    private HashMap<T, Integer> mapping = new HashMap<>();
    private List<T> backMapping = new ArrayList<>();
    private int size = 0;

    public IntegerMapping(int initialCapacity) {
        mapping = new HashMap<>(initialCapacity);
        backMapping = new ArrayList<>(initialCapacity);
    }

    /**
     * @param value is added to the mapping (if not already present)
     * @return the associated integer
     */
    public int map(T value) {
        Integer result = mapping.get(value);
        if (result == null) {
            int newIndex = size++;
            mapping.put(value, newIndex);
            backMapping.add(value);
            return newIndex;
        }
        return result;
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
