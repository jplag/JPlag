package de.jplag.end_to_end_testing.helper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
import de.jplag.end_to_end_testing.model.ResultDescription;
import de.jplag.options.LanguageOption;

public class JsonHelper {
    /**
     * private constructor to prevent instantiation
     */
    private JsonHelper() {
        // For Serialization
    }

    /**
     * @param directoryName name to the result path
     * @param languageOption for which the results are to be loaded
     * @return ResultDescription as serialized object
     * @throws IOException is thrown for all problems that may occur while parsing the json file. This includes both reading
     */
    public static List<ResultDescription> getJsonModelListFromPath(String directoryName, LanguageOption languageOption) throws IOException {

        Path jsonPath = Path.of(TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toString(), languageOption.toString(), directoryName + ".json");

        if (jsonPath.toFile().exists() && jsonPath.toFile().length() > 0) {

            return Arrays.asList(new ObjectMapper().readValue(jsonPath.toFile(), ResultDescription[].class));
        } else {
            return Collections.<ResultDescription>emptyList();
        }
    }

    /**
     * Saves the passed object as a json file to the given path
     * @param resultDescriptionist list of elements to be saved
     * @param directoryName path to the temporary storage location
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     */
    public static void writeJsonModelsToJsonFile(List<ResultDescription> resultDescriptionist, String directoryName, LanguageOption languageOption)
            throws IOException {
        // create an instance of DefaultPrettyPrinter
        // new DefaultPrettyPrinter()
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

        Path temporaryDirectory = Path.of(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME.toString(), languageOption.toString(),
                directoryName + ".json");

        FileHelper.createDirectoryIfItDoseNotExist(temporaryDirectory.getParent().toFile());
        FileHelper.createFileIfItDoseNotExist(temporaryDirectory.toFile());

        // convert book object to JSON file

        writer.writeValue(temporaryDirectory.toFile(), resultDescriptionist.toArray());

    }
}
