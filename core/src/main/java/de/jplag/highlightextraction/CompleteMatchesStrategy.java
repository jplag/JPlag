package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Strategy that calculates the frequencies of matches over all submissions.
 */

public class CompleteMatchesStrategy implements FrequencyStrategy {
    /**
     * Calculates the frequency of all matches and adds them to the map.
     * @param matchTokenTypes List of tokens representing the match.
     * @param frequencyMap Map that associates token subsequences with how often they occur across comparisons.
     * @param strategyNumber The minimum sub length considered in other strategies.
     */
    @Override
    public void addMatchToFrequencyMap(List<TokenType> matchTokenTypes, Map<List<TokenType>, Integer> frequencyMap, int strategyNumber) {
        frequencyMap.put(matchTokenTypes, frequencyMap.getOrDefault(matchTokenTypes, 0) + 1);
    }
}
