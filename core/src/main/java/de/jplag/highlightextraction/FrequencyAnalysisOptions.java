package de.jplag.highlightextraction;

import de.jplag.highlightextraction.strategy.CompleteMatchesStrategy;
import de.jplag.highlightextraction.strategy.FrequencyStrategy;
import de.jplag.highlightextraction.weighting.SigmoidWeighting;

/**
 * Options for Frequency Analysis.
 * @param enabled if false, highlight extraction is skipped.
 * @param analysisStrategy the strategy used to determine the frequency of a Match
 * @param minimumSubsequenceLength the minimum considered size of Subsequences from matches in the analysisStrategy
 * @param weightingStrategy strategy used to influence the similarity based on Match frequency
 * @param weightingFactor how strong the impact of the weightingStrategy is
 */
public record FrequencyAnalysisOptions(boolean enabled, FrequencyStrategy analysisStrategy, int minimumSubsequenceLength,
        WeightingFunction weightingStrategy, double weightingFactor) {

    /** Default value for the highlighting enabling. */
    public static final boolean DEFAULT_ENABLED = false;
    /** Default value for the analysis strategy. */
    public static final FrequencyStrategy DEFAULT_ANALYSIS_STRATEGY = new CompleteMatchesStrategy();
    /** Default value for the minimum match weight factor. */
    public static final int DEFAULT_MINIMUM_SUBSEQUENCE_LENGTH = 1;
    /** Default value for the weighing function. */
    public static final WeightingFunction DEFAULT_WEIGHTING_FUNCTION = new SigmoidWeighting();
    /** Default value for the minimum match weight factor. */
    public static final double DEFAULT_WEIGHTING_FACTOR = 0.25;

    /**
     * Default options for frequency Analysis.
     */
    public FrequencyAnalysisOptions() {
        this(DEFAULT_ENABLED, DEFAULT_ANALYSIS_STRATEGY, DEFAULT_MINIMUM_SUBSEQUENCE_LENGTH, DEFAULT_WEIGHTING_FUNCTION, DEFAULT_WEIGHTING_FACTOR);
    }

    /**
     * Chosen FrequencyStrategy.
     */
    public FrequencyAnalysisOptions withAnalysisStrategy(FrequencyStrategy strategy) {
        return new FrequencyAnalysisOptions(enabled, strategy, minimumSubsequenceLength, weightingStrategy, weightingFactor);
    }

    /**
     * Minimum considered subsequence length.
     */
    public FrequencyAnalysisOptions withMinimumSubsequenceLength(int minimumSubsequenceLength) {
        return new FrequencyAnalysisOptions(enabled, analysisStrategy, minimumSubsequenceLength, weightingStrategy, weightingFactor);
    }

    /**
     * Chosen weightingStrategy.
     */
    public FrequencyAnalysisOptions withWeightingFunction(WeightingFunction weighting) {
        return new FrequencyAnalysisOptions(enabled, analysisStrategy, minimumSubsequenceLength, weighting, weightingFactor);
    }

    /**
     * Weighting weightingFactor for weightingStrategy.
     */
    public FrequencyAnalysisOptions withWeightingFactor(double weightingFactor) {
        return new FrequencyAnalysisOptions(enabled, analysisStrategy, minimumSubsequenceLength, weightingStrategy, weightingFactor);
    }
}
