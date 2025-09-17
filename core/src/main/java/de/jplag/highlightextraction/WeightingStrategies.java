package de.jplag.highlightextraction;

/**
 * This class contains the possible weighting functions for a match, in the isFrequencyAnalysisEnabled analysis.
 */
public enum WeightingStrategies {
    PROPORTIONAL(new ProportionalWeightedStrategy()),
    LINEAR(new LinearWeightedStrategy()),
    QUADRATIC(new QuadraticWeightedStrategy()),
    SIGMOID(new SigmoidWeightingStrategy());

    private final MatchWeightingFunction strategy;

    /**
     * @param strategy weighting strategy
     */
    WeightingStrategies(MatchWeightingFunction strategy) {
        this.strategy = strategy;
    }

    /**
     * @return the isFrequencyAnalysisEnabled strategy of the enum constant.
     */
    public MatchWeightingFunction getStrategy() {
        return strategy;
    }

}
