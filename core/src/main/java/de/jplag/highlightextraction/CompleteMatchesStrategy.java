package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that calculates the frequencies of matches across all submissions. For each match, the full token sequence
 * is added to the frequency map without modification.
 */
public class CompleteMatchesStrategy implements FrequencyStrategy {
    /**
     * Adds the given token sequence to the map and updates its frequency.
     * @param matchTokenTypes List of tokensTypes representing the match.
     * @param frequencyMap Map that associates token subsequences with the number of times they occur across comparisons.
     * @param strategyNumber Ignored in this strategy. The minimum sub length considered in other strategies.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Map<List<TokenType>, Integer> frequencyMap, int strategyNumber) {
        Consumer<List<TokenType>> sequenceConsumer = seq -> SubSequenceUtil.addSequence(frequencyMap, seq);
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> sequenceConsumer, int strategyNumber) {
        sequenceConsumer.accept(matchTokenTypes);
    }
}
