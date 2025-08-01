package de.jplag.highlightextraction;

import java.util.List;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Interface for different frequency calculation strategies. Implementations define how submatches are considered in the
 * frequency calculation of matches.
 */
public interface FrequencyStrategy {
    /**
     * Updates the frequency map with token subsequences and their frequencies according to the implemented strategy.
     * @param matchTokenTypes List of match token types representing the match.
     * @param addSequenceKey Consumer that adds the sequence to the list, without counting the frequency.
     * @param addSequence Consumer that adds the sequence to the list, and updates the frequency.
     * @param strategyNumber The minimum length of token subsequences to consider.
     */
    void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> addSequenceKey, Consumer<List<TokenType>> addSequence,
            int strategyNumber);
}
