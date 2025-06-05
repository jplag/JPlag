package de.jplag.highlightExtraction;

import java.util.List;
import java.util.Map;

/**
 * Interface for building frequency maps of token subsequences based on a given strategy.
 */
public interface FrequencyBuilder {
    /**
     * Builds or updates the frequency map with submatches according to the implemented strategy.
     * @param tokens List of tokens representing the match.
     * @param comparisonId Identifier for the comparison.
     * @param frequencyMap Map that contains token subsequences and how often they occur across comparisons.
     * @param strategyParam Parameter defining strategy-dependent submatch lengths.
     */
    void build(List<String> tokens, String comparisonId, Map<String, List<String>> frequencyMap, int strategyParam);
}
