package de.jplag.clustering;

import java.util.Optional;
import java.util.function.Function;

import de.jplag.clustering.preprocessors.CumulativeDistributionFunctionPreprocessor;
import de.jplag.clustering.preprocessors.PercentileThresholdProcessor;
import de.jplag.clustering.preprocessors.ThresholdPreprocessor;

/**
 * List of all usable {@link ClusteringPreprocessor}s.
 */
public enum Preprocessing {
    NONE(options -> null),
    /** {@link CumulativeDistributionFunctionPreprocessor} */
    CUMULATIVE_DISTRIBUTION_FUNCTION(options -> new CumulativeDistributionFunctionPreprocessor()),
    /** {@link ThresholdPreprocessor} */
    THRESHOLD(options -> new ThresholdPreprocessor(options.preprocessorThreshold())),
    /** {@link PercentileThresholdProcessor} */
    PERCENTILE(options -> new PercentileThresholdProcessor(options.preprocessorPercentile()));

    private final Function<ClusteringOptions, ClusteringPreprocessor> constructor;

    Preprocessing(Function<ClusteringOptions, ClusteringPreprocessor> constructor) {
        this.constructor = constructor;
    }

    public Optional<ClusteringPreprocessor> constructPreprocessor(ClusteringOptions options) {
        return Optional.ofNullable(constructor.apply(options));
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace('_', ' ');
    }
}
