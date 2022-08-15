package de.jplag.end_to_end_testing.oldModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.jplag.JPlagComparison;
import de.jplag.options.JPlagOptions;

/**
 * The ResultModel contains all information and comparison values that are important for a test and that could be parsed
 * from the json file.
 */
public class ResultModel {

    @JsonProperty("result_minimal_similarity")
    private float resultSimilarityMinimum;
    @JsonProperty("result_maximum_similarity")
    private float resultSimilarityMaximum;
    @JsonProperty("result_matched_token_number")
    private int resultMatchedTokenNumber;
    @JsonProperty("test_identifier")
    private String testIdentifier;
    @JsonProperty("minimum_token_match")
    private int minimumTokenMatch;

    /**
     * Constructor for the ResultModel. The model is the serialization of the Json file in the form of a Java object.
     * @param resultSimilarityMinimum comparative value of the minimum similarity
     * @param resultSimilarityMaximum comparative value of the maximum similarity
     * @param resultMatchedTokenNumber comparative value of the matched token number
     * @param minimumTokenMatch 
     * @param testIdentifier specifies which associated test results are needed for a test case and are therefore associated
     * with this Id.
     */
    public ResultModel(float resultSimilarityMinimum, float resultSimilarityMaximum, int resultMatchedTokenNumber, int minimumTokenMatch ,String testIdentifier) {
        this.resultSimilarityMinimum = resultSimilarityMinimum;
        this.resultSimilarityMaximum = resultSimilarityMaximum;
        this.resultMatchedTokenNumber = resultMatchedTokenNumber;
        this.minimumTokenMatch = minimumTokenMatch;
        this.testIdentifier = testIdentifier;
    }

    /**
     * Constructor for the ResultModel. The model is the serialization of the Json file in the form of a Java object.
     * @param jplagComparison object from witch the values need to be stored
     * @param testIdentifier specifies which associated test results are needed for a test case and are therefore associated
     * with this Id.
     */
    public ResultModel(JPlagComparison jplagComparison, String testIdentifier, JPlagOptions jplagOptions) {
        this.resultMatchedTokenNumber = jplagComparison.getNumberOfMatchedTokens();
        this.resultSimilarityMinimum = jplagComparison.minimalSimilarity();
        this.resultSimilarityMaximum = jplagComparison.maximalSimilarity();
        this.minimumTokenMatch = jplagOptions.getMinimumTokenMatch();
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
    @JsonIgnore
    public String getTestIdentifier() {
        return testIdentifier;
    }

    /**
     * @return Minimum similarity in percent of both submissions.
     */
    @JsonIgnore
    public float getMinimalSimilarity() {
        return resultSimilarityMinimum;
    }

    /**
     * @return Maximum similarity in percent of both submissions.
     */
    @JsonIgnore
    public float getMaximalSimilarity() {
        return resultSimilarityMaximum;
    }

    /**
     * @return Total number of matched tokens stored for this comparison.
     */
    @JsonIgnore
    public int getNumberOfMatchedTokens() {
        return resultMatchedTokenNumber;
    }
    
    @JsonIgnore
    public int getJPlagOptions()
    {
    	return minimumTokenMatch;
    }
}
