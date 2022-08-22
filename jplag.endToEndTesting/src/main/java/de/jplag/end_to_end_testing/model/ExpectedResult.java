package de.jplag.end_to_end_testing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.jplag.JPlagComparison;

/**
 * The ExpectedResult contains all information and comparison values that are important for a test and that could be
 * parsed from the json file.
 */
public class ExpectedResult {

    @JsonProperty("minimal_similarity")
    private float resultSimilarityMinimum;
    @JsonProperty("maximum_similarity")
    private float resultSimilarityMaximum;
    @JsonProperty("matched_token_number")
    private int resultMatchedTokenNumber;

    /**
     * Constructor for the ExpectedResult. The model is the serialization of the Json file in the form of a Java object.
     * @param resultSimilarityMinimum comparative value of the minimum similarity
     * @param resultSimilarityMaximum comparative value of the maximum similarity
     * @param resultMatchedTokenNumber comparative value of the matched token number
     */
    public ExpectedResult(float resultSimilarityMinimum, float resultSimilarityMaximum, int resultMatchedTokenNumber) {
        this.resultSimilarityMinimum = resultSimilarityMaximum;
        this.resultSimilarityMaximum = resultSimilarityMaximum;
        this.resultMatchedTokenNumber = resultMatchedTokenNumber;
    }

    /**
     * Constructor for the ExpectedResult. The model is the serialization of the Json file in the form of a Java object.
     * @param jplagComparison object from witch the values need to be stored
     */
    public ExpectedResult(JPlagComparison jplagComparison) {
        this.resultSimilarityMinimum = jplagComparison.minimalSimilarity();
        this.resultSimilarityMaximum = jplagComparison.maximalSimilarity();
        this.resultMatchedTokenNumber = jplagComparison.getNumberOfMatchedTokens();
    }

    /**
     * empty constructor in case the serialization contains an empty object to prevent throwing exceptions. this constructor
     * was necessary for serialization with the Jackson parse extension
     */
    public ExpectedResult() {
        // For Serialization
    }

    /**
     * @return Minimum similarity in percent of both submissions.
     */
    @JsonIgnore
    public float getResultSimilarityMinimum() {
        return resultSimilarityMinimum;
    }

    /**
     * @return Maximum similarity in percent of both submissions.
     */
    @JsonIgnore
    public float getResultSimilarityMaximum() {
        return resultSimilarityMaximum;
    }

    /**
     * @return Total number of matched tokens stored for this comparison.
     */
    @JsonIgnore
    public int getResultMatchedTokenNumber() {
        return resultMatchedTokenNumber;
    }
}
