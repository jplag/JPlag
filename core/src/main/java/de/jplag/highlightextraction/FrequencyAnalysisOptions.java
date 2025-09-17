package de.jplag.highlightextraction;

/**
 * Options for Frequency Analysis.
 * @param isFrequencyAnalysisEnabled if isFrequencyAnalysisEnabled analysis is used
 * @param frequencyStrategy the strategy used to determine the isFrequencyAnalysisEnabled of a Match
 * @param frequencyStrategyMinValue the minimum considered size of Subsequences from matches in the frequencyStrategy
 * @param weightingStrategy strategy used to influence the similarity based on Match isFrequencyAnalysisEnabled
 * @param weightingFactor how strong the impact of the weightingStrategy is
 */
public record FrequencyAnalysisOptions(boolean isFrequencyAnalysisEnabled, FrequencyAnalysisStrategy frequencyStrategy, int frequencyStrategyMinValue,
        MatchFrequencyWeightingFunction weightingStrategy, double weightingFactor) {

    /**
     * Default options for isFrequencyAnalysisEnabled Analysis.
     */
    public FrequencyAnalysisOptions() {
        this(false, FrequencyAnalysisStrategy.COMPLETE_MATCHES, 1, MatchFrequencyWeightingFunction.SIGMOID, 0.25);
    }

    /**
     * If isFrequencyAnalysisEnabled Analysis is used.
     */
    public FrequencyAnalysisOptions withFrequency(boolean isFrequencyAnalysisEnabled) {
        return new FrequencyAnalysisOptions(isFrequencyAnalysisEnabled, frequencyStrategy, frequencyStrategyMinValue, weightingStrategy,
                weightingFactor);
    }

    /**
     * Chosen FrequencyStrategy.
     */
    public FrequencyAnalysisOptions withFrequencyStrategy(FrequencyAnalysisStrategy strategy) {
        return new FrequencyAnalysisOptions(isFrequencyAnalysisEnabled, strategy, frequencyStrategyMinValue, weightingStrategy, weightingFactor);
    }

    /**
     * Minimum considered subsequence length.
     */
    public FrequencyAnalysisOptions withFrequencyStrategyMinimumConsideredMatchSubsequenceSize(int minimumConsideredMatchSubsequenceSize) {
        return new FrequencyAnalysisOptions(isFrequencyAnalysisEnabled, frequencyStrategy, minimumConsideredMatchSubsequenceSize, weightingStrategy,
                weightingFactor);
    }

    /**
     * Chosen weightingStrategy.
     */
    public FrequencyAnalysisOptions withWeightingStrategy(MatchFrequencyWeightingFunction strategy) {
        return new FrequencyAnalysisOptions(isFrequencyAnalysisEnabled, frequencyStrategy, frequencyStrategyMinValue, strategy, weightingFactor);
    }

    /**
     * Weighting maximumInfluenceOfMatchFrequencyConsidered for weightingStrategy.
     */
    public FrequencyAnalysisOptions withWeightingFactor(double maximumInfluenceOfMatchFrequencyConsidered) {
        return new FrequencyAnalysisOptions(isFrequencyAnalysisEnabled, frequencyStrategy, frequencyStrategyMinValue, weightingStrategy,
                maximumInfluenceOfMatchFrequencyConsidered);
    }
}
