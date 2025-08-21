package de.jplag.highlightextraction;

/**
 * This class
 */
public enum WeightingStrategies {
    PROPORTIONAL(new ProportionalWeigthedStrategy()),
    LINEAR(new RareTokensWeightedStrategy()),
    QUADRATIC(new QuadraticWeightedStrategy()),
    SIGMOID(new SigmoidWeightingStrategy());

    private final SimilarityStrategy strategy;

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
