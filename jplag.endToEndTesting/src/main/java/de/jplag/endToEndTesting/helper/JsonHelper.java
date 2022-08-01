package de.jplag.endToEndTesting.helper;

import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.ResultJsonModel;

public class JsonHelper {

	public static List<ResultJsonModel> getResultModelFromPath(String pathToJsonFile)
			throws JsonMappingException, JsonProcessingException, Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		return Arrays.asList(objectMapper.readValue(Paths.get(pathToJsonFile).toFile(), ResultJsonModel[].class));
	}
}
