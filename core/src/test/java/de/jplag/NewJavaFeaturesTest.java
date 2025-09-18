package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.exceptions.ExitException;

/**
 * Unit test for verifying plagiarism detection on Java files that use modern language features. This test ensures that
 * the plagiarism detection works correctly for Java version 21 features, checking the number of matches, files, and
 * similarity scores. It also allows running in CI environments even if the Java version differs.
 */
class NewJavaFeaturesTest extends TestBase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int EXPECTED_MATCHES = 9; // might change if you add files to the submissions
    private static final int NUMBER_OF_TEST_FILES = 10;
    private static final double EXPECTED_SIMILARITY = 0.898; // might change if you add files to the submissions

    private static final String EXCLUSION_FILE_NAME = "blacklist.txt";
    private static final String ROOT_DIRECTORY = "NewJavaFeatures";
    private static final String CHANGE_MESSAGE = "Number of %s changed! If intended, modify the test case!";
    private static final String VERSION_MISMATCH_MESSAGE = "Using Java version %s instead of %s may skew the results.";
    private static final String VERSION_MATCH_MESSAGE = "Java version matches, but results deviate from expected values";
    private static final String CI_VARIABLE = "CI";

    private static final int EXPECTED_JAVA_VERSION = 25;

    @Test
    @DisplayName("test comparison of Java files with modern language features")
    void testJavaFeatureDuplicates() throws ExitException {
        // pre-condition
        int javaVersion = Runtime.version().feature();
        boolean isCiRun = System.getenv(CI_VARIABLE) != null;
        assumeTrue(javaVersion == EXPECTED_JAVA_VERSION || isCiRun, VERSION_MISMATCH_MESSAGE.formatted(javaVersion, EXPECTED_JAVA_VERSION));

        JPlagResult result = runJPlagWithExclusionFile(ROOT_DIRECTORY, EXCLUSION_FILE_NAME);

        for (Submission submission : result.getSubmissions().getSubmissions()) {
            logger.info(TokenPrinter.printTokens(submission.getTokenList(), submission.getRoot()));
        }

        // Ensure test input did not change:
        assertEquals(2, result.getNumberOfSubmissions(), String.format(CHANGE_MESSAGE, "Submissions"));
        for (Submission submission : result.getSubmissions().getSubmissions()) {
            assertEquals(NUMBER_OF_TEST_FILES, submission.getFiles().size(), String.format(CHANGE_MESSAGE, "Files"));
        }
        assertEquals(1, result.getAllComparisons().size(), String.format(CHANGE_MESSAGE, "Comparisons"));

        // Check similarity and number of matches:
        var comparison = result.getAllComparisons().get(0);
        assertEquals(EXPECTED_SIMILARITY, comparison.similarity(), DELTA, VERSION_MATCH_MESSAGE);
        assertEquals(EXPECTED_MATCHES, comparison.matches().size(), VERSION_MATCH_MESSAGE);
    }
}
