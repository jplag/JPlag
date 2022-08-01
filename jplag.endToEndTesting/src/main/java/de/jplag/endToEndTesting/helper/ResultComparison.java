package de.jplag.endToEndTesting.helper;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import de.jplag.endToEndTesting.constants.Constant;

public class ResultComparison {
	private String functionName;
	private float similarity;

	/**
	 * Based on the passed function name the stored values in the json-file are
	 * loaded and parsed
	 * 
	 * @param functionName of the current test which is searched for as key in the
	 *                     json-file
	 * @throws JSONException
	 * @throws IOException
	 */
	public ResultComparison(String functionName) throws JSONException, IOException {
		if (functionName != null && !functionName.isEmpty()) {
			this.functionName = functionName;
			loadStoredComparativeValues();
		} else {
			throw new IllegalArgumentException("function name cannot be empty");
		}
	}

	/**
	 * @return the function name passed in the constructor
	 */
	public String getFunctionName() {
		return functionName;
	}

	/**
	 * @return the stored similarity value from the json file for the function
	 *         specified in the constructor.
	 */
	public float getSimilarity() {
		return similarity;
	}

	/**
	 * Loads the comparison values of the current function into the object
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	private void loadStoredComparativeValues() throws JSONException, IOException {

		var jsonResultObj = JsonReader
				.readJsonFromPath(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toAbsolutePath().toString());

		var resultsArray = jsonResultObj.getJSONArray("results");

		for (int i = 0; i < resultsArray.length(); i++) {
			JSONObject articleObject = resultsArray.getJSONObject(i);
			if (articleObject.getString(Constant.JSON_TEST_NAME_NODE).equals(functionName)) {
				similarity = (float) articleObject.getDouble(Constant.JSON_SIMILARITY_NODE);
				return;
			}
		}
	}
}
