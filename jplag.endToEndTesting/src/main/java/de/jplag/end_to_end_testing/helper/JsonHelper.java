package de.jplag.end_to_end_testing.helper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.jplag.end_to_end_testing.constants.Constant;
import de.jplag.end_to_end_testing.model.ResultJsonModel;

/**
 * Helper class for serializing and creating all json dependent events.
 */
public final class JsonHelper {

    /**
     * private constructor to prevent instantiation
     */
    private JsonHelper() {
    	// For Serialization 
    }

    /**
     * Parsing the old results in the json file as a list from ResultJsonModel.
     * @return list of saved results for the test cases
     * @throws IOException is thrown for all problems that may occur while parsing the json file. This includes both reading
     * and parsing problems.
     */
    public static List<ResultJsonModel> getResultModelFromPath() throws IOException {
        return Arrays.asList(new ObjectMapper().readValue(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toFile(), ResultJsonModel[].class));
    }
}
