package de.jplag.highlight_extraction;

import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Strategy that calculates the frequencies of matches over all submissions.
 */

public class CompleteMatchesStrategy implements FrequencyStrategy {
    /**
      * Calculates the frequency of all matches and adds them to the map.
      * @param tokens List of tokens representing the match.
      * @param map Map that associates token subsequences with how often they occur across comparisons.
      * @param size The minimum sub length considered in other strategies.
      */
    @Override
    public void createFrequencymap(List<TokenType> tokens, Map<List<TokenType>, Integer> map, int size) {
        final List<TokenType> key = tokens;
        map.putIfAbsent(key, 0);
        int count = map.get(key) + 1;
        map.put(key, count);
    }
}
