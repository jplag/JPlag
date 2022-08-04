package de.jplag.endToEndTesting.helper;

import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.jplag.endToEndTesting.constants.Constant;
import model.ResultJsonModel;

public final class JsonHelper {

	private JsonHelper() {
		// private constructor to prevent instantiation
	}

	/**
	 * Parsing the old results in the json file as a list from ResultJsonModel.
	 * 
	 * @param pathToJsonFile
	 * @return list of saved results for the test cases
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	public static List<ResultJsonModel> getResultModelFromPath()
			throws JsonMappingException, JsonProcessingException, Exception {
		return Arrays.asList(
				new ObjectMapper().readValue(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toFile(), ResultJsonModel[].class));
	}
}
