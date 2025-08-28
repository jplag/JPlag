package de.jplag.highlightextraction;

/**
 * This class contains the possible weighting functions for a match, in the frequency analysis.
 */
public enum WeightingStrategies {
    PROPORTIONAL(new ProportionalWeigthedStrategy()),
    LINEAR(new LinearWeightedStrategy()),
    QUADRATIC(new QuadraticWeightedStrategy()),
    SIGMOID(new SigmoidWeightingStrategy());

    private final SimilarityStrategy strategy;

    /**
     * @param strategy weighting strategy
     */
    WeightingStrategies(SimilarityStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * @return the frequency strategy of the enum constant.
     */
    public SimilarityStrategy getStrategy() {
        return strategy;
    }

}
