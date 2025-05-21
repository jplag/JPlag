package de.jplag.highlightExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy that uses a fixed window size to create submatches of a match sequence in a comparison
 * and calculates the frequencies over all submissions.
 */

public class WindowOfMatchesStrategy implements FrequencyStrategy{

    /**
     * Adds all submatches with window length of the matches to a map using the token sequence as the key.
     *
     * @param tokens       Token list of the match.
     * @param comparisonId Identifier for the comparison.
     * @param map          Map that contains token subsequences and how often they occur across comparisons.
     * @param size         The length of the considered token window.
     */

    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        for (int i = 0; i <= tokens.size() - size; i++) {
            List<String> window = tokens.subList(i, i + size);
            String key = String.join(" ", window);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(comparisonId);
        }
    }

    /**
     * Calculates the frequency of all submatches and adds them to the map.
     *
     * @param tokens       Token list of the match.
     * @param comparisonId Identifier for the comparison.
     * @param map          Map that contains token subsequences and how often they occur across comparisons.
     * @param size         The length of the considered token window.
     */

    @Override
    public void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        for (int i = 0; i <= tokens.size() - size; i++) {
            List<String> window = tokens.subList(i, i + size);
            String key = String.join(" ", window);
            if (!(map.containsKey(key) && map.get(key).contains(comparisonId))) {
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(comparisonId);
            }
        }
    }
}
