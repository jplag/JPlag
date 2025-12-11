package de.jplag.highlightextraction;

import de.jplag.highlightextraction.strategy.CompleteMatchesStrategy;
import de.jplag.highlightextraction.strategy.FrequencyStrategy;
import de.jplag.highlightextraction.weighting.SigmoidWeighting;
import de.jplag.reporting.jsonfactory.serializer.AllCapsClassNameSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Options for Frequency Analysis.
 * @param enabled if false, highlight extraction is skipped.
 * @param analysisStrategy the strategy used to determine the frequency of a Match
 * @param weightingFunction function used to determine the weight from the match rarity
 * @param weightingFactor scales the impact of the weighting
 */
@RecordBuilder
public record FrequencyAnalysisOptions(boolean enabled, @JsonSerialize(using = AllCapsClassNameSerializer.class) FrequencyStrategy analysisStrategy,
        @JsonSerialize(using = AllCapsClassNameSerializer.class) WeightingFunction weightingFunction, double weightingFactor) {

    /** Default value for the highlighting enabling. */
    public static final boolean DEFAULT_ENABLED = false;
    /** Default analysis strategy. */
    public static final FrequencyStrategy DEFAULT_ANALYSIS_STRATEGY = new CompleteMatchesStrategy();
    /** Default minimum subsequence length. */
    public static final int DEFAULT_MINIMUM_SUBSEQUENCE_LENGTH = 1;
    /** Default weighting function. */
    public static final WeightingFunction DEFAULT_WEIGHTING_FUNCTION = new SigmoidWeighting();
    /** Default minimum match weight factor. */
    public static final double DEFAULT_WEIGHTING_FACTOR = 0.25;

    /**
     * Default options for frequency Analysis.
     */
    public FrequencyAnalysisOptions() {
        this(DEFAULT_ENABLED, DEFAULT_ANALYSIS_STRATEGY, DEFAULT_WEIGHTING_FUNCTION, DEFAULT_WEIGHTING_FACTOR);
    }

    /**
     * Creates a copy of this {@link FrequencyAnalysisOptions} using the given value for
     * {@link FrequencyAnalysisOptions#enabled}.
     * @param enabled the new value for enabled
     * @return the new frequency analysis options.
     */
    public FrequencyAnalysisOptions withEnabled(boolean enabled) {
        return new FrequencyAnalysisOptions(enabled, analysisStrategy, weightingFunction, weightingFactor);
    }

    /**
     * Creates a copy of this {@link FrequencyAnalysisOptions} using the given value for
     * {@link FrequencyAnalysisOptions#analysisStrategy}.
     * @param strategy the new value for analysisStrategy
     * @return the new frequency analysis options.
     */
    public FrequencyAnalysisOptions withAnalysisStrategy(FrequencyStrategy strategy) {
        return new FrequencyAnalysisOptions(enabled, strategy, weightingFunction, weightingFactor);
    }

    /**
     * Creates a copy of this {@link FrequencyAnalysisOptions} using the given value for
     * {@link FrequencyAnalysisOptions#weightingFunction}.
     * @param weighting the new value for weightingFunction
     * @return the new frequency analysis options.
     */
    public FrequencyAnalysisOptions withWeightingFunction(WeightingFunction weighting) {
        return new FrequencyAnalysisOptions(enabled, analysisStrategy, weighting, weightingFactor);
    }

    /**
     * Creates a copy of this {@link FrequencyAnalysisOptions} using the given value for
     * {@link FrequencyAnalysisOptions#weightingFactor}.
     * @param weightingFactor the new value for weightingFactor
     * @return the new frequency analysis options.
     */
    public FrequencyAnalysisOptions withWeightingFactor(double weightingFactor) {
        return new FrequencyAnalysisOptions(enabled, analysisStrategy, weightingFunction, weightingFactor);
    }
}
