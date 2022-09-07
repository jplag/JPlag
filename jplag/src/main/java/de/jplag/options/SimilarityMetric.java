package de.jplag.options;

import java.util.function.Function;

import de.jplag.JPlagComparison;

public enum SimilarityMetric implements Function<JPlagComparison, Double> {
    AVG(JPlagComparison::similarity),
    MIN(JPlagComparison::minimalSimilarity),
    MAX(JPlagComparison::maximalSimilarity),
    INTERSECTION(it -> (double) it.getNumberOfMatchedTokens());

    private final Function<JPlagComparison, Double> similarityFunction;

    SimilarityMetric(Function<JPlagComparison, Double> determinePercentage) {
        this.similarityFunction = determinePercentage;
    }

    public boolean isAboveThreshold(JPlagComparison comparison, double similarityThreshold) {
        return similarityFunction.apply(comparison) >= similarityThreshold;
    }

    @Override
    public Double apply(JPlagComparison comparison) {
        return similarityFunction.apply(comparison);
    }
}
