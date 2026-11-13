package de.jplag.reporting.csv.comparisons;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Simple implementation, that does not change the names.
 */
public class IdentityMapper implements NameMapper {
    @Override
    public String map(String original) {
        return original;
    }

    @Override
    public List<Map.Entry<String, String>> getNameMap() {
        return Collections.emptyList();
    }
}
