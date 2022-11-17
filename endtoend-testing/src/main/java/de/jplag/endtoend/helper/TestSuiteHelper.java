package de.jplag.endtoend.helper;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import de.jplag.JPlagComparison;
import de.jplag.Language;
import de.jplag.Submission;
import de.jplag.endtoend.constants.TestDirectoryConstants;

/**
 * Helper class to perform all necessary additional functions for the endToEnd tests.
 */
public class TestSuiteHelper {

    /**
     * private constructor to prevent instantiation
     */
    private TestSuiteHelper() {
        // For Serialization
    }

    /**
     * Creates a unique identifier from the submissions in the JPlagComparison object which is used to find the results in
     * the json files.
     * @param jPlagComparison object from which the hash should be generated
     * @return unique identifier for test case recognition
     */
    public static String getTestIdentifier(JPlagComparison jPlagComparison) {
        return List.of(jPlagComparison.firstSubmission(), jPlagComparison.secondSubmission()).stream().map(Submission::getRoot)
                .map(FileHelper::getFileNameWithoutFileExtension).sorted().collect(Collectors.joining("-"));

    }

    /**
     * Returns the file pointing to the directory of the submissions for the given language and result json. The result
     * json's name is expected to be equal to the test suite identifier.
     * @param language is the language for the tests
     * @param resultJSON is the json containing the expected values
     * @return returns the directory of the submissions
     */
    public static File getSubmissionDirectory(Language language, File resultJSON) {
        return getSubmissionDirectory(language, FileHelper.getFileNameWithoutFileExtension(resultJSON));
    }

    /**
     * Returns the file pointing to the directory of the submissions for the given language and test suite identifier as
     * described in the Readme.md.
     * @param language is the langauge for the tests
     * @param testSuiteIdentifier is the test suite identifier of the tests
     * @return returns the directory of the submissions
     */
    public static File getSubmissionDirectory(Language language, String testSuiteIdentifier) {
        return TestDirectoryConstants.BASE_PATH_TO_LANGUAGE_RESOURCES.resolve(language.getIdentifier()).resolve(testSuiteIdentifier).toFile();
    }
}
