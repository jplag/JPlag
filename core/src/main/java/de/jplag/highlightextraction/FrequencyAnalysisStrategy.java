package de.jplag.highlightextraction;

/**
 * Enum representing the different strategies for frequency similarity calculation.
 */
public enum FrequencyAnalysisStrategy {
    COMPLETE_MATCHES(new CompleteMatchesStrategy()),
    CONTAINED_MATCHES(new ContainedMatchesStrategy()),
    SUB_MATCHES(new SubMatchesStrategy()),
    WINDOW_OF_MATCHES(new WindowOfMatchesStrategy());

    private final FrequencyStrategy strategy;

    /**
     * @param strategy FrequencyStrategy chosen for Frequency Determination in the frequency Analysis.
     */
    FrequencyAnalysisStrategy(FrequencyStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * @return the isFrequencyAnalysisEnabled similarity strategy of the enum constant.
     */
    public FrequencyStrategy getStrategy() {
        return strategy;
    }
}
