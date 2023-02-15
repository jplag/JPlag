package de.jplag.options;

import java.util.function.ToDoubleFunction;

import de.jplag.JPlagComparison;

public enum SimilarityMetric implements ToDoubleFunction<JPlagComparison> {
    AVG("average similarity", JPlagComparison::similarity),
    MIN("minimum similarity", JPlagComparison::minimalSimilarity),
    MAX("maximal similarity", JPlagComparison::maximalSimilarity),
    INTERSECTION("matched tokens", it -> (double) it.getNumberOfMatchedTokens());

    private final ToDoubleFunction<JPlagComparison> similarityFunction;
    private final String description;

    SimilarityMetric(String description, ToDoubleFunction<JPlagComparison> similarityFunction) {
        this.description = description;
        this.similarityFunction = similarityFunction;
    }

    public boolean isAboveThreshold(JPlagComparison comparison, double similarityThreshold) {
        return similarityFunction.applyAsDouble(comparison) >= similarityThreshold;
    }

    @Override
    public double applyAsDouble(JPlagComparison comparison) {
        return similarityFunction.applyAsDouble(comparison);
    }

    @Override
    public String toString() {
        return description;
    }
}
