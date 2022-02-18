package de.jplag.clustering;

import antlr.preprocessor.Preprocessor;

import de.jplag.clustering.preprocessors.CumulativeDistributionFunctionPreprocessor;
import de.jplag.clustering.preprocessors.PercentileThresholdProcessor;
import de.jplag.clustering.preprocessors.ThresholdPreprocessor;

/**
 * List of all usable {@link Preprocessor}s.
 */
public enum Preprocessing {
    NONE,
    /** {@link CumulativeDistributionFunctionPreprocessor} */
    CUMULATIVE_DISTRIBUTION_FUNCTION,
    /** {@link ThresholdPreprocessor} */
    THRESHOLD,
    /** {@link PercentileThresholdProcessor} */
    PERCENTILE
}
