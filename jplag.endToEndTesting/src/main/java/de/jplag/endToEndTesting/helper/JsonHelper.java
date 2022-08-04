package de.jplag.endToEndTesting.helper;

import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public static List<ResultJsonModel> getResultModelFromPath(String pathToJsonFile)
			throws JsonMappingException, JsonProcessingException, Exception {
		return Arrays.asList(new ObjectMapper().readValue(Paths.get(pathToJsonFile).toFile(), ResultJsonModel[].class));
	}
}
