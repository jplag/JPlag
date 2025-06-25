package de.jplag.highlight_extraction;

import static de.jplag.highlight_extraction.StrategyMethods.createKey;

import java.util.List;
import java.util.Map;

import de.jplag.Match;

/**
 * Strategy that calculates the frequencies of matches over all submissions.
 */

public class CompleteMatchesStrategy implements FrequencyStrategy {

    /**
     * Adds all matches to a map using the token sequence as the key.
     * @param tokens List of tokens representing the match.
     * @param comparisonId Identifier for the comparison.
     * @param map Map that associates token subsequences with how often they occur across comparisons.
     * @param size The minimum sub length considered in other strategies.
     */

    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        String key = createKey(tokens);
        map.computeIfAbsent(key, k -> new java.util.ArrayList<>());
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
        String key = createKey(tokens);
        map.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(comparisonId);
    }

    @Override
    public double calculateWeight(Match match, Map<String, List<String>> frequencyMap, List<String> matchToken) {
        List<String> values = frequencyMap.get(createKey(matchToken));
//        if (values != null) {
//            System.out.println("key  gefunden");
//            System.out.println("frequency: " + values.size());
//        }
        return values != null ? values.size() : 0.0;
    }

}
