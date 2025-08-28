package de.jplag.highlightextraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.Match;
import de.jplag.TokenType;

/**
 * Strategy that counts all occurrences of complete matches and contiguous submatches from the comparisons, if the
 * matches and submatches are longer than minSubSequenceLength.
 */
public class SubMatchesStrategy implements FrequencyStrategy {
    int minSubSequenceLength;

    /**
     * Creates submatches to build the keys and adds their frequencies to the frequencyMap.
     * @param matchTokenTypes List of matchTokenTypes representing the match.
     * @param addSequenceKey Consumer that adds the sequence to the list, without counting the frequency.
     * @param addSequence Consumer that adds the sequence to the list, and updates the frequency.
     * @param minSubSequenceSize Minimum length of the considered submatches.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> addSequenceKey,
            Consumer<List<TokenType>> addSequence, int minSubSequenceSize) {
        this.minSubSequenceLength = minSubSequenceSize;
        List<List<TokenType>> subSequences = SubSequenceUtil.getSubSequences(matchTokenTypes, minSubSequenceSize);
        for (List<TokenType> subSequence : subSequences) {
            addSequence.accept(subSequence);
        }

    }

    /**
     * Calculates the weight of a match considering subsequences of the match
     * @param match Considered match
     * @param frequencyMap Frequency map build with processMatchTokenTypes method
     * @param matchToken tokenType sequence of the match
     * @return a weight for the match
     */
    @Override
    public double calculateWeight(Match match, Map<List<TokenType>, Integer> frequencyMap, List<TokenType> matchToken) {
        List<List<TokenType>> keys = SubSequenceUtil.getSubSequences(matchToken, minSubSequenceLength);
        SubSequenceUtil.getSubSequences(matchToken, minSubSequenceLength);
        List<Integer> frequencies = new ArrayList<>();
        for (List<TokenType> key : keys) {
            int freq = frequencyMap.getOrDefault(key, 0);
            frequencies.add(freq);
        }

        return frequencies.stream().mapToInt(Integer::intValue).average().orElse(0.0);

    }
}
