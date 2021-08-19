package jplag.options;

import java.util.function.Function;

import jplag.JPlagComparison;

public enum SimilarityMetric {
    AVG(it -> it.percent()),
    MIN(it -> it.percentMinAB()),
    MAX(it -> it.percentMaxAB());

    private final Function<JPlagComparison, Float> similarityFunction;

    private SimilarityMetric(Function<JPlagComparison, Float> determinePercentage) {
        this.similarityFunction = determinePercentage;
    }

    public boolean isAboveThreshold(JPlagComparison comparison, float similarityThreshold) {
        return similarityFunction.apply(comparison) >= similarityThreshold;
    }
}
