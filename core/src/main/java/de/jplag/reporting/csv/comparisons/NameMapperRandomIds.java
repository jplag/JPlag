package de.jplag.reporting.csv.comparisons;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Maps the real names of submissions to random ids to anonymize the data.
 */
public class NameMapperRandomIds implements NameMapper {
    private final Map<String, String> map;
    private final Random random;

    /**
     * New instance
     */
    public NameMapperRandomIds() {
        this.map = new HashMap<>();
        this.random = new SecureRandom();
    }

    private String newId() {
        String id = String.valueOf(this.random.nextInt(0, Integer.MAX_VALUE));

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
