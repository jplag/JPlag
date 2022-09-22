package de.jplag;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;

public class ParallelComparisonTest extends TestBase {

    /**
     * The simple duplicate contains obvious plagiarism.
     */
    @Test
    void testSimpleDuplicate() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("SimpleDuplicate");

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(1, result.getAllComparisons().get(0).matches().size());
        assertEquals(1, result.getSimilarityDistribution()[3]);
        assertEquals(0.6207, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    /**
     * The simple duplicate with a custom min token match.
     */
    @Test
    void testWithMinTokenMatch() throws ExitException {
        var expectedDistribution = new int[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        JPlagResult result = runJPlag("SimpleDuplicate", it -> it.withMinimumTokenMatch(5));

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(2, result.getAllComparisons().get(0).matches().size());
        assertArrayEquals(expectedDistribution, result.getSimilarityDistribution());
        assertEquals(0.9655, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    /**
     * The classes in no duplicate have nearly nothing in common.
     */
    @Test
    void testNoDuplicate() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("NoDuplicate");

        assertEquals(3, result.getNumberOfSubmissions());
        assertEquals(3, result.getAllComparisons().size());

        result.getAllComparisons().forEach(comparison -> assertEquals(0, comparison.similarity(), DELTA));
    }

    /**
     * This case is more complex and consists out of 5 submissions with different plagiarism. A is the original code (coming
     * from an older JPlag version) B is a partial copy of that code C is a full copy of that code D is dumb plagiarism,
     * e.g., changed variable names, additional unneeded code, ... E is just a Hello World Java errorConsumer
     */
    @Test
    void testPartialPlagiarism() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("PartialPlagiarism");

        assertEquals(5, result.getNumberOfSubmissions());
        assertEquals(10, result.getAllComparisons().size());

        // All comparisons with E shall have no matches
        result.getAllComparisons().stream()
                .filter(comparison -> comparison.secondSubmission().getName().equals("E") || comparison.firstSubmission().getName().equals("E"))
                .forEach(comparison -> assertEquals(0, comparison.similarity(), DELTA));

        // Hard coded assertions on selected comparisons
        assertEquals(0.246, getSelectedPercent(result, "A", "B"), DELTA);
        assertEquals(0.997, getSelectedPercent(result, "A", "C"), DELTA);
        assertEquals(0.779, getSelectedPercent(result, "A", "D"), DELTA);
        assertEquals(0.246, getSelectedPercent(result, "B", "C"), DELTA);
        assertEquals(0.283, getSelectedPercent(result, "B", "D"), DELTA);
        assertEquals(0.779, getSelectedPercent(result, "C", "D"), DELTA);

        // More detailed assertions for the plagiarism in A-D
        var biggestMatch = getSelectedComparison(result, "A", "D");
        assertEquals(0.964, biggestMatch.get().maximalSimilarity(), DELTA);
        assertEquals(0.653, biggestMatch.get().minimalSimilarity(), DELTA);
        assertEquals(12, biggestMatch.get().matches().size());
    }

    // TODO SH: Methods like this should be moved to the API and also should accept wildcards
    private double getSelectedPercent(JPlagResult result, String nameA, String nameB) {
        return getSelectedComparison(result, nameA, nameB).map(JPlagComparison::similarity).orElse(-1.0);
    }

    private Optional<JPlagComparison> getSelectedComparison(JPlagResult result, String nameA, String nameB) {
        return result.getAllComparisons().stream()
                .filter(comparison -> comparison.firstSubmission().getName().equals(nameA) && comparison.secondSubmission().getName().equals(nameB)
                        || comparison.firstSubmission().getName().equals(nameB) && comparison.secondSubmission().getName().equals(nameA))
                .findFirst();
    }

    @Test
    void testMultiRootDirNoBasecode() throws ExitException {
        List<String> paths = List.of(getBasePath("basecode"), getBasePath("SimpleDuplicate")); // 3 + 2 submissions.
        JPlagResult result = runJPlag(paths, it -> it);
        assertEquals(5, result.getNumberOfSubmissions());
    }

    @Test
    void testMultiRootDirSeparateBasecode() throws ExitException {
        String basecodePath = getBasePath("basecode-base");
        List<String> paths = List.of(getBasePath("basecode"), getBasePath("SimpleDuplicate")); // 3 + 2 submissions.
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionName(basecodePath));
        assertEquals(5, result.getNumberOfSubmissions());
    }

    @Test
    void testMultiRootDirBasecodeInSubmissionDir() throws ExitException {
        String basecodePath = getBasePath("basecode", "base");
        List<String> paths = List.of(getBasePath("basecode"), getBasePath("SimpleDuplicate")); // 2 + 2 submissions.
        JPlagResult result = runJPlag(paths, it -> it.withBaseCodeSubmissionName(basecodePath));
        assertEquals(4, result.getNumberOfSubmissions());
    }

    @Test
    void testMultiRootDirBasecodeName() {
        List<String> paths = List.of(getBasePath("basecode"), getBasePath("SimpleDuplicate"));
        String basecodePath = "base"; // Should *not* find basecode/base
        assertThrows(BasecodeException.class, () -> runJPlag(paths, it -> it.withBaseCodeSubmissionName(basecodePath)));
    }

    @Test
    void testDisjunctNewAndOldRootDirectories() throws ExitException {
        List<String> newDirectories = List.of(getBasePath("SimpleDuplicate")); // 2 submissions
        List<String> oldDirectories = List.of(getBasePath("basecode")); // 3 submissions
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it);
        int numberOfExpectedComparison = 1 + 3 * 2;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }

    @Test
    void testOverlappingNewAndOldDirectoriesOverlap() throws ExitException {
        List<String> newDirectories = List.of(getBasePath("SimpleDuplicate")); // 2 submissions
        List<String> oldDirectories = List.of(getBasePath("SimpleDuplicate"));
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it);
        int numberOfExpectedComparison = 1;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }

    @Test
    void testBasecodeInOldDirectory() throws ExitException {
        String basecodePath = getBasePath("basecode", "base");
        List<String> newDirectories = List.of(getBasePath("SimpleDuplicate")); // 2 submissions
        List<String> oldDirectories = List.of(getBasePath("basecode")); // 3 - 1 submissions
        JPlagResult result = runJPlag(newDirectories, oldDirectories, it -> it.withBaseCodeSubmissionName(basecodePath));
        int numberOfExpectedComparison = 1 + 2 * 2;
        assertEquals(numberOfExpectedComparison, result.getAllComparisons().size());
    }
}
