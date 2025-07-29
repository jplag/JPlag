package de.jplag.highlightextraction;

import java.util.List;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that counts all occurrences of complete matches inside all complete matches and contiguous submatches from
 * the comparisons, if the submatches are longer than minSubSequenceLength.
 */
public class ContainedMatchesStrategy implements FrequencyStrategy {
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
        List<List<TokenType>> subSequences = SubSequenceUtil.getSubSequences(matchTokenTypes, minSubSequenceLength);
        for (List<TokenType> subSequence : subSequences) {
            addSequenceKey.accept(subSequence);
        }
        if (matchTokenTypes.size() >= minSubSequenceLength) {
            addSequence.accept(matchTokenTypes);
        }
    }

}
