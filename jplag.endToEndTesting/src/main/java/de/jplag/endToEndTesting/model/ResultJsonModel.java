package de.jplag.endToEndTesting.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The ResultJsonModel is the java object for the JavaResult.json file. The
 * object contains all the necessary information for the comparisons in the test
 * cases between old and new results, which have been stored in the
 * JavaResult.json file.
 */
public class ResultJsonModel {

	@JsonProperty("function_name")
	private String functionName;
	@JsonProperty("result_similarity")
	private String resultSimilarity;

	/**
	 * Constructor for the ResultJsonModel. The model is the serialization of the
	 * Json file in the form of a Java object.
	 * 
	 * @param functionName     the function name for the associated test results.
	 *                         Used as identifier to search results for the test
	 *                         cases.
	 * @param resultSimilarity Comparison value used in the tests
	 */
	public ResultJsonModel(String functionName, String resultSimilarity) {
		this.functionName = functionName;
		this.resultSimilarity = resultSimilarity;
	}

	/**
	 * empty constructor in case the serialization contains an empty object to
	 * prevent throwing exceptions. this constructor was necessary for serialization
	 * with the Jackson parse extension
	 */
	public ResultJsonModel() {
	}

	/**
	 * @return of the comparative similarity
	 */
	public float similarity() {
		return Float.parseFloat(resultSimilarity);
	}

	/**
	 * @return functionname associated with the results
	 */
	public String getFunctionName() {
		return functionName;
	}
}
