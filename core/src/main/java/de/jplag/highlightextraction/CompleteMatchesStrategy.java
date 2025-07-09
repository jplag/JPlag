package de.jplag.highlightextraction;

import static de.jplag.highlightextraction.SubSequenceUtil.addSequence;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that calculates the frequencies of matches over all submissions.
 */
public class CompleteMatchesStrategy implements FrequencyStrategy {
    /**
     * Adds the frequency of the Match to the map.
     * @param matchTokenTypes List of tokensTypes representing the match.
     * @param frequencyMap Map that associates token subsequences with how often they occur across comparisons.
     * @param strategyNumber The minimum sub length considered in other strategies.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Map<Integer, Integer> frequencyMap, int strategyNumber) {
        Consumer<List<TokenType>> sequenceConsumer = seq -> addSequence(frequencyMap, seq);
        sequenceConsumer.accept(matchTokenTypes);
    }
}
