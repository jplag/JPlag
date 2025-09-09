package de.jplag.reporting.csv.comparisons;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Maps the names of submissions for csv printing. Used to anonymize data if needed.
 */
public interface NameMapper {
    /**
     * Maps the original name to the one that should be printed.
     * @param original The original name
     * @return The name for printing
     */
    String map(String original);

    /**
     * @return The list of mappings.
     */
    List<Map.Entry<String, String>> getNameMap();

    /**
     * Simple implementation, that does not change the names.
     */
    class IdentityMapper implements NameMapper {
        @Override
        public String map(String original) {
            return original;
        }

        @Override
        public List<Map.Entry<String, String>> getNameMap() {
            return Collections.emptyList();
        }
    }
}
