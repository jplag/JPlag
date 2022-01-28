package de.jplag.options;

import java.util.function.Function;

import de.jplag.JPlagComparison;

public enum SimilarityMetric implements Function<JPlagComparison, Float> {
    AVG(it -> it.similarity()),
    MIN(it -> it.minimalSimilarity()),
    MAX(it -> it.maximalSimilarity()),
    INTERSECTION(it -> (float) it.getNumberOfMatchedTokens());

    private final Function<JPlagComparison, Float> similarityFunction;

    private SimilarityMetric(Function<JPlagComparison, Float> determinePercentage) {
        this.similarityFunction = determinePercentage;
    }

    public boolean isAboveThreshold(JPlagComparison comparison, float similarityThreshold) {
        return similarityFunction.apply(comparison) >= similarityThreshold;
    }

    @Override
    public Float apply(JPlagComparison comparison) {
        return similarityFunction.apply(comparison);
    }
}
