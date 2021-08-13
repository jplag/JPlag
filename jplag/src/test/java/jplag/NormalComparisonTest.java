package jplag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NormalComparisonTest extends TestBase {

    /**
     * The simple duplicate contains obvious plagiarism.
     */
    @Test
    public void testSimpleDuplicate() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("SimpleDuplicate");

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getComparisons().size());
        assertEquals(1, result.getComparisons().get(0).matches.size());
        assertEquals(1, result.getSimilarityDistribution()[6]);
        assertEquals(62.07f, result.getComparisons().get(0).percent(), 0.1f);
    }

    /**
     * The classes in no duplicate have nearly nothing in common.
     */
    @Test
    public void testNoDuplicate() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("NoDuplicate");

        assertEquals(3, result.getNumberOfSubmissions());
        assertEquals(3, result.getComparisons().size());

        result.getAllComparisons().forEach(comparison -> {
            assertEquals(0f, comparison.percent(), 0.1f);
        });
    }

    /**
     * This case is more complex and consists out of 5 submissions with different plagiarism.
     * A is the original code (coming from an older JPlag version)
     * B is a partial copy of that code
     * C is a full copy of that code
     * D is dumb plagiarism, e.g., changed variable names, additional unneeded code, ...
     * E is just a Hello World Java program
     */
    @Test
    public void partialPlagiarism() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("PartialPlagiarism");

        assertEquals(5, result.getNumberOfSubmissions());
        // TODO SH: Add way more detailed assertions
    }

}
