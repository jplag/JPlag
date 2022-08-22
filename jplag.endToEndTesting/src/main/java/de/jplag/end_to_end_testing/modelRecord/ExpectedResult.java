package de.jplag.end_to_end_testing.modelRecord;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExpectedResult(@JsonProperty("minimal_similarity") float resultSimilarityMinimum,
        @JsonProperty("maximum_similarity") float resultSimilarityMaximum, @JsonProperty("matched_token_number") int resultMatchedTokenNumber) {
}
