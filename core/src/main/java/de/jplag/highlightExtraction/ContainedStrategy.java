package de.jplag.highlightExtraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Strategy that uses submatches from the comparisons and calculates the frequency of their appearance in matches across
 * all submissions.
 */

public class ContainedStrategy implements FrequencyStrategy {

    /**
     * Adds all submatches with min size length of the matches to a map using the token sequence as the key.
     * @param tokens Token list of the match.
     * @param comparisonId Identifier for the comparison.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param size Minimum length of the considered submatches.
     */

    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        if (tokens.size() >= size) {
            for (int j = size; j <= tokens.size(); j++) {
                applyWindowCreate(map, comparisonId, tokens, j);
            }
            addToMap(map, tokens);
        }

    }

    /**
     * Calculates the frequency of all matches and adds them to the map.
     * @param tokens List of tokens representing the match.
     * @param comparisonId Identifier for the comparison.
     * @param map Map that associates token subsequences with how often they occur across comparisons.
     * @param size The minimum sub length considered in other strategies.
     */

    @Override
    public void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        if (tokens.size() >= size) {
            String key = String.join(" ", tokens);
            List<String> idList = map.get(key);
            if (idList == null) {
                throw new IllegalStateException("Key not found in map: " + key);
            }
            idList.add(comparisonId);
        }

    }

    /**
     * Creates the submatches to build the keys.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param comparisonId Identifier for the comparison.
     * @param tokens Token list of the match.
     * @param size Minimum length of the considered submatches.
     */

    private void applyWindowCreate(Map<String, List<String>> map, String comparisonId, List<String> tokens, int size) {
        LinkedList<String> copy = new LinkedList<>(tokens);
        while (copy.size() >= size) {
            List<String> subList = copy.subList(0, size);
            addToMap(map, subList);
            copy.removeFirst();
        }
    }

    /**
     * Builds the map by adding token subsequences from a match.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param tokens Token list of the match.
     */

    private void addToMap(Map<String, List<String>> map, List<String> tokens) {
        String key = String.join(" ", tokens);
        map.computeIfAbsent(key, k -> new ArrayList<>());
    }
}
