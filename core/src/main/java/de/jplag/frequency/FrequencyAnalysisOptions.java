package de.jplag.frequency;

import de.jplag.frequency.strategy.CompleteMatchesStrategy;
import de.jplag.frequency.strategy.FrequencyStrategy;
import de.jplag.frequency.weighting.SigmoidWeighting;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Options for Frequency Analysis.
 * @param enabled if false, frequency analysis is skipped.
 * @param analysisStrategy the strategy used to determine the frequency of a Match
 * @param frequencyStrategyMinValue the minimum considered size of subsequences from matches in the frequency strategy
 * @param weightingFunction function used to determine the weight from the match rarity
 * @param weightingFactor scales the impact of the weighting
 */
@RecordBuilder
public record FrequencyAnalysisOptions(boolean enabled, FrequencyStrategy analysisStrategy, int frequencyStrategyMinValue,
        MatchWeightingFunction weightingFunction, double weightingFactor) implements FrequencyAnalysisOptionsBuilder.With {

    /** Default value for the analysis being enabled. */
    public static final boolean DEFAULT_ENABLED = false;
    /** Default analysis strategy. */
    public static final FrequencyStrategy DEFAULT_ANALYSIS_STRATEGY = new CompleteMatchesStrategy();
    /** Default minimum subsequence length. */
    public static final int DEFAULT_MINIMUM_SUBSEQUENCE_LENGTH = 1;
    /** Default weighting function. */
    public static final MatchWeightingFunction DEFAULT_WEIGHTING_FUNCTION = new SigmoidWeighting();
    /** Default minimum match weight factor. */
    public static final double DEFAULT_WEIGHTING_FACTOR = 0.25;

    /**
     * Default options for frequency Analysis.
     */
    public FrequencyAnalysisOptions() {
        this(DEFAULT_ENABLED, DEFAULT_ANALYSIS_STRATEGY, DEFAULT_MINIMUM_SUBSEQUENCE_LENGTH, DEFAULT_WEIGHTING_FUNCTION, DEFAULT_WEIGHTING_FACTOR);
    }
}
