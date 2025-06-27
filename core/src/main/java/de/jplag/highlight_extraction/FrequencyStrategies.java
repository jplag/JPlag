package de.jplag.highlight_extraction;

/**
 * Enum representing the different strategies for frequency calculation of token subsequences in comparisons.
 */
public enum FrequencyStrategies {
    COMPLETE_MATCHES(new CompleteMatchesStrategy()),
    CONTAINED_MATCHES(new ContainedMatchesStrategy()),
    SUB_MATCHES(new SubMatchesStrategy()),
    WINDOW_OF_MATCHES(new WindowOfMatchesStrategy());

    private final FrequencyStrategy strategy;

    FrequencyStrategies(FrequencyStrategy strategy) {
        this.strategy = strategy;
    }

    public FrequencyStrategy getStrategy() {
        return strategy;
    }
}
