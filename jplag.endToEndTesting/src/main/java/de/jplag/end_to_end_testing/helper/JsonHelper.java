package de.jplag.end_to_end_testing.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
     * @return list of saved results for the test cases
     * @param resultJsonPath Path to the stored test results
     * @throws IOException is thrown for all problems that may occur while parsing the json file. This includes both reading
     * and parsing problems.
     */
    public static List<JsonModel> getJsonModelListFromPath(Path resultJsonPath) throws IOException {
        if (resultJsonPath.toFile().exists() && resultJsonPath.toFile().length() > 0) {
            return Arrays.asList(new ObjectMapper().readValue(resultJsonPath.toFile(), JsonModel[].class));
        } else {
            return null;
        }
    }

    /**
     * Saves the passed object to the specified path
     * @param resultModel elements to be saved
     * @param temporaryResultDirectory path to the temporary storage location
     * @param functionName name of the function for which the element is to be saved
     * @param fileName the name of the file under which the object should be stored
     * @throws StreamWriteException Intermediate base class for all read-side streaming processing problems,
     * includingparsing and input value coercion problems.
     * @throws DatabindException Intermediate base class for all databind level processing problems, asdistinct from
     * stream-level problems or I/O issues below.
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     */
    public static void writeResultModelToJsonFile(ResultModel resultModel, String temporaryResultDirectory, String functionName, String fileName)
            throws StreamWriteException, DatabindException, IOException {
        // create an instance of DefaultPrettyPrinter
        // new DefaultPrettyPrinter()
        ObjectWriter writer = new ObjectMapper().writer();
        File temporaryDirectorie = Path.of(temporaryResultDirectory, functionName).toFile();
        File temporaryFile = Path.of(temporaryDirectorie.toString(), fileName).toFile();
        if (!temporaryDirectorie.exists()) {
            temporaryDirectorie.mkdirs();
        }
        if (!temporaryFile.exists()) {
            temporaryFile.createNewFile();
        }
        // convert book object to JSON file
        writer.writeValue(temporaryFile, resultModel);

    }

    /**
     * Saves the passed object as a json file to the given path
     * @param jsonModelList list of elements to be saved
     * @param temporaryResultDirectory path to the temporary storage location
     * @throws StreamWriteException Intermediate base class for all read-side streaming processing problems,
     * includingparsing and input value coercion problems.
     * @throws DatabindException Intermediate base class for all databind level processing problems, asdistinct from
     * stream-level problems or I/O issues below.
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     */
    public static void writeJsonModelsToJsonFile(List<JsonModel> jsonModelList, Path temporaryResultDirectory)
            throws StreamWriteException, DatabindException, IOException {
        // create an instance of DefaultPrettyPrinter
        // new DefaultPrettyPrinter()
        ObjectWriter writer = new ObjectMapper().writer();

        if (!temporaryResultDirectory.getParent().toFile().exists()) {
            temporaryResultDirectory.getParent().toFile().mkdirs();
        }
        if (!temporaryResultDirectory.toFile().exists()) {
            temporaryResultDirectory.toFile().createNewFile();
        }

        // convert book object to JSON file

        writer.writeValue(temporaryResultDirectory.toFile(), jsonModelList.toArray());

    }

    /**
     * @param jsonFile json file which should be returned as objet
     * @return the serialized object at the specified pdaf
     * @throws StreamWriteException Intermediate base class for all read-side streaming processing problems,
     * includingparsing and input value coercion problems.
     * @throws DatabindException Intermediate base class for all databind level processing problems, asdistinct from
     * stream-level problems or I/O issues below.
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     */
    public static ResultModel getResultModelFromPath(File jsonFile) throws StreamReadException, DatabindException, IOException {
        return new ObjectMapper().readValue(jsonFile, ResultModel.class);
    }

}
