package de.jplag.endtoend.helper;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.jplag.JPlagComparison;
import de.jplag.Submission;

/**
 * Helper class to perform all necessary additional functions for the endToEnd tests.
 */
public final class TestSuiteHelper {

    /**
     * private constructor to prevent instantiation.
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
        return Stream.of(jPlagComparison.firstSubmission(), jPlagComparison.secondSubmission()).map(Submission::getRoot)
                .map(FileHelper::getFileNameWithoutFileExtension).sorted().collect(Collectors.joining("-"));

    }
}
