package de.jplag;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;

/**
 * Test class for the very basic functionality of JPlag.
 * @author Timur Saglam
 */
class BasicFunctionalityTest extends TestBase {

    private static final int DISTRIBUTION_INDEX = 66;

    @Test
    @DisplayName("test submissions that contain obvious plagiarism")
    void testSimpleDuplicate() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("SimpleDuplicate");

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(1, result.getAllComparisons().get(0).matches().size());
        assertEquals(1, result.getSimilarityDistribution()[DISTRIBUTION_INDEX]);
        assertEquals(0.666, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    @Test
    @DisplayName("test submissions with a custom minimum token match")
    void testWithMinTokenMatch() throws ExitException {
        var expectedDistribution = new int[100];
        expectedDistribution[96] = 1;
        JPlagResult result = runJPlag("SimpleDuplicate", it -> it.withMinimumTokenMatch(4));

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(2, result.getAllComparisons().get(0).matches().size());
        assertArrayEquals(expectedDistribution, result.getSimilarityDistribution());
        assertEquals(0.9629, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    @Test
    @DisplayName("test submissions that are not similar")
    void testNoDuplicate() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("NoDuplicate");

        assertEquals(3, result.getNumberOfSubmissions());
        assertEquals(3, result.getAllComparisons().size());

        result.getAllComparisons().forEach(comparison -> assertEquals(0, comparison.similarity(), DELTA));
    }

    /**
     * This case is more complex and consists out of 5 submissions with different plagiarism. A is the original code (coming
     * from an older JPlag version) B is a partial copy of that code C is a full copy of that code D is dumb plagiarism,
     * e.g., changed variable names, additional unneeded code, ... E is just a Hello World Java errorConsumer.
     * @throws ExitException when JPlag causes an error.
     */
    @Test
    @DisplayName("test multiple submissions with varying degree of plagiarism")
    void testPartialPlagiarism() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("PartialPlagiarism");

        assertEquals(5, result.getNumberOfSubmissions());
        assertEquals(10, result.getAllComparisons().size());

        // All comparisons with E shall have no matches
        result.getAllComparisons().stream()
                .filter(comparison -> "E".equals(comparison.secondSubmission().getName()) || "E".equals(comparison.firstSubmission().getName()))
                .forEach(comparison -> assertEquals(0, comparison.similarity(), DELTA));

        // Hard coded assertions on selected comparisons
        assertEquals(0.237, getSelectedPercent(result, "A", "B"), DELTA);
        assertEquals(0.996, getSelectedPercent(result, "A", "C"), DELTA);
        assertEquals(0.760, getSelectedPercent(result, "A", "D"), DELTA);
        assertEquals(0.237, getSelectedPercent(result, "B", "C"), DELTA);
        assertEquals(0.283, getSelectedPercent(result, "B", "D"), DELTA);
        assertEquals(0.760, getSelectedPercent(result, "C", "D"), DELTA);

        // More detailed assertions for the plagiarism in A-D
        var biggestMatch = getSelectedComparison(result, "A", "D");
        assertEquals(0.959, biggestMatch.get().maximalSimilarity(), DELTA);
        assertEquals(0.630, biggestMatch.get().minimalSimilarity(), DELTA);
        assertEquals(12, biggestMatch.get().matches().size());
    }

    @Test
    @DisplayName("test basic functionality for varying minimum token match values.")
    void testHighMinimumTokenMatch() throws ExitException {
        for (int i = 10; i < 50; i++) {
            int minimumTokenMatch = i;
            JPlagResult result = runJPlag("PartialPlagiarism", it -> it.withMinimumTokenMatch(minimumTokenMatch));
            if (minimumTokenMatch <= 12) {
                assertEquals(5, result.getNumberOfSubmissions());
                assertEquals(10, result.getAllComparisons().size());
            } else {
                assertEquals(4, result.getNumberOfSubmissions());
                assertEquals(6, result.getAllComparisons().size());
            }
        }
    }

    @Test
    @DisplayName("test single-files as submissions (no folders)")
    void testSingleFileSubmisssions() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("SimpleSingleFile");

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(1, result.getSimilarityDistribution()[DISTRIBUTION_INDEX]);
        assertEquals(0.666, result.getAllComparisons().get(0).similarity(), DELTA);

        var matches = result.getAllComparisons().get(0).matches();
        // Run JPlag for same files but in submission folders:
        var expectedMatches = runJPlagWithDefaultOptions("SimpleDuplicate").getAllComparisons().get(0).matches();
        assertEquals(expectedMatches.size(), matches.size());

        for (int i = 0; i < matches.size(); i++) {
            assertEquals(expectedMatches.get(i).startOfFirst(), matches.get(i).startOfFirst());
            assertEquals(expectedMatches.get(i).startOfSecond(), matches.get(i).startOfSecond());
            assertEquals(expectedMatches.get(i).lengthOfFirst(), matches.get(i).lengthOfFirst());
            assertEquals(expectedMatches.get(i).lengthOfSecond(), matches.get(i).lengthOfSecond());
        }

    }

}
