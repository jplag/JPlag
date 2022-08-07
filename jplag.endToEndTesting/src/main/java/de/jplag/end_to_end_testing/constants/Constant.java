package de.jplag.end_to_end_testing.constants;

import java.nio.file.Path;
import java.util.HashMap;

import de.jplag.options.LanguageOption;

/**
 * All constant values that are needed in the test cases or helper classes.
 */
public final class Constant {

    private Constant() {
        // private constructor to prevent instantiation
    }

    /**
     * Name for the folder to copy the submissions to in order to test them with JPlag
     */
    public static final String TEMPORARY_DIRECTORY_NAME = "testing-directory-submission";
    /**
     * Create the complete path to the submission files. Here the temporary system path is extended with the
     * "TEMPORARY_DIRECTORY_NAME", which is predefined in this class.
     */
    public static final String TEMPORARY_SUBMISSION_DIRECTORY_NAME = Path.of("target", TEMPORARY_DIRECTORY_NAME).toString();

    /**
     * Base path to the created plagiarism and the main file located in the project resources.
     */
    public static final Path BASE_PATH_TO_JAVA_RESOURCES_SORTALGO = Path.of("src", "test", "resources", "java", "sortAlgo");

    /**
     * @return mapper for the language specific stored result json data
     */
    public static final HashMap<LanguageOption, Path> RESULT_PATH_MAPPER() {
        HashMap<LanguageOption, Path> languageSpecificResultMapper = new HashMap<LanguageOption, Path>();
        languageSpecificResultMapper.put(LanguageOption.JAVA, Constant.BASE_PATH_TO_JAVA_RESULT_JSON);
        languageSpecificResultMapper.put(LanguageOption.C_SHARP, Constant.BASE_PATH_TO_C_SHARP_RESULT_JSON);

        return languageSpecificResultMapper;
    }

    private static final Path BASE_PATH_TO_RESULT_JSON = Path.of("src", "test", "resources", "results");
    /**
     * Base path to the saved results of the previous tests in a *.json file for java
     */
    private static final Path BASE_PATH_TO_JAVA_RESULT_JSON = Path.of(BASE_PATH_TO_RESULT_JSON.toString(), "JavaResult.json");
    /**
     * Base path to the saved results of the previous tests in a *.json file for csharp
     */
    private static final Path BASE_PATH_TO_C_SHARP_RESULT_JSON = Path.of(BASE_PATH_TO_RESULT_JSON.toString(), "CSharpResult.json");

}
