package de.jplag.end_to_end_testing.model;

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

    /**
     * @param identifier for which the stored results are needed
     * @return stored results as ExpectedResult object for the passed id
     */
    @JsonIgnore
    public ExpectedResult getExpectedResultByIdentifier(String identifier) {
        return identifierToResultMap.get(identifier);
    }

    /**
     * Adds expected results to the existing list of the object. These results are stored in a map with the given
     * identifier.
     * @param identifier under which the results should be stored
     * @param expectedResult expected results belonging to the identifier
     */
    public void putIdentifierToResultMap(String identifier, ExpectedResult expectedResult) {
        identifierToResultMap.put(identifier, expectedResult);
    }
}