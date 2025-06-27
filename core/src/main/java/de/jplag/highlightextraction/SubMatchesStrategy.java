package de.jplag.highlightextraction;

import static de.jplag.highlightextraction.SubSequenceUtil.getSubSequences;

import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Strategy that uses submatches of matches from the comparisons and calculates their frequency in the matches across
 * all submissions.
 */
public class SubMatchesStrategy implements FrequencyStrategy {

    /**
     * Creates submatches to build the keys and adds their frequencies to the frequencyMap.
     * @param frequencyMap Map that contains token subsequences and how often they occur across comparisons.
     * @param matchTokenTypes List of matchTokenTypes representing the match.
     * @param strategyNumber Minimum length of the considered submatches.
     */
    @Override
    public void addMatchToFrequencyMap(List<TokenType> matchTokenTypes, Map<List<TokenType>, Integer> frequencyMap, int strategyNumber) {
        List<List<TokenType>> subSequences = getSubSequences(matchTokenTypes, strategyNumber);
        for (List<TokenType> subSequence : subSequences) {
            frequencyMap.put(subSequence, frequencyMap.getOrDefault(subSequence, 0) + 1);
        }

    }
}
