package de.jplag.options;

import java.util.function.Function;

import de.jplag.JPlagComparison;

public enum SimilarityMetric {
    AVG(JPlagComparison::similarity),
    MIN(JPlagComparison::minimalSimilarity),
    MAX(JPlagComparison::maximalSimilarity);

    private final Function<JPlagComparison, Float> similarityFunction;

    SimilarityMetric(Function<JPlagComparison, Float> determinePercentage) {
        this.similarityFunction = determinePercentage;
    }

    public boolean isAboveThreshold(JPlagComparison comparison, float similarityThreshold) {
        return similarityFunction.apply(comparison) >= similarityThreshold;
    }
}
