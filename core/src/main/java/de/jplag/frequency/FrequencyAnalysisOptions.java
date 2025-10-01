package de.jplag.frequency;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Options for Frequency Analysis.
 * @param enabled specifies if the analysis is enabled.
 * @param frequencyStrategy the strategy used to determine the frequency of a Match
 * @param frequencyStrategyMinValue the minimum considered size of Subsequences from matches in the frequencyStrategy
 * @param weightingStrategy strategy used to influence the similarity based on Match frequency
 * @param weightingFactor how strong the impact of the weightingStrategy is
 */
@RecordBuilder
public record FrequencyAnalysisOptions(boolean enabled, FrequencyStrategy frequencyStrategy, int frequencyStrategyMinValue,
        MatchFrequencyWeightingFunction weightingStrategy, double weightingFactor) implements FrequencyAnalysisOptionsBuilder.With {

    /** default value for the analysis being enabled. **/
    public static final boolean DEFAULT_ENABLED = false;

    /**
     * Default options for frequency Analysis.
     */
    public FrequencyAnalysisOptions() {
        this(false, new CompleteMatchesStrategy(), 1, MatchFrequencyWeightingFunction.SIGMOID, 0.25);
    }
}
