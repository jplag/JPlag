package de.jplag.endtoend.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object that maps the results of the end top end tests using the identifierToResultMap. this creates a map of test
 * data and its results for each possible option specified. this is important both for serializing the data into json
 * format and for deserialization.
 */
public record ResultDescription(@JsonIgnore String languageIdentifier, @JsonProperty("options") Options options,
        @JsonProperty("tests") Map<String, ExpectedResult> identifierToResultMap) {
}