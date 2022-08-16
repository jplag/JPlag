package de.jplag.end_to_end_testing.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
import de.jplag.end_to_end_testing.model.Options;
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
	 * 
	 * @param resultJsonPath Path to the stored test results
	 * @return ResultDescription as serialized object
	 * @throws IOException is thrown for all problems that may occur while parsing
	 *                     the json file. This includes both reading
	 */
  public static List<ResultDescription> getJsonModelListFromPath(Path resultJsonPath) throws IOException {
  if (resultJsonPath.toFile().exists() && resultJsonPath.toFile().length() > 0) {
      return Arrays.asList(new ObjectMapper().readValue(resultJsonPath.toFile(), ResultDescription[].class));
  } else {
      return Collections.<ResultDescription>emptyList();
  }
}
	

//	public static void writeToJsonFile(ResultDescription resultDescription, String directoryName) throws IOException {
//		// create an instance of DefaultPrettyPrinter
//		// new DefaultPrettyPrinter()
//		ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
//
//		File temporaryDirectorie = Path.of(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME,
//				resultDescription.getLanguageOption().toString()).toFile();
//		File temporaryFile = Path.of(temporaryDirectorie.toString(), directoryName + ".json").toFile();
//		FileHelper.createDirectoryIfItDoseNotExist(temporaryDirectorie);
//		FileHelper.createFileIfItDoseNotExist(temporaryFile);
//		// convert book object to JSON file
//		writer.writeValue(temporaryFile, resultDescription);
//
//	}

  /**
  * Saves the passed object as a json file to the given path
  * @param jsonModelList list of elements to be saved
  * @param temporaryResultDirectory path to the temporary storage location
  * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
  * exceptions produced by failed orinterrupted I/O operations.
  */
 public static void writeJsonModelsToJsonFile(List<ResultDescription> resultDescriptionist, String directoryName) throws IOException {
     // create an instance of DefaultPrettyPrinter
     // new DefaultPrettyPrinter()
     ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

     Path temporaryDirectory = Path.of(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME,
    		 resultDescriptionist.get(0).getLanguageOption().toString() , directoryName + ".json");
    		 
     FileHelper.createDirectoryIfItDoseNotExist(temporaryDirectory.getParent().toFile());
     FileHelper.createFileIfItDoseNotExist(temporaryDirectory.toFile());

     // convert book object to JSON file

     writer.writeValue(temporaryDirectory.toFile(), resultDescriptionist.toArray());

 }
}
