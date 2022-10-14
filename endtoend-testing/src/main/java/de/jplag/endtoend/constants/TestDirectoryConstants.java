package de.jplag.endtoend.constants;

import java.nio.file.Path;

/**
 * All constant values that are needed in the test cases or helper classes.
 */
public final class TestDirectoryConstants {

    private TestDirectoryConstants() {
        // private constructor to prevent instantiation
    }

    /**
     * Base path to the saved results
     */
    public static final Path BASE_PATH_TO_RESULT_JSON = Path.of("src", "test", "resources", "results");

    /**
     * Base path to the endToEnd testing resources
     */
    public static final Path BASE_PATH_TO_LANGUAGE_RESOURCES = Path.of("src", "test", "resources", "languageTestFiles");

    /**
     * Create the complete path to the submission files. Here the temporary system path is extended with the
     * "SUBMISSION_DIRECTORY_NAME", which is predefined in this class.
     */
    public static final Path TEMPORARY_SUBMISSION_DIRECTORY_NAME = Path.of("target", "testing-directory-submission");
}
