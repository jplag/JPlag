package de.jplag.highlightExtraction;

import java.util.List;
import java.util.Map;

/**
 * Interface for different frequency calculation strategies.
 * Implementations define how submatches are considered in the frequency calculation of matches.
 */

public interface FrequencyStrategy {
    /**
     * Fills the map with token subsequences according to the implemented strategy.
     *
     * @param tokens       List of tokens representing the match.
     * @param comparisonId Identifier for the comparison.
     * @param map          Map that associates token subsequences with the comparisons they appear in.
     * @param size         The minimum length of token subsequences to consider.
     */
    void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size);
    /**
     * Updates the map with frequencies of token subsequences according to the implemented strategy.
     *
     * @param tokens       List of tokens representing the match.
     * @param comparisonId Identifier for the comparison.
     * @param map          Map that associates token subsequences with the comparisons they appear in.
     * @param size         The minimum length of token subsequences to consider.
     */
    void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size);
}
