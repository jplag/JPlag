package de.jplag.highlightextraction;

/**
 * Options for Frequency Analysis.
 * @param frequencyStrategy the strategy used to determine the frequency of a Match
 * @param frequencyStrategyMinValue the minimum considered size of Subsequences from matches in the frequencyStrategy
 * @param weightingStrategy strategy used to influence the similarity based on Match frequency
 * @param weightingFactor how strong the impact of the weightingStrategy is
 */
public record FrequencyAnalysisOptions(FrequencyAnalysisStrategy frequencyStrategy, int frequencyStrategyMinValue,
        MatchFrequencyWeightingFunction weightingStrategy, double weightingFactor) {

    /**
     * Default options for frequency Analysis.
     */
    public FrequencyAnalysisOptions() {
        this(FrequencyAnalysisStrategy.COMPLETE_MATCHES, 1, MatchFrequencyWeightingFunction.SIGMOID, 0.25);
    }

    /**
     * Chosen FrequencyStrategy.
     */
    public FrequencyAnalysisOptions withFrequencyStrategy(FrequencyAnalysisStrategy strategy) {
        return new FrequencyAnalysisOptions(strategy, frequencyStrategyMinValue, weightingStrategy, weightingFactor);
    }

    /**
     * Minimum considered subsequence length.
     */
    public FrequencyAnalysisOptions withFrequencyStrategyMinimumConsideredMatchSubsequenceSize(int minimumConsideredMatchSubsequenceSize) {
        return new FrequencyAnalysisOptions(frequencyStrategy, minimumConsideredMatchSubsequenceSize, weightingStrategy, weightingFactor);
    }

    /**
     * Chosen weightingStrategy.
     */
    public FrequencyAnalysisOptions withWeightingStrategy(MatchFrequencyWeightingFunction strategy) {
        return new FrequencyAnalysisOptions(frequencyStrategy, frequencyStrategyMinValue, strategy, weightingFactor);
    }

    /**
     * Weighting maximumInfluenceOfMatchFrequencyConsidered for weightingStrategy.
     */
    public FrequencyAnalysisOptions withWeightingFactor(double maximumInfluenceOfMatchFrequencyConsidered) {
        return new FrequencyAnalysisOptions(frequencyStrategy, frequencyStrategyMinValue, weightingStrategy,
                maximumInfluenceOfMatchFrequencyConsidered);
    }
}
