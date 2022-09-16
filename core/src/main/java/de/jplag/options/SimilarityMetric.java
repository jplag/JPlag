package de.jplag.options;

import java.util.function.ToDoubleFunction;

import de.jplag.JPlagComparison;

public enum SimilarityMetric implements ToDoubleFunction<JPlagComparison> {
    AVG(JPlagComparison::similarity),
    MIN(JPlagComparison::minimalSimilarity),
    MAX(JPlagComparison::maximalSimilarity),
    INTERSECTION(it -> (double) it.getNumberOfMatchedTokens());

    private final ToDoubleFunction<JPlagComparison> similarityFunction;

    SimilarityMetric(ToDoubleFunction<JPlagComparison> similarityFunction) {
        this.similarityFunction = similarityFunction;
    }

    public boolean isAboveThreshold(JPlagComparison comparison, double similarityThreshold) {
        return similarityFunction.applyAsDouble(comparison) >= similarityThreshold;
    }

    @Override
    public double applyAsDouble(JPlagComparison comparison) {
        return similarityFunction.applyAsDouble(comparison);
    }
}
