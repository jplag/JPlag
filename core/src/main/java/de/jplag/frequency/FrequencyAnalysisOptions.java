package de.jplag.frequency;

/**
 * Options for Frequency Analysis.
 * @param enabled specifies if the analysis is enabled.
 * @param frequencyStrategy the strategy used to determine the frequency of a Match
 * @param frequencyStrategyMinValue the minimum considered size of Subsequences from matches in the frequencyStrategy
 * @param weightingStrategy strategy used to influence the similarity based on Match frequency
 * @param weightingFactor how strong the impact of the weightingStrategy is
 */
public record FrequencyAnalysisOptions(boolean enabled, FrequencyAnalysisStrategy frequencyStrategy, int frequencyStrategyMinValue,
        MatchFrequencyWeightingFunction weightingStrategy, double weightingFactor) {

    /** default value for the analysis being enabled. **/
    public static final boolean DEFAULT_ENABLED = false;

    /**
     * Default options for frequency Analysis.
     */
    public FrequencyAnalysisOptions() {
        this(false, FrequencyAnalysisStrategy.COMPLETE_MATCHES, 1, MatchFrequencyWeightingFunction.SIGMOID, 0.25);
    }

    /**
     * Chosen FrequencyStrategy.
     */
    public FrequencyAnalysisOptions withFrequencyStrategy(FrequencyAnalysisStrategy strategy) {
        return new FrequencyAnalysisOptions(enabled, strategy, frequencyStrategyMinValue, weightingStrategy, weightingFactor);
    }

    /**
     * Minimum considered subsequence length.
     */
    public FrequencyAnalysisOptions withFrequencyStrategyMinimumConsideredMatchSubsequenceSize(int minimumConsideredMatchSubsequenceSize) {
        return new FrequencyAnalysisOptions(enabled, frequencyStrategy, minimumConsideredMatchSubsequenceSize, weightingStrategy, weightingFactor);
    }

    /**
     * Chosen weightingStrategy.
     */
    public FrequencyAnalysisOptions withWeightingStrategy(MatchFrequencyWeightingFunction strategy) {
        return new FrequencyAnalysisOptions(enabled, frequencyStrategy, frequencyStrategyMinValue, strategy, weightingFactor);
    }

    /**
     * Weighting maximumInfluenceOfMatchFrequencyConsidered for weightingStrategy.
     */
    public FrequencyAnalysisOptions withWeightingFactor(double maximumInfluenceOfMatchFrequencyConsidered) {
        return new FrequencyAnalysisOptions(enabled, frequencyStrategy, frequencyStrategyMinValue, weightingStrategy,
                maximumInfluenceOfMatchFrequencyConsidered);
    }
}
