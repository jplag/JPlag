package de.jplag;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jplag.exceptions.ExitException;

public class NewJavaFeaturesTest extends TestBase {
    private static final String EXCLUSION_FILE_NAME = "blacklist.txt";
    private static final String ROOT_DIRECTORY = "NewJavaFeatures";

    @Test
    public void testJavaFeatureDuplicates() throws ExitException {
        JPlagResult result = runJPlagWithExclusionFile(ROOT_DIRECTORY, EXCLUSION_FILE_NAME);

        // Two submissions, one comparison with 100% match:
        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getComparisons().size());
        var comparison = result.getComparisons().get(0);
        assertEquals(92.1, comparison.similarity(), DELTA);

        // Five matches that each start at the same position:
        assertEquals(4, comparison.getMatches().size());
    }

}
