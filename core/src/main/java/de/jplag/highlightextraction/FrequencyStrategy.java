package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Interface for different frequency calculation strategies. Implementations define how submatches are considered in the
 * frequency calculation of matches.
 */
public interface FrequencyStrategy {
    /**
     * Fills the frequencyMap with token subsequences and their frequencies according to the implemented strategy.
     * @param matchTokenTypes List of matchTokenTypes representing the match.
     * @param frequencyMap Map that associates token subsequences with the comparisons they appear in.
     * @param strategyNumber The minimum length of token subsequences to consider.
     */
    void processMatchTokenTypes(List<TokenType> matchTokenTypes, Map<List<TokenType>, Integer> frequencyMap, int strategyNumber);
}
