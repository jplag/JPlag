package de.jplag.highlightextraction;

import static de.jplag.highlightextraction.SubSequenceUtil.getSubSequences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.Match;
import de.jplag.TokenType;

/**
 * Strategy that counts all occurrences of complete matches inside all complete matches and contiguous submatches from
 * the comparisons, if the submatches are longer than minSubSequenceLength.
 */
public class ContainedMatchesStrategy implements FrequencyStrategy {
    /**
     * Minimum considered subsequence length.
     */
    private int minSubSequenceLength;

    /**
     * Adds all submatches of the given match to the map if their length is at least minSubSequenceLength long, using the
     * token sequence as key. The full match itself is also added if it is at least minSubSequenceLength.
     * @param matchTokenTypes List of tokens representing the match.
     * @param addSequenceKey Consumer that adds the sequence to the list, without counting the frequency
     * @param addSequence Consumer that adds the sequence to the list, and updates the frequency
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> addSequenceKey,
            Consumer<List<TokenType>> addSequence, int minSubSequenceSize) {
        minSubSequenceLength = minSubSequenceSize;
        List<List<TokenType>> subSequences = getSubSequences(matchTokenTypes, minSubSequenceSize);
        for (List<TokenType> subSequence : subSequences) {
            addSequenceKey.accept(subSequence);
        }
        if (matchTokenTypes.size() >= minSubSequenceSize) {
            addSequence.accept(matchTokenTypes);
        }
    }

    /**
     * Calculates the weight of a match considering subsequences of the match.
     * @param match Considered match
     * @param frequencyMap Frequency map build with processMatchTokenTypes method
     * @param matchToken tokenType sequence of the match
     * @return a weight for the match
     */
    @Override
    public double calculateMatchFrequency(Match match, Map<List<TokenType>, Integer> frequencyMap, List<TokenType> matchToken) {
        List<List<TokenType>> subSequences = getSubSequences(matchToken, minSubSequenceLength);
        List<Integer> frequencies = new ArrayList<>();
        for (List<TokenType> subsequence : subSequences) {
            Integer subSequenceFrequency = frequencyMap.get(subsequence);
            if (subSequenceFrequency != null && subSequenceFrequency != 0) {
                frequencies.add(subSequenceFrequency);
            }
        }
        return frequencies.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
