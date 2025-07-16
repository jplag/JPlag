package de.jplag.highlightextraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Utility class that contains the methods that are used by more than one Strategy.
 */
public final class SubSequenceUtil {
    private SubSequenceUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Calculates all possible sublists with min minSubSequenceSize length of the matchTokenTypes.
     * @param matchTokenTypes is a List of the match, in TokenTypes, to create the SubSequences used for the
     * SubSequenceStrategy and ContainedStrategy. The original list is also included. All contiguous (Sub-)lists, that can
     * be taken from the matchTokenTypes list that have length greater or equal minSubSequenceSize.
     * @param minSubSequenceSize is the minimum considered size of the sub-Sequence-List.
     * @return List of all as considered subSequences.
     */
    public static List<List<TokenType>> getSubSequences(List<TokenType> matchTokenTypes, int minSubSequenceSize) {
        int possibleSubSequenceStartPositions = matchTokenTypes.size() - minSubSequenceSize + 1;
        int subSequencyCount = possibleSubSequenceStartPositions * (possibleSubSequenceStartPositions + 1) / 2;
        List<List<TokenType>> subSequences = new ArrayList<>(subSequencyCount);
        if (matchTokenTypes.size() >= minSubSequenceSize) {
            for (int subSequenceSize = minSubSequenceSize; subSequenceSize <= matchTokenTypes.size(); subSequenceSize++) {
                for (int windowStartIndex = 0; windowStartIndex <= matchTokenTypes.size() - subSequenceSize; windowStartIndex++) {
                    List<TokenType> subSequence = new ArrayList<>(matchTokenTypes.subList(windowStartIndex, windowStartIndex + subSequenceSize));
                    subSequences.add(subSequence);
                }
            }
        }
        return subSequences;
    }

    /**
     * Updates the frequency of the given sequence in the frequency map.
     * @param frequencyMap Map of sequences to their frequency counts.
     * @param sequence The token sequence whose frequency will be updated.
     */
    static void addSequence(Map<List<TokenType>, Integer> frequencyMap, List<TokenType> sequence) {
        frequencyMap.put(sequence, frequencyMap.getOrDefault(sequence, 0) + 1);
    }
}
