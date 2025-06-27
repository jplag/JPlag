package de.jplag.highlightextraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Strategy that uses a fixed window size to create submatches of a match sequence in a comparison and calculates the
 * frequencies over all submissions.
 */

public class WindowOfMatchesStrategy implements FrequencyStrategy {

    /**
     * Adds all submatches with window length of the matches to a map using the token sequence as the key.
     * @param matchTokenTypes Token list of the match.
     * @param frequencyMap Map that contains token subsequences and how often they occur across comparisons.
     * @param strategyNumber The length of the considered token window.
     */
    @Override
    public void addMatchToFrequencyMap(List<TokenType> matchTokenTypes, Map<Integer, Integer> frequencyMap, int strategyNumber) {
        List<List<TokenType>> windowSequences = getWindowSequences(matchTokenTypes, strategyNumber);
        for (List<TokenType> windowSequence : windowSequences) {
            frequencyMap.put(windowSequence.hashCode(), frequencyMap.getOrDefault(windowSequence.hashCode(), 0) + 1);
        }
    }

    /**
     * Calculates all possible Sublists with length of windowSize
     * @param matchTokenTypes tokens Of the Match
     * @param windowSize considered windowSize of the Sublists
     * @return List of all as considered Sublists
     */
    public static List<List<TokenType>> getWindowSequences(List<TokenType> matchTokenTypes, int windowSize) {
        List<List<TokenType>> windowSequences = new LinkedList<>();
        if (matchTokenTypes.size() >= windowSize) {
            for (int windowStartIndex = 0; windowStartIndex <= matchTokenTypes.size() - windowSize; windowStartIndex++) {
                List<TokenType> windowSequence = new ArrayList<>(matchTokenTypes.subList(windowStartIndex, windowStartIndex + windowSize));
                windowSequences.add(windowSequence);
            }
        }
        return windowSequences;
    }
}
