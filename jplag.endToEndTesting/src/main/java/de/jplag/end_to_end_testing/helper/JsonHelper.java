package de.jplag.end_to_end_testing.helper;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.jplag.end_to_end_testing.model.ResultDescription;

public class JsonHelper {
	/**
     * private constructor to prevent instantiation
     */
    private JsonHelper() {
        // For Serialization
    }
     
    /**
     * Parsing the old results in the json file as a list from ResultDescription.
     * @param resultJsonPath Path to the stored test results
     * @return ResultDescription as serialized object
     * @throws IOException  is thrown for all problems that may occur while parsing the json file. This includes both reading
     */
    public static ResultDescription getResultDescriptionListFromPath(Path resultJsonPath) throws IOException {
        if (resultJsonPath.toFile().exists() && resultJsonPath.toFile().length() > 0) {
        	ObjectMapper objectMapper = new ObjectMapper();
        	return objectMapper.readValue(resultJsonPath.toFile(), ResultDescription.class);
        } else {
            return new ResultDescription();
        }
    }
}
