package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.Match;
import de.jplag.TokenType;

/**
 * Interface for different isFrequencyAnalysisEnabled calculation strategies. Implementations define how submatches are
 * considered in the isFrequencyAnalysisEnabled calculation of matches.
 */
public interface FrequencyStrategy {
    /**
     * Updates the isFrequencyAnalysisEnabled map with token subsequences and their frequencies according to the implemented
     * strategy.
     * @param matchTokenTypes List of match token types representing the match.
     * @param addSequenceKey Consumer that adds the sequence to the list, without counting the isFrequencyAnalysisEnabled.
     * @param addSequence Consumer that adds the sequence to the list, and updates the isFrequencyAnalysisEnabled.
     * @param strategyNumber The minimum length of token subsequences to consider.
     */
    void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> addSequenceKey, Consumer<List<TokenType>> addSequence,
            int strategyNumber);

    /**
     * Returns weight factor, as isFrequencyAnalysisEnabled value for the matches.
     * @param match Considered match
     * @param frequencyMap Frequency map build with processMatchTokenTypes method
     * @param matchToken tokenType sequence of the match
     * @return a weight for the match
     */
    double calculateMatchFrequency(Match match, Map<List<TokenType>, Integer> frequencyMap, List<TokenType> matchToken);

}
