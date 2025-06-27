package de.jplag.highlightextraction;

import static de.jplag.highlightextraction.SubSequenceUtil.getSubSequences;

import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Strategy that uses submatches from the comparisons and calculates the frequency of their appearance in matches across
 * all submissions.
 */

public class ContainedMatchesStrategy implements FrequencyStrategy {
    /**
     * Adds all submatches with min size length of the matches to a map using the token sequence as the key.
     * @param matchTokenTypes Token list of the match.
     * @param frequencyMap Map that contains token subsequences and how often they occur across comparisons.
     * @param strategyNumber Minimum length of the considered submatches.
     */
    @Override
    public void addMatchToFrequencyMap(List<TokenType> matchTokenTypes, Map<Integer, Integer> frequencyMap, int strategyNumber) {
        List<List<TokenType>> subSequences = getSubSequences(matchTokenTypes, strategyNumber);
        for (List<TokenType> subSequence : subSequences) {
            frequencyMap.putIfAbsent(subSequence.hashCode(), 0);
        }
        if (matchTokenTypes.size() >= strategyNumber) {
            frequencyMap.put(matchTokenTypes.hashCode(), frequencyMap.getOrDefault(matchTokenTypes.hashCode(), 0) + 1);
        }
    }

}
