package de.jplag.end_to_end_testing.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.jplag.JPlagComparison;
import de.jplag.end_to_end_testing.helper.JPlagTestSuiteHelper;
import de.jplag.options.LanguageOption;

/**
 * Mapper class to map the language to the results and tested options.
 */
public class ResultDescription {
    @JsonIgnore
    private LanguageOption languageOption;
    @JsonProperty("options")
    Options options;
    @JsonProperty("tests")
    Map<String, ExpectedResult> identifierToResultMap;

    /**
     * Constructor for the ResultDescription. The model is the serialization of the Json file in the form of a Java object.
     * @param options which have been tested
     * @param identifierToResultMap mapped results
     */
    public ResultDescription(Options options, Map<String, ExpectedResult> identifierToResultMap) {
        this.options = options;
        this.identifierToResultMap = identifierToResultMap;
    }

    /**
     * Constructor for the ResultDescription. The model is the serialization of the Json file in the form of a Java object.
     * @param options which have been tested
     * @param jPlagComparison results available for the test cases
     * @param languageOption to which the results are to be added
     */
    public ResultDescription(Options options, JPlagComparison jPlagComparison, LanguageOption languageOption) {
        this.languageOption = languageOption;
        this.options = options;
        identifierToResultMap = new HashMap<>();
        identifierToResultMap.put(JPlagTestSuiteHelper.getTestIdentifier(jPlagComparison), new ExpectedResult(jPlagComparison));
    }

    /**
     * Constructor for the ResultDescription. The model is the serialization of the Json file in the form of a Java object.
     * @param languageOption to which the results are to be added
     * @param options which have been tested
     * @param identifierToResultMap mapped results
     */
    public ResultDescription(LanguageOption languageOption, Options options, Map<String, ExpectedResult> identifierToResultMap) {
        this.languageOption = languageOption;
        this.options = options;
        this.identifierToResultMap = identifierToResultMap;

    }

    /**
     * empty constructor in case the serialization contains an empty object to prevent throwing exceptions. this constructor
     * was necessary for serialization with the Jackson parse extension
     */
    public ResultDescription() {
        // For Serialization
    }

    /**
     * @return the mapped results that are available for the searched language.
     */
    @JsonIgnore
    public Map<String, ExpectedResult> getIdentifierResultMap() {
        return identifierToResultMap;
    }

    /**
     * @param identifier for which the results are needed.
     * @return results for the transferred identifiers
     */
    @JsonIgnore
    public ExpectedResult getExpectedResultByIdentifier(String identifier) {
        return identifierToResultMap.get(identifier);
    }

    /**
     * @return options n which the tests have been run
     */
    @JsonIgnore
    public Options getOptions() {
        return options;
    }

    /**
     * @return LanguageOption of the current mapped results
     */
    @JsonIgnore
    public LanguageOption getLanguageOption() {
        return languageOption;
    }

    /**
     * Adds new results to the map
     * @param identifier under which the results are to be stored
     * @param expectedResult expected results for a specific test run
     */
    public void putIdenfifierToResultMap(String identifier, ExpectedResult expectedResult) {
        identifierToResultMap.put(identifier, expectedResult);
    }

}
