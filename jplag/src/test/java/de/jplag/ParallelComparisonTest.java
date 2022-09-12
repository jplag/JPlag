package de.jplag;

import static de.jplag.strategy.ComparisonMode.PARALLEL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;
import de.jplag.strategy.ParallelComparisonStrategy;

/**
 * Currently just a copy of {@link NormalComparisonTest} but for the {@link ParallelComparisonStrategy}. // TODO TS:
 * de-duplicate this
 * @author Timur Saglam
 */
public class ParallelComparisonTest extends TestBase {

    /**
     * The simple duplicate contains obvious plagiarism.
     */
    @Test
    public void testSimpleDuplicate() throws ExitException {
        JPlagResult result = runJPlag("SimpleDuplicate", it -> it.withComparisonMode(PARALLEL));

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(1, result.getAllComparisons().get(0).matches().size());
        assertEquals(1, result.getSimilarityDistribution()[3]);
        assertEquals(62.07, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    /**
     * The classes in no duplicate have nearly nothing in common.
     */
    @Test
    public void testNoDuplicate() throws ExitException {
        JPlagResult result = runJPlag("NoDuplicate", it -> it.withComparisonMode(PARALLEL));

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
    public void testPartialPlagiarism() throws ExitException {
        JPlagResult result = runJPlag("PartialPlagiarism", it -> it.withComparisonMode(PARALLEL));

        assertEquals(5, result.getNumberOfSubmissions());
        assertEquals(10, result.getAllComparisons().size());

        // All comparisons with E shall have no matches
        result.getAllComparisons().stream()
                .filter(comparison -> comparison.secondSubmission().getName().equals("E") || comparison.firstSubmission().getName().equals("E"))
                .forEach(comparison -> assertEquals(0, comparison.similarity(), DELTA));

        // Hard coded assertions on selected comparisons
        assertEquals(24.6, getSelectedPercent(result, "A", "B"), DELTA);
        assertEquals(99.7, getSelectedPercent(result, "A", "C"), DELTA);
        assertEquals(77.9, getSelectedPercent(result, "A", "D"), DELTA);
        assertEquals(24.6, getSelectedPercent(result, "B", "C"), DELTA);
        assertEquals(28.3, getSelectedPercent(result, "B", "D"), DELTA);
        assertEquals(77.9, getSelectedPercent(result, "C", "D"), DELTA);

        // More detailed assertions for the plagiarism in A-D
        var biggestMatch = getSelectedComparison(result, "A", "D");
        assertEquals(96.4, biggestMatch.get().maximalSimilarity(), DELTA);
        assertEquals(65.3, biggestMatch.get().minimalSimilarity(), DELTA);
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
}
