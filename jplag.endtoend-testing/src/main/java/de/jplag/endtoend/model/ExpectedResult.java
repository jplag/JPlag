package de.jplag.endtoend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * contains the current comparative values for the endToEnd tests. The comparative values were determined by discussion
 * which can be found at https://github.com/jplag/JPlag/issues/548 Here this object is used for serialization and
 * deserialization of the information from json to object or object to json.
 */
public record ExpectedResult(@JsonProperty("minimal_similarity") double resultSimilarityMinimum,
        @JsonProperty("maximum_similarity") double resultSimilarityMaximum, @JsonProperty("matched_token_number") int resultMatchedTokenNumber) {
}
