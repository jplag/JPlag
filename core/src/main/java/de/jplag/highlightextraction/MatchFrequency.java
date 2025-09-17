package de.jplag.highlightextraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Contains the map that maps each Match to its frequency Calculated according to the chosen FrequencyStrategy
 * @param matchFrequencyMap maps each Match to its frequency.
 */
public record MatchFrequency(Map<List<TokenType>, Double> matchFrequencyMap) {
    /**
     * Constructor.
     */
    public MatchFrequency() {
        this(new HashMap<>());
    }
}
