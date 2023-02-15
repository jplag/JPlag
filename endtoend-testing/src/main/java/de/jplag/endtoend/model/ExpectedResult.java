package de.jplag.endtoend.model;

import de.jplag.options.SimilarityMetric;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * contains the current comparative values for the endToEnd tests. The comparative values were determined by discussion
 * which can be found at https://github.com/jplag/JPlag/issues/548 Here this object is used for serialization and
 * deserialization of the information from json to object or object to json.
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
            case MIN -> resultSimilarityMinimum();
            case MAX -> resultSimilarityMaximum();
            case INTERSECTION -> resultMatchedTokenNumber();
            default -> throw new IllegalArgumentException("Metric not supported!");
        };
    }

}
