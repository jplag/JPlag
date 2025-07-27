package de.jplag.highlightextraction;

import java.util.List;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that counts all occurrences of complete matches and continuos submatches from the comparisons, if the
 * matches and submatches are longer than minSubSequenceLength.
 */
public class SubMatchesStrategy implements FrequencyStrategy {

    /**
     * Creates submatches to build the keys and adds their frequencies to the frequencyMap.
     * @param matchTokenTypes List of matchTokenTypes representing the match.
     * @param addSequenceKey<TokenType>> addSequenceKey adds the Sequence to the list, without counting the frequency
     * @param addSequence<TokenType>> addSequence adds the Sequence to the list, and updates the frequency
     * @param minSubSequenceSize Minimum length of the considered submatches.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> addSequenceKey,
            Consumer<List<TokenType>> addSequence, int minSubSequenceSize) {
        List<List<TokenType>> subSequences = SubSequenceUtil.getSubSequences(matchTokenTypes, minSubSequenceSize);
        for (List<TokenType> subSequence : subSequences) {
            addSequence.accept(subSequence);
        }

    }
}
