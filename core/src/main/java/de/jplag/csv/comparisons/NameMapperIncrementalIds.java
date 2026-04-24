package de.jplag.csv.comparisons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps the real names of submissions to incremental ids. The ids will be in order of the queried new names.
 */
public class NameMapperIncrementalIds implements NameMapper {
    private final Map<String, String> map;
    private int nextId;

    /**
     * Creates a new instance.
     */
    public NameMapperIncrementalIds() {
        this.map = new HashMap<>();
        this.nextId = 0;
    }

    private String newId() {
        String id = String.valueOf(this.nextId++);

        if (this.map.containsKey(id)) {
            return newId();
        }

        return id;
    }

    @Override
    public String map(String original) {
        this.map.computeIfAbsent(original, ignore -> this.newId());
        return this.map.get(original);
    }

    @Override
    public List<Map.Entry<String, String>> getNameMap() {
        return this.map.entrySet().stream().toList();
    }
}
