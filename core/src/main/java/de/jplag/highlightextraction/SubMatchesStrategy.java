package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that uses submatches of matches from the comparisons and calculates the frequency of all submatches in the
 * matches across all submissions. The full Match is also considered a submatch if it is longer or equals to
 * minSubSequenceSize.
 */
public class SubMatchesStrategy implements FrequencyStrategy {

    /**
     * Creates submatches to build the keys and adds their frequencies to the frequencyMap.
     * @param frequencyMap Map that contains token subsequences and how often they occur across comparisons.
     * @param matchTokenTypes List of matchTokenTypes representing the match.
     * @param minSubSequenceSize Minimum length of the considered submatches.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Map<List<TokenType>, Integer> frequencyMap, int minSubSequenceSize) {
        Consumer<List<TokenType>> sequenceConsumer = sequence -> SubSequenceUtil.addSequence(frequencyMap, sequence);
        List<List<TokenType>> subSequences = SubSequenceUtil.getSubSequences(matchTokenTypes, minSubSequenceSize);
        for (List<TokenType> subSequence : subSequences) {
            sequenceConsumer.accept(subSequence);
        }

    }
}
