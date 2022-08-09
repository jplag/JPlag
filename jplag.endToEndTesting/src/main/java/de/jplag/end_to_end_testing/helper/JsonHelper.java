package de.jplag.end_to_end_testing.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
import de.jplag.end_to_end_testing.model.JsonModel;
import de.jplag.end_to_end_testing.model.ResultModel;

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
	 * 
	 * @return list of saved results for the test cases
	 * @param resultJsonPath Path to the stored test results
	 * @throws IOException is thrown for all problems that may occur while parsing
	 *                     the json file. This includes both reading and parsing
	 *                     problems.
	 */
	public static List<JsonModel> getResultModelListFromPath(Path resultJsonPath) throws IOException {
		return Arrays.asList(new ObjectMapper().readValue(resultJsonPath.toFile(), JsonModel[].class));
	}

	/**
	 * @param resultModel
	 * @throws StreamWriteException
	 * @throws DatabindException
	 * @throws IOException
	 */
	public static void writeObjectToJsonFile(ResultModel resultModel, String functionName, String fileName)
			throws StreamWriteException, DatabindException, IOException {
		// create an instance of DefaultPrettyPrinter
		ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
    		File temporaryDirectorie = Path.of(TestDirectoryConstants.TEMPORARY_RESULT_DIRECTORY_NAME.toString(), functionName).toFile();
		File temporaryFile = Path.of(temporaryDirectorie.toString(), fileName)
				.toFile();
		if (!temporaryDirectorie.exists()) {
			temporaryDirectorie.mkdirs();			
		}
		if (!temporaryFile.exists()) {
			temporaryFile.createNewFile();
		}
		// convert book object to JSON file
		writer.writeValue(temporaryFile, resultModel);

	}
	
	public static ResultModel getResultModelFromPath(File jsonFile) throws StreamReadException, DatabindException, IOException
	{
		return new ObjectMapper().readValue(jsonFile, ResultModel.class);
	}
}
