package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that extracts submatches from matches if they are longer than minSubSequenceLength from the comparisons and
 * calculates how often they occur across all submissions.
 */
public class ContainedMatchesStrategy implements FrequencyStrategy {
    /**
     * Adds all submatches of the given match to the map if their length is at least minSubSequenceLength long, using the
     * token sequence as key. The full match itself is also added if it is at least minSubSequenceLength.
     * @param matchTokenTypes List of tokens representing the match.
     * @param frequencyMap Map that contains token subsequences and how often they occur across comparisons.
     * @param minSubSequenceLength Minimum length of the considered submatches.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Map<List<TokenType>, Integer> frequencyMap, int minSubSequenceLength) {
        Consumer<List<TokenType>> sequenceConsumer = seq -> SubSequenceUtil.addSequence(frequencyMap, seq);
        List<List<TokenType>> subSequences = SubSequenceUtil.getSubSequences(matchTokenTypes, minSubSequenceLength);
        for (List<TokenType> subSequence : subSequences) {
            frequencyMap.putIfAbsent(subSequence, 0);
        }
        if (matchTokenTypes.size() >= minSubSequenceLength) {
            sequenceConsumer.accept(matchTokenTypes);
        }
    }

}
