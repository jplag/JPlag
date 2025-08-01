package de.jplag.endtoend.model;

import de.jplag.JPlagComparison;
import de.jplag.options.SimilarityMetric;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * contains the current comparative values for the endToEnd tests. Represents the expected result metrics for similarity
 * comparison. The comparative values were determined by discussion which can be found at
 * <a href="https://github.com/jplag/JPlag/issues/548">GitHub</a>.Here this object is used for serialization and
 * deserialization of the information from json to object or object to json.
 * @param resultSimilarityMinimum the minimum expected similarity value
 * @param resultSimilarityMaximum the maximum expected similarity value
 * @param resultMatchedTokenNumber the expected number of matched tokens
 */
public record ExpectedResult(@JsonProperty("minimal_similarity") double resultSimilarityMinimum,
        @JsonProperty("maximum_similarity") double resultSimilarityMaximum, @JsonProperty("matched_token_number") int resultMatchedTokenNumber) {

    /**
     * Returns the value of a similarity metric for this result.
     * @param metric is the specified similarity metric.
     * @return the similarity value as a double value.
     */
    public double getSimilarityForMetric(SimilarityMetric metric) {
        return switch (metric) {
            case AVG -> (resultSimilarityMinimum() + resultSimilarityMaximum()) / 2.0;
            case MAX -> resultSimilarityMaximum();
            default -> throw new IllegalArgumentException(String.format("Similarity metric %s not supported for end to end tests", metric.name()));
        };
    }

    /**
     * Creates an expected result from a comparison.
     * @param comparison The comparison
     * @return The expected result
     */
    public static ExpectedResult fromComparison(JPlagComparison comparison) {
        return new ExpectedResult(comparison.minimalSimilarity(), comparison.maximalSimilarity(), comparison.getNumberOfMatchedTokens());
    }
}
