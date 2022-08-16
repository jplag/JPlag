package de.jplag.end_to_end_testing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.jplag.JPlagComparison;

public class ExpectedResult {

	@JsonProperty("result_minimal_similarity")
	private float resultSimilarityMinimum;
	@JsonProperty("result_maximum_similarity")
	private float resultSimilarityMaximum;
	@JsonProperty("result_matched_token_number")
	private int resultMatchedTokenNumber;

	public ExpectedResult(float resultSimilarityMinimum, float resultSimilarityMaximum,
			int resultMatchedTokenNumber) {
		this.resultSimilarityMinimum = resultSimilarityMaximum;
		this.resultSimilarityMaximum = resultSimilarityMaximum;
		this.resultMatchedTokenNumber = resultMatchedTokenNumber;
	}

	public ExpectedResult(JPlagComparison jPlagComparison)
	{
		this.resultSimilarityMinimum = jPlagComparison.minimalSimilarity();
		this.resultSimilarityMaximum = jPlagComparison.maximalSimilarity();
		this.resultMatchedTokenNumber = jPlagComparison.getNumberOfMatchedTokens();
	}
	/**
	 * empty constructor in case the serialization contains an empty object to
	 * prevent throwing exceptions. this constructor was necessary for serialization
	 * with the Jackson parse extension
	 */
	public ExpectedResult() {
		// For Serialization
	}

	@JsonIgnore
	public float getResultSimilarityMinimum() {
		return resultSimilarityMinimum;
	}

	@JsonIgnore
	public float getResultSimilarityMaximum() {
		return resultSimilarityMaximum;
	}

	@JsonIgnore
	public int getResultMatchedTokenNumber() {
		return resultMatchedTokenNumber;
	}
}
