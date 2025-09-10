package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.Match;
import de.jplag.TokenType;

/**
 * Strategy that calculates the frequencies of matches across all submissions. For each match, the complete token
 * sequence is added to the frequency map without modification. So the Strategy counts all occurrences of complete
 * matches inside all the complete matches of the comparisons.
 */
public class CompleteMatchesStrategy implements FrequencyStrategy {
    /**
     * Adds the given token sequence to the map and updates its frequency.
     * @param matchTokenTypes List of tokens types representing the match.
     * @param addSequenceKey Consumer that adds the sequence to the list, without counting the frequency
     * @param addSequence Consumer that adds the sequence to the list, and updates the frequency
     * @param minSubSequenceSize Ignored in this strategy. The minimum sub length considered in other strategies.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> addSequenceKey,
            Consumer<List<TokenType>> addSequence, int minSubSequenceSize) {
        addSequence.accept(matchTokenTypes);
    }

    /**
     * Calculates the weight of a match. According to the strategy.
     * @param match Considered match
     * @param frequencyMap Frequency map build with processMatchTokenTypes method
     * @param matchToken tokenType sequence of the match
     * @return a weight for the match
     */
    @Override
    public double calculateMatchFrequency(Match match, Map<List<TokenType>, Integer> frequencyMap, List<TokenType> matchToken) {
        Integer values = frequencyMap.get(matchToken);
        return values != null ? values : 0.0;
    }
}
