package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.exceptions.ExitException;

public class NewJavaFeaturesTest extends TestBase {

    private final Logger logger = LoggerFactory.getLogger(NewJavaFeaturesTest.class);

    private static final int EXPECTED_MATCHES = 6; // might change if you add files to the submissions
    private static final double EXPECTED_SIMILARITY = 0.96; // might change if you add files to the submissions
    private static final String EXPECTED_JAVA_VERSION = "17"; // might change with newer JPlag versions

    private static final String EXCLUSION_FILE_NAME = "blacklist.txt";
    private static final String ROOT_DIRECTORY = "NewJavaFeatures";
    private static final String CHANGE_MESSAGE = "Number of %s changed! If intended, modify the test case!";
    private static final String VERSION_MISMATCH_MESSAGE = "Using Java version %s instead of %s may skew the results";
    private static final String VERSION_MATCH_MESSAGE = "Java version matches, but results deviate from expected values";
    private static final String JAVA_VERSION_KEY = "java.version";

    @Test
    @DisplayName("test comparison of Java files with modern language features")
    public void testJavaFeatureDuplicates() throws ExitException {

        JPlagResult result = runJPlagWithExclusionFile(ROOT_DIRECTORY, EXCLUSION_FILE_NAME);

        // Ensure test input did not change:
        assertEquals(2, result.getNumberOfSubmissions(), String.format(CHANGE_MESSAGE, "Submissions"));
        for (Submission submission : result.getSubmissions().getSubmissions()) {
            assertEquals(6, submission.getFiles().size(), String.format(CHANGE_MESSAGE, "Files"));
        }
        assertEquals(1, result.getAllComparisons().size(), String.format(CHANGE_MESSAGE, "Comparisons"));

        // Check similarity and number of matches:
        var comparison = result.getAllComparisons().get(0);
        String versionMessage = createJavaVersionMessage();
        assertEquals(EXPECTED_SIMILARITY, comparison.similarity(), DELTA, versionMessage);
        assertEquals(EXPECTED_MATCHES, comparison.matches().size(), versionMessage);
    }

    private String createJavaVersionMessage() {
        String actualJavaVersion = System.getProperty(JAVA_VERSION_KEY);
        String message = VERSION_MATCH_MESSAGE;
        if (!actualJavaVersion.startsWith(EXPECTED_JAVA_VERSION)) {
            message = VERSION_MISMATCH_MESSAGE.formatted(actualJavaVersion, EXPECTED_JAVA_VERSION);
            logger.error(message);
        }
        return message;
    }

}
