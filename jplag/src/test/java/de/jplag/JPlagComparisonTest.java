package de.jplag;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.jplag.exceptions.ExitException;

public class JPlagComparisonTest extends TestBase {

    private static final String[] FILES = {"GSTiling.java", "Match.java", "Matches.java", "Structure.java", "Submission.java", "Table.java",
            "Token.java"};
    private static final String[] FILES_2 = {"Match.java", "Matches.java", "Structure.java", "Submission.java", "Table.java", "Token.java"};
    private static final String[] FILES_3 = {"Table.java", "Token.java"};

    /**
     * Tests whether the right set of files corresponding to a set of matches is produced by the {@link JPlagComparison}.
     */
    @Test
    public void testFilesOfMatches() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("PartialPlagiarism");
        List<JPlagComparison> comparisons = result.getComparisons();
        assertEquals(5, result.getNumberOfSubmissions());
        assertEquals(10, comparisons.size());
        for (int submission = 0; submission < 2; submission++) { // for both submissions in a comparison
            assertArrayEquals(FILES, comparisons.get(0).files(submission));
            for (int i = 1; i < 3; i++) {
                assertArrayEquals(FILES_2, comparisons.get(i).files(submission));
            }
            for (int i = 3; i < 6; i++) {
                assertArrayEquals(FILES_3, comparisons.get(i).files(submission));
            }
            for (int i = 6; i < 10; i++) {
                assertArrayEquals(new String[] {}, comparisons.get(i).files(submission));
            }
        }

    }
}
