package de.jplag;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jplag.exceptions.ExitException;

public class NewJavaFeaturesTest extends TestBase {
    private static final int EXPECTED_MATCHES = 6; // might change if you add files to the submissions
    private static final double EXPECTED_SIMILARITY = 96.0; // might change if you add files to the submissions

    private static final String EXCLUSION_FILE_NAME = "blacklist.txt";
    private static final String ROOT_DIRECTORY = "NewJavaFeatures";
    private static final String CHANGE_MESSAGE = "Number of %s changed! If intended, modify the test case!";

    @Test
    public void testJavaFeatureDuplicates() throws ExitException {
        JPlagResult result = runJPlagWithExclusionFile(ROOT_DIRECTORY, EXCLUSION_FILE_NAME);

        // Ensure test input did not change:
        assertEquals(String.format(CHANGE_MESSAGE, "Submissions"), 2, result.getNumberOfSubmissions());
        for (Submission submission : result.getSubmissions().getSubmissions()) {
            assertEquals(String.format(CHANGE_MESSAGE, "Files"), 6, submission.getFiles().size());
        }
        assertEquals(String.format(CHANGE_MESSAGE, "Comparisons"), 1, result.getComparisons().size());

        // Check similarity and number of matches:
        var comparison = result.getComparisons().get(0);
        assertEquals(EXPECTED_SIMILARITY, comparison.similarity(), DELTA);
        assertEquals(EXPECTED_MATCHES, comparison.getMatches().size());
    }

}
