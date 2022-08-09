package de.jplag.end_to_end_testing.constants;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.jplag.options.LanguageOption;

/**
 * All constant values that are needed in the test cases or helper classes.
 */
public final class TestDirectoryConstants {

    private TestDirectoryConstants() {
        // private constructor to prevent instantiation
    }

    /**
     * Create the complete path to the submission files. Here the temporary system path is extended with the
     * "SUBMISSION_DIRECTORY_NAME", which is predefined in this class.
     */
    public static final String TEMPORARY_SUBMISSION_DIRECTORY_NAME = Path.of("target", "testing-directory-submission").toString();
    /**
     * Create the complete path to the temporary result files. Here the temporary system path is extended with the
     * "RESULT_DIRECTORY_NAME", which is predefined in this class.
     */
    private static final Path TEMPORARY_RESULT_DIRECTORY_NAME = Path.of("target", "testing-directory-temporary-result");

    /**
     * Base path to the created plagiarism and the main file located in the project resources.
     */
    public static final Path BASE_PATH_TO_JAVA_RESOURCES_SORTALGO = Path.of("src", "test", "resources", "java", "sortAlgo");

    /**
     * Base path to the created plagiarism and the main file located in the project resources.
     */
    public static final Path BASE_PATH_TO_C_SHARP_RESOURCES_SORTALGO = Path.of("src", "test", "resources", "csharp", "sortAlgo");

    /**
     * @return mapper for the language specific stored result json data
     */
    public static final HashMap<LanguageOption, List<Path>> RESOURCE_PATH_MAPPER() {
        HashMap<LanguageOption, List<Path>> languageSpecificResultMapper = new HashMap<LanguageOption, List<Path>>();
        languageSpecificResultMapper.put(LanguageOption.JAVA, Collections.unmodifiableList(Arrays.asList(BASE_PATH_TO_JAVA_RESOURCES_SORTALGO)));
        languageSpecificResultMapper.put(LanguageOption.C_SHARP,
                Collections.unmodifiableList(Arrays.asList(BASE_PATH_TO_C_SHARP_RESOURCES_SORTALGO)));

        return languageSpecificResultMapper;
    }

    /**
     * @return mapper for the language specific stored result json data
     */
    public static final HashMap<LanguageOption, Path> RESULT_PATH_MAPPER() {
        HashMap<LanguageOption, Path> languageSpecificResultMapper = new HashMap<LanguageOption, Path>();
        languageSpecificResultMapper.put(LanguageOption.JAVA, BASE_PATH_TO_JAVA_RESULT_JSON);
        languageSpecificResultMapper.put(LanguageOption.C_SHARP, BASE_PATH_TO_C_SHARP_RESULT_JSON);

        return languageSpecificResultMapper;
    }

    /**
     * @return mapper for the language specific stored result json data
     */
    public static final HashMap<LanguageOption, Path> TEMPORARY_RESULT_PATH_MAPPER() {
        HashMap<LanguageOption, Path> languageSpecificResultMapper = new HashMap<LanguageOption, Path>();
        languageSpecificResultMapper.put(LanguageOption.JAVA, Path.of(TEMPORARY_RESULT_DIRECTORY_NAME.toString(), LanguageOption.JAVA.toString()));
        languageSpecificResultMapper.put(LanguageOption.C_SHARP,
                Path.of(TEMPORARY_RESULT_DIRECTORY_NAME.toString(), LanguageOption.C_SHARP.toString()));

        return languageSpecificResultMapper;
    }

    /**
     * Base path to the saved results
     */
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
