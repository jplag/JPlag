package de.jplag.endToEndTesting.helper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.jplag.endToEndTesting.constants.Constant;
import de.jplag.endToEndTesting.model.ResultJsonModel;

/**
 * Helper class for serializing and creating all json dependent events.
 *
 */
public final class JsonHelper {

	/**
	 * private constructor to prevent instantiation
	 */
	private JsonHelper() {
	}

	/**
	 * Parsing the old results in the json file as a list from ResultJsonModel.
	 * 
	 * @return list of saved results for the test cases
	 * @throws IOException is thrown for all problems that may occur while parsing
	 *                     the json file. This includes both reading and parsing
	 *                     problems.
	 */
	public static List<ResultJsonModel> getResultModelFromPath() throws IOException {
		return Arrays.asList(
				new ObjectMapper().readValue(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toFile(), ResultJsonModel[].class));
	}
}
