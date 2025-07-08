package de.jplag.highlight_extraction;

import static de.jplag.highlight_extraction.StrategyMethods.createKey;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.jplag.Match;

/**
 * Strategy that uses a fixed window size to create submatches of a match sequence in a comparison and calculates the
 * frequencies over all submissions.
 */

public class WindowOfMatchesStrategy implements FrequencyStrategy {
    public static int size;

    /**
     * Adds all submatches with window length of the matches to a map using the token sequence as the key.
     * @param tokens Token list of the match.
     * @param comparisonId Identifier for the comparison.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param size The length of the considered token window.
     */

    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        this.size = size;
        for (int i = 0; i <= tokens.size() - size; i++) {
            List<String> window = tokens.subList(i, i + size);
            String key = String.join(" ", window);
            map.computeIfAbsent(key, k -> new ArrayList<>());
        }
    }

    /**
     * Calculates the frequency of all submatches and adds them to the map.
     * @param tokens Token list of the match.
     * @param comparisonId Identifier for the comparison.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param size The length of the considered token window.
     */

    @Override
    public void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) throws IllegalStateException {
        for (int i = 0; i <= tokens.size() - size; i++) {
            List<String> window = tokens.subList(i, i + size);
            String key = String.join(" ", window);
            if (!map.containsKey(key)) {
                throw new IllegalStateException("Unexpected window found in check() : " + key);
            }
            map.get(key).add(comparisonId);
        }
    }

    public static List<String> generateAllWindowKeys(List<String> tokens) {
        List<String> keys = new ArrayList<>();
        LinkedList<String> copy = new LinkedList<>(tokens);
        while (copy.size() >= size) {
            List<String> subList = copy.subList(0, size);
            keys.add(createKey(subList));
            copy.removeFirst();
        }
        return keys;
    }

    @Override
    public double calculateWeight(Match match, Map<String, List<String>> frequencyMap, List<String> matchToken) {
        List<String> keys = generateAllWindowKeys(matchToken);
        List<Integer> frequencies = new ArrayList<>();
        for (String key : keys) {
            frequencies.add(frequencyMap.get(key).size());
        }

        return frequencies.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
