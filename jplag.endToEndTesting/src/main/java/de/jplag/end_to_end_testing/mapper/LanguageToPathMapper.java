package de.jplag.end_to_end_testing.mapper;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
import de.jplag.options.LanguageOption;

public class LanguageToPathMapper {

    private LanguageToPathMapper() {
        // private constructor to prevent instantiation
    }

    /**
     * @return Mapper for the language specific stored result json data
     */
    private static final HashMap<LanguageOption, Path> temporaryResultPathMapper() {
        HashMap<LanguageOption, Path> languageSpecificResultMapper = new HashMap<>();
        languageSpecificResultMapper.put(LanguageOption.JAVA,
                Path.of(TestDirectoryConstants.TEMPORARY_RESULT_DIRECTORY_NAME.toString(), LanguageOption.JAVA.toString()));

        return languageSpecificResultMapper;
    }

    /**
     * @return Mapper for the language-specific stored test plagiarism classes
     */
    private static final HashMap<LanguageOption, List<Path>> resourcePathMapper() {
        HashMap<LanguageOption, List<Path>> languageSpecificResultMapper = new HashMap<>();
        languageSpecificResultMapper.put(LanguageOption.JAVA,
                Collections.unmodifiableList(Arrays.asList(TestDirectoryConstants.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO)));
        return languageSpecificResultMapper;
    }

    /**
     * @return Mapper for the language specific stored result json data
     */
    private static final HashMap<LanguageOption, Path> resultPathMapper() {
        HashMap<LanguageOption, Path> languageSpecificResultMapper = new HashMap<>();
        languageSpecificResultMapper.put(LanguageOption.JAVA, TestDirectoryConstants.BASE_PATH_TO_JAVA_RESULT_JSON);

        return languageSpecificResultMapper;
    }

    /**
     * returns the path to which the temporary results for the tests are to be stored.
     * @param languageOption language for which the temporary result path is needed
     * @return path to which the temporary results for the tests are to be stored.
     */
    public static Path getTemporaryResultPathFromLanguageOption(LanguageOption languageOption) {
        return temporaryResultPathMapper().get(languageOption);
    }

    /**
     * Path to the stored json data for the language specific tests
     * @param languageOption for which the result path is needed
     * @return path to result json file
     */
    public static Path getTestResultPathFromLanguageOption(LanguageOption languageOption) {
        return resultPathMapper().get(languageOption);
    }

    /**
     * Paths to the resource folders where the language-specific data to be tested is located.
     * @param languageOption for which the resource paths are needed
     * @return List of paths to the added resource files for the tests
     */
    public static List<Path> getResourcePathsFromLanguageOption(LanguageOption languageOption) {
        return resourcePathMapper().get(languageOption);
    }
}
