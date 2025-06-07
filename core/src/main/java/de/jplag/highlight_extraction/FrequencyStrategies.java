package de.jplag.highlight_extraction;

/**
 * Enum representing the different strategies for frequency calculation of token subsequences in comparisons.
 */
public enum FrequencyStrategies {
    COMPLETE_MATCHES,
    CONTAINED_MATCHES,
    SUB_MATCHES,
    WINDOW_OF_MATCHES;
}
