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
    int minSubSequenceLength;

    /**
     * Adds all submatches of the given match to the map if their length is at least minSubSequenceLength long, using the
     * token sequence as key. The full match itself is also added if it is at least minSubSequenceLength.
     * @param matchTokenTypes List of tokens representing the match.
     * @param addSequenceKey Consumer that adds the sequence to the list, without counting the frequency
     * @param addSequence Consumer that adds the sequence to the list, and updates the frequency
     * @param minSubSequenceLength Minimum length of the considered submatches.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> addSequenceKey,
            Consumer<List<TokenType>> addSequence, int minSubSequenceLength) {
        this.minSubSequenceLength = minSubSequenceLength;
        List<List<TokenType>> subSequences = getSubSequences(matchTokenTypes, minSubSequenceLength);
        for (List<TokenType> subSequence : subSequences) {
            addSequenceKey.accept(subSequence);
        }
        if (matchTokenTypes.size() >= minSubSequenceLength) {
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
        List<List<TokenType>> keys = getSubSequences(matchToken, minSubSequenceLength);
        List<Integer> frequencies = new ArrayList<>();
        for (List<TokenType> key : keys) {
            frequencies.add(frequencyMap.getOrDefault(key, 0));
        }
        return frequencies.stream().filter(freq -> freq > 0).mapToInt(Integer::intValue).average().orElse(0.0);
    }

}
