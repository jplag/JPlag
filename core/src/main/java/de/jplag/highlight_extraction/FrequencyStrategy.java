package de.jplag.highlight_extraction;

import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Interface for different frequency calculation strategies. Implementations define how submatches are considered in the
 * frequency calculation of matches.
 */

public interface FrequencyStrategy {
     /**
     * Fills the map with token subsequences and their frequencies according to the implemented strategy.
     * @param tokens List of tokens representing the match.
     * @param map Map that associates token subsequences with the comparisons they appear in.
     * @param size The minimum length of token subsequences to consider.
     */
    void createFrequencymap(List<TokenType> tokens, Map<List<TokenType>, Integer> map, int size);
}
