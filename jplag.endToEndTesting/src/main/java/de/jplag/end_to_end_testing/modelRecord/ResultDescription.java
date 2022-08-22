package de.jplag.end_to_end_testing.modelRecord;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.jplag.options.LanguageOption;

public record ResultDescription(@JsonIgnore LanguageOption languageOption, @JsonProperty("options") Options options,
        @JsonProperty("tests") Map<String, ExpectedResult> identifierToResultMap) {

    @JsonIgnore
    public ExpectedResult getExpectedResultByIdentifier(String identifier) {
        return identifierToResultMap.get(identifier);
    }

    public void putIdenfifierToResultMap(String identifier, ExpectedResult expectedResult) {
        identifierToResultMap.put(identifier, expectedResult);
    }

}