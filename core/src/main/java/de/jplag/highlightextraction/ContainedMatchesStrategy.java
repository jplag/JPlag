package de.jplag.highlightextraction;

import static de.jplag.highlightextraction.SubSequenceUtil.addSequence;
import static de.jplag.highlightextraction.SubSequenceUtil.getSubSequences;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that uses matches and their submatches, that are longer than minSubSequenceLength from the comparisons and
 * calculates the frequency of their appearance in full matches across all submissions.
 */
public class ContainedMatchesStrategy implements FrequencyStrategy {
    /**
     * Adds all submatches with min size length of the matches to a map using the token sequence as the key.
     * @param matchTokenTypes Token list of the match.
     * @param frequencyMap Map that contains token subsequences and how often they occur across comparisons.
     * @param minSubSequenceLength Minimum length of the considered submatches.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Map<Integer, Integer> frequencyMap, int minSubSequenceLength) {
        Consumer<List<TokenType>> sequenceConsumer = seq -> addSequence(frequencyMap, seq);
        List<List<TokenType>> subSequences = getSubSequences(matchTokenTypes, minSubSequenceLength);
        for (List<TokenType> subSequence : subSequences) {
            frequencyMap.putIfAbsent(subSequence.hashCode(), 0);
        }
        if (matchTokenTypes.size() >= minSubSequenceLength) {
            sequenceConsumer.accept(matchTokenTypes);
        }
    }

}
