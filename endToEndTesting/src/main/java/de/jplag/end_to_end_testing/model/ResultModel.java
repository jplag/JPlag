package de.jplag.end_to_end_testing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The ResultModel contains all information and comparison values that are important for a test and that could be parsed
 * from the json file.
 */
public class ResultModel {

    @JsonProperty("result_similarity")
    private float resultSimilarity;
    @JsonProperty("test_identifier")
    private int testIdentifier;

    /**
     * Constructor for the ResultModel. The model is the serialization of the Json file in the form of a Java object.
     * @param resultSimilarity stored comparative value of the similarity
     * @param testIdentifier specifies which associated test results are needed for a test case and are therefore associated
     * with this Id.
     */
    public ResultModel(float resultSimilarity, int testIdentifier) {
        this.resultSimilarity = resultSimilarity;
        this.testIdentifier = testIdentifier;
    }

    /**
     * empty constructor in case the serialization contains an empty object to prevent throwing exceptions. this constructor
     * was necessary for serialization with the Jackson parse extension
     */
    public ResultModel() {
        // For Serialization
    }

    /**
     * @return Identifier assigned to the result
     */
    public int getTestId() {
        return testIdentifier;
    }

    /**
     * @return of the comparative similarity
     */
    public float getResultSimilarity() {
        return resultSimilarity;
    }
}
