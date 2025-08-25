package de.jplag.options;

import java.util.function.ToDoubleFunction;

import de.jplag.JPlagComparison;
import de.jplag.Match;

/**
 * Similarity metrics supported by JPlag, computing a similarity value in percent for a set of matches between two
 * programs.
 */
public enum SimilarityMetric implements ToDoubleFunction<JPlagComparison> {
    AVG("average similarity", JPlagComparison::similarity),
    /**
     * @deprecated Unused metric
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    MIN("minimum similarity", JPlagComparison::minimalSimilarity),
    MAX("maximal similarity", JPlagComparison::maximalSimilarity),
    /**
     * @deprecated Unused metric
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    INTERSECTION("matched tokens", it -> (double) it.getNumberOfMatchedTokens()),
    LONGEST_MATCH("number of tokens in the longest match", it -> it.matches().stream().mapToInt(Match::minimumLength).max().orElse(0)),
    MAXIMUM_LENGTH(
            "length of the longer submission",
            it -> Math.max(it.firstSubmission().getNumberOfTokens(), it.secondSubmission().getNumberOfTokens()));

    private final ToDoubleFunction<JPlagComparison> similarityFunction;
    private final String description;

    SimilarityMetric(String description, ToDoubleFunction<JPlagComparison> similarityFunction) {
        this.description = description;
        this.similarityFunction = similarityFunction;
    }

    /**
     * Checks whether the similarity of a comparison between two program submissions exceeds a threshold for the specific
     * metric.
     * @param comparison is the comparison between two submissions.
     * @param similarityThreshold is the threshold to exceed.
     * @return true if it exceeds the threshold.
     */
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
