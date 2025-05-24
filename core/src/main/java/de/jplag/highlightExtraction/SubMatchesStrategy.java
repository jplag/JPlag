package de.jplag.highlightExtraction;

import de.jplag.JPlagComparison;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Strategy that uses submatches of matches from the comparisons
 * and calculates their frequency in the matches across all submissions.
 */

public class SubMatchesStrategy implements FrequencyStrategy{

    /**
     * Adds all submatches with min size length of the matches to a map using the token sequence as the key.
     *
     * @param tokens       Token list of the match.
     * @param comparisonId Identifier for the comparison.
     * @param map          Map that contains token subsequences and how often they occur across comparisons.
     * @param size         Minimum length of the considered submatches.
     */

    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        if (tokens.size() >= size) {
            for (int j = size; j <= tokens.size(); j++) {
                applyWindowCreate(map, comparisonId, tokens, j);
            }
        }
        addToMap(map, comparisonId, tokens);
    }

    /**
     * Calculates the frequency of all submatches and adds them to the map.
     *
     * @param tokens       Token list of the match.
     * @param comparisonId Identifier for the comparison.
     * @param map          Map that contains token subsequences and how often they occur across comparisons.
     * @param size         Minimum length of the considered submatches.
     */

    @Override
    public void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        if (tokens.size() >= size) {
            for (int i = size; i <= tokens.size(); i++) {
                applyWindowCheck(map, comparisonId, tokens, i);
            }
        }
    }

    /**
     * Creates the submatches to build the keys.
     *
     * @param map          Map that contains token subsequences and how often they occur across comparisons.
     * @param comparisonId Identifier for the comparison.
     * @param tokens       Token list of the match.
     * @param size         Minimum length of the considered submatches.
     */

    private void applyWindowCreate(Map<String, List<String>> map,
                                   String comparisonId,
                                   List<String> tokens,
                                   int size) {
        LinkedList<String> copy = new LinkedList<>(tokens);
        while (copy.size() >= size) {
            List<String> subList = copy.subList(0, size);
            addToMap(map, comparisonId, subList);
            copy.removeFirst();
        }
    }

    /**
     * Creates submatches to build the keys and adds their frequencies to the map.
     *
     * @param map          Map that contains token subsequences and how often they occur across comparisons.
     * @param comparisonId Identifier for the comparison.
     * @param tokens       List of tokens representing the match.
     * @param size         Minimum length of the considered submatches.
     */

    private void applyWindowCheck(Map<String, List<String>> map,
                                  String comparisonId,
                                  List<String> tokens,
                                  int size) {
        LinkedList<String> copy = new LinkedList<>(tokens);
        while (copy.size() >= size) {
            List<String> subList = copy.subList(0, size);
            checkInMap(map, comparisonId, subList);
            copy.removeFirst();
        }
    }

    /**
     * Builds the map by adding token subsequences from a match.
     *
     * @param map          Map that contains token subsequences and how often they occur across comparisons.
     * @param comparisonId Identifier for the comparison.
     * @param tokens       Token list of the match.
     */

    private void addToMap(Map<String, List<String>> map,
                          String comparisonId,
                          List<String> tokens) {
        String key = String.join(" ", tokens);
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(comparisonId);
    }

    /**
     * Adds the frequency of the substring to the map.
     *
     * @param map          Map that contains token subsequences and how often they occur across comparisons.
     * @param comparisonId Identifier for the comparison.
     * @param tokens       Token list of the match.
     */

    private void checkInMap(Map<String, List<String>> map,
                            String comparisonId,
                            List<String> tokens) {
        String key = String.join(" ", tokens);
        List<String> existing = map.get(key);
        if (existing == null ) {
            return;
        }
        existing.add(comparisonId);
    }
}
