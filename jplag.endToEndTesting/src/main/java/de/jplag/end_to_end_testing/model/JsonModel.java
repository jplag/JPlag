package de.jplag.end_to_end_testing.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The ResultJsonModel is the java object for the JavaResult.json file. The object contains all the necessary
 * information for the comparisons in the test cases between old and new results, which have been stored in the
 * JavaResult.json file.
 */
public class JsonModel {
    @JsonProperty("function_name")
    private String functionName;
    @JsonProperty("test_results")
    private ResultModel[] results;

    /**
     * Constructor for the JsonModel. The model is the serialization of the Json file in the form of a Java object.
     * @param functionName the function name for the associated test results. Used as identifier to search results for the
     * test cases.
     * @param results Collection of the results that are in the current json file for certain tests
     */
    public JsonModel(String functionName, ResultModel[] results) {
        this.functionName = functionName;
        this.results = results;
    }

    /**
     * empty constructor in case the serialization contains an empty object to prevent throwing exceptions. this constructor
     * was necessary for serialization with the Jackson parse extension
     */
    public JsonModel() {
        // For Serialization
    }

    /**
     * @return the name of the currently used function stored in json
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * returns the results for the function that have been stored for the given id.
     * @param identifier for the comparative values
     * @return associated comparison values that have been assigned to the identifier
     */
    public ResultModel getResultModelById(Integer identifier) {
        return Arrays.asList(results).stream().filter(resultModel -> identifier.equals(resultModel.getTestIdentifier())).findAny().orElse(null);
    }
}
