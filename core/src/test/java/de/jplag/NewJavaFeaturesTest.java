package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;

public class NewJavaFeaturesTest extends TestBase {
    private static final int EXPECTED_MATCHES = 6; // might change if you add files to the submissions
    private static final double EXPECTED_SIMILARITY = 0.96; // might change if you add files to the submissions

    private static final String EXCLUSION_FILE_NAME = "blacklist.txt";
    private static final String ROOT_DIRECTORY = "NewJavaFeatures";
    private static final String CHANGE_MESSAGE = "Number of %s changed! If intended, modify the test case!";

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
        assertEquals(EXPECTED_SIMILARITY, comparison.similarity(), DELTA);
        assertEquals(EXPECTED_MATCHES, comparison.matches().size());
    }

}
