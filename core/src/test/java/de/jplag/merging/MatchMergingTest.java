package de.jplag.merging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.SharedTokenType;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * This class extends on {@link TestBase} and performs several test on Match Merging, in order to check its
 * functionality. Therefore it uses java programs and feds them into the JPlag pipeline. Results are stored before- and
 * after Match Merging and used for all tests. The samples named "original" and "plag" are from PROGpedia and under the
 * CC BY 4.0 license.
 */
class MatchMergingTest extends TestBase {
    private final JPlagOptions options;
    private List<Match> matches;
    private List<JPlagComparison> comparisonsBefore;
    private List<JPlagComparison> comparisonsAfter;
    private final LongestCommonSubsequenceSearch comparisonStrategy;
    private final SubmissionSet submissionSet;
    private static final int MINIMUM_NEIGHBOR_LENGTH = 1;
    private static final int MAXIMUM_GAP_SIZE = 10;
    private static final int MINIMUM_REQUIRED_MERGES = 0;

    MatchMergingTest() throws ExitException {
        options = getDefaultOptions("merging")
                .withMergingOptions(new MergingOptions(true, MINIMUM_NEIGHBOR_LENGTH, MAXIMUM_GAP_SIZE, MINIMUM_REQUIRED_MERGES));

        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        submissionSet = builder.buildSubmissionSet();

        comparisonStrategy = new LongestCommonSubsequenceSearch(options);
    }

    @BeforeEach
    void prepareTestState() {
        JPlagResult result = comparisonStrategy.compareSubmissions(submissionSet);
        comparisonsBefore = new ArrayList<>(result.getAllComparisons());

        if (options.mergingOptions().enabled()) {
            result = new MatchMerging(options).mergeMatchesOf(result);
        }
        comparisonsAfter = new ArrayList<>(result.getAllComparisons());

        comparisonsBefore.sort(Comparator.comparing(Object::toString));
        comparisonsAfter.sort(Comparator.comparing(Object::toString));
    }

    @Test
    @DisplayName("Test length of matches after Match Merging")
    void testBufferRemoval() {
        checkMatchLength(JPlagComparison::matches, options.minimumTokenMatch(), comparisonsAfter);
    }

    @Test
    @DisplayName("Test length of matches after Greedy String Tiling")
    void testGSTMatches() {
        checkMatchLength(JPlagComparison::matches, options.minimumTokenMatch(), comparisonsBefore);
    }

    @Test
    @DisplayName("Test length of ignored matches after Greedy String Tiling")
    void testGSTIgnoredMatches() {
        checkMatchLength(JPlagComparison::ignoredMatches, options.mergingOptions().minimumNeighborLength(), comparisonsBefore);
    }

    private void checkMatchLength(Function<JPlagComparison, List<Match>> matchFunction, int threshold, List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            matches = matchFunction.apply(comparison);
            for (Match match : matches) {
                assertTrue(match.lengthOfFirst() >= threshold);
                assertTrue(match.lengthOfSecond() >= threshold);
            }
        }
    }

    @Test
    @DisplayName("Test if similarity increased after Match Merging")
    void testSimilarityIncreased() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            assertTrue(comparisonsAfter.get(i).similarity() >= comparisonsBefore.get(i).similarity());
        }
    }

    @Test
    @DisplayName("Test if amount of matches reduced after Match Merging")
    void testFewerMatches() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            int totalMatchesAfter = comparisonsAfter.get(i).matches().size() + comparisonsAfter.get(i).ignoredMatches().size();
            int totalMatchesBefore = comparisonsBefore.get(i).matches().size() + comparisonsBefore.get(i).ignoredMatches().size();

            assertTrue(totalMatchesAfter <= totalMatchesBefore,
                    "Expected total matches after to be less than or equal to before, but got " + totalMatchesAfter + " > " + totalMatchesBefore);
        }
    }

    @Test
    @DisplayName("Test if amount of token increased after Match Merging")
    void testMoreToken() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            int tokensBeforeFirst = comparisonsBefore.get(i).firstSubmission().getTokenList().size();
            int tokensBeforeSecond = comparisonsBefore.get(i).secondSubmission().getTokenList().size();

            int tokensAfterFirst = comparisonsAfter.get(i).firstSubmission().getTokenList().size();
            int tokensAfterSecond = comparisonsAfter.get(i).secondSubmission().getTokenList().size();

            assertTrue(tokensAfterFirst >= tokensBeforeFirst);
            assertTrue(tokensAfterSecond >= tokensBeforeSecond);
        }
    }

    @Test
    @DisplayName("Test if amount of FILE_END token stayed the same")
    void testFileEnd() {
        int amountFileEndBefore = countFileEndTokens(comparisonsBefore);
        int amountFileEndAfter = countFileEndTokens(comparisonsAfter);

        assertEquals(amountFileEndBefore, amountFileEndAfter);
    }

    private int countFileEndTokens(List<JPlagComparison> comparisons) {
        int fileEndTokens = 0;
        for (JPlagComparison comparison : comparisons) {
            fileEndTokens += countFileEndTokens(comparison.firstSubmission());
            fileEndTokens += countFileEndTokens(comparison.secondSubmission());
        }
        return fileEndTokens;
    }

    private int countFileEndTokens(Submission submission) {
        return Math.toIntExact(submission.getTokenList().stream().filter(token -> SharedTokenType.FILE_END.equals(token.getType())).count());
    }

    @Test
    @DisplayName("Sanity check for match merging")
    void testSanity() {

        List<Match> matchesBefore = findComparison(comparisonsBefore, "sanityA.java", "sanityB.java").ignoredMatches();
        List<Match> matchesAfter = findComparison(comparisonsAfter, "sanityA.java", "sanityB.java").matches();

        List<Match> expectedBefore = List.of( //
                new Match(5, 3, 6, 6), //
                new Match(11, 12, 6, 6), //
                new Match(0, 0, 3, 3), //
                new Match(3, 18, 2, 2), //
                new Match(17, 20, 2, 2) //
        );

        List<Match> expectedAfter = List.of(new Match(5, 3, 12, 15));

        assertIterableEquals(expectedBefore, matchesBefore);

        assertIterableEquals(expectedAfter, matchesAfter);
    }

    private static JPlagComparison findComparison(List<JPlagComparison> comparisons, String firstName, String secondName) {
        return comparisons.stream()
                .filter(it -> firstName.equals(it.firstSubmission().getName()) && secondName.equals(it.secondSubmission().getName())).findAny()
                .orElseThrow();
    }
}