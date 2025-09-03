package de.jplag.highlightextraction;

/**
 * Options for Frequency Analysis.
 * @param frequency if frequency analysis is used
 * @param frequencyStrategy the strategy used to determine the frequency of a Match
 * @param frequencyStrategyMinValue the minimum considered size of Subsequences from matches in the frequencyStrategy
 * @param weightingStrategy strategy used to influence the similarity based on Match frequency
 * @param weightingFactor how strong the impact of the weightingStrategy is
 */
public record FrequencyAnalysisOptions(boolean frequency, FrequencyStrategies frequencyStrategy, int frequencyStrategyMinValue,
        WeightingStrategies weightingStrategy, double weightingFactor) {
    /**
     * Options for Frequency Analysis.
     * @param frequency if frequency analysis is used
     * @param frequencyStrategy the strategy used to determine the frequency of a Match
     * @param frequencyStrategyMinValue the minimum considered size of Subsequences from matches in the frequencyStrategy
     * @param weightingStrategy strategy used to influence the similarity based on Match frequency
     * @param weightingFactor how strong the impact of the weightingStrategy is
     */
    public FrequencyAnalysisOptions(boolean frequency, FrequencyStrategies frequencyStrategy, int frequencyStrategyMinValue,
            WeightingStrategies weightingStrategy, double weightingFactor) {
        this.frequency = frequency;
        this.frequencyStrategy = frequencyStrategy;
        this.frequencyStrategyMinValue = frequencyStrategyMinValue;
        this.weightingStrategy = weightingStrategy;
        this.weightingFactor = weightingFactor;
    }

    /**
     * Default options for frequency Analysis.
     */
    public FrequencyAnalysisOptions() {
        this(false, FrequencyStrategies.COMPLETE_MATCHES, 1, WeightingStrategies.SIGMOID, 0.25);
    }

    /**
     * @return if the frequency Analysis is used.
     */
    public boolean frequency() {
        return frequency;
    }

    public FrequencyAnalysisOptions withFrequency(boolean frequency) {
        return new FrequencyAnalysisOptions(frequency, frequencyStrategy, frequencyStrategyMinValue, weightingStrategy, weightingFactor);
    }

    public FrequencyAnalysisOptions withFrequencyStrategy(FrequencyStrategies strategy) {
        return new FrequencyAnalysisOptions(frequency, strategy, frequencyStrategyMinValue, weightingStrategy, weightingFactor);
    }

    public FrequencyAnalysisOptions withFrequencyStrategyMinValue(int minValue) {
        return new FrequencyAnalysisOptions(frequency, frequencyStrategy, minValue, weightingStrategy, weightingFactor);
    }

    public FrequencyAnalysisOptions withWeightingStrategy(WeightingStrategies strategy) {
        return new FrequencyAnalysisOptions(frequency, frequencyStrategy, frequencyStrategyMinValue, strategy, weightingFactor);
    }

    public FrequencyAnalysisOptions withWeightingFactor(double factor) {
        return new FrequencyAnalysisOptions(frequency, frequencyStrategy, frequencyStrategyMinValue, weightingStrategy, factor);
    }
}
