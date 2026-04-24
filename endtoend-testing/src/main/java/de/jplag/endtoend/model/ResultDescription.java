package de.jplag.endtoend.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object that maps the results of the end top end tests using the identifierToResultMap. this creates a map of test
 * stream and its results for each possible option specified. this is important both for serializing the stream into
 * json format and for deserialization.
 * @param identifier a unique identifier for the result
 * @param identifierToResultMap a map of test names to their expected results
 * @param goldStandard the gold standard metrics for evaluation
 */
public record ResultDescription(@JsonProperty String identifier, @JsonProperty("tests") Map<String, ExpectedResult> identifierToResultMap,
        @JsonProperty GoldStandard goldStandard) {
}