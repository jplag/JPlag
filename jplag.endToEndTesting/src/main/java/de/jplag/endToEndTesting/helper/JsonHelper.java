package de.jplag.endToEndTesting.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.Reader;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.jplag.endToEndTesting.constants.Constant;
import model.ResultJsonModel;

public class JsonHelper {

	public static List<ResultJsonModel> getResultModelFromPath(String pathToJsonFile)
			throws JsonMappingException, JsonProcessingException, Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		return Arrays.asList(objectMapper.readValue(Paths.get(pathToJsonFile).toFile(), ResultJsonModel[].class));
	}
}
