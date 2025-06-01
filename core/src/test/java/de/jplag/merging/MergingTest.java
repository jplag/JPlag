package de.jplag.merging;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.Token;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ComparisonException;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * This class extends on {@link TestBase} and performs several test on Match Merging, in order to check its
 * functionality. Therefore it uses java programs and feds them into the JPlag pipeline. Results are stored before- and
 * after Match Merging and used for all tests. The samples named "original" and "plag" are from PROGpedia and under the
 * CC BY 4.0 license.
 */
class MergingTest extends TestBase {
    private final JPlagOptions options;
    private List<Match> matches;
    private List<JPlagComparison> comparisonsBefore;
    private List<JPlagComparison> comparisonsAfter;
    private final LongestCommonSubsequenceSearch comparisonStrategy;
    private final SubmissionSet submissionSet;
    private static final int MINIMUM_NEIGHBOR_LENGTH = 1;
    private static final int MAXIMUM_GAP_SIZE = 10;
    private static final int MINIMUM_REQUIRED_MERGES = 0;

    MergingTest() throws ExitException {
        options = getDefaultOptions("merging")
                .withMergingOptions(new MergingOptions(true, MINIMUM_NEIGHBOR_LENGTH, MAXIMUM_GAP_SIZE, MINIMUM_REQUIRED_MERGES));

        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        submissionSet = builder.buildSubmissionSet();

        comparisonStrategy = new LongestCommonSubsequenceSearch(options);
    }

    @BeforeEach
    void prepareTestState() throws ComparisonException {
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
                assertTrue(match.length() >= threshold);
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
            assertTrue(comparisonsAfter.get(i).matches().size() + comparisonsAfter.get(i).ignoredMatches().size() <= comparisonsBefore.get(i)
                    .matches().size() + comparisonsBefore.get(i).ignoredMatches().size());
        }
    }

    @Test
    @DisplayName("Test if amount of token reduced after Match Merging")
    void testFewerToken() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            assertTrue(comparisonsAfter.get(i).firstSubmission().getTokenList().size() <= comparisonsBefore.get(i).firstSubmission().getTokenList()
                    .size()
                    && comparisonsAfter.get(i).secondSubmission().getTokenList().size() <= comparisonsBefore.get(i).secondSubmission().getTokenList()
                            .size());
        }
    }

    @Test
    @DisplayName("Test if amount of FILE_END token stayed the same")
    void testFileEnd() {
        int amountFileEndBefore = 0;
        for (JPlagComparison comparison : comparisonsBefore) {
            List<Token> tokenLeft = new ArrayList<>(comparison.firstSubmission().getTokenList());
            List<Token> tokenRight = new ArrayList<>(comparison.secondSubmission().getTokenList());

            for (Token token : tokenLeft) {
                if (SharedTokenType.FILE_END.equals(token.getType())) {
                    amountFileEndBefore++;
                }
            }

            for (Token token : tokenRight) {
                if (SharedTokenType.FILE_END.equals(token.getType())) {
                    amountFileEndBefore++;
                }
            }
        }

        int amountFileEndAfter = 0;
        for (JPlagComparison comparison : comparisonsAfter) {
            List<Token> tokenLeft = new ArrayList<>(comparison.firstSubmission().getTokenList());
            List<Token> tokenRight = new ArrayList<>(comparison.secondSubmission().getTokenList());

            for (Token token : tokenLeft) {
                if (SharedTokenType.FILE_END.equals(token.getType())) {
                    amountFileEndAfter++;
                }
            }

            for (Token token : tokenRight) {
                if (SharedTokenType.FILE_END.equals(token.getType())) {
                    amountFileEndAfter++;
                }
            }
        }

        assertEquals(amountFileEndBefore, amountFileEndAfter);
    }

    @Test
    @DisplayName("Test if merged matches have counterparts in the original matches")
    void testCorrectMerges() {
        boolean correctMerges = true;
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            matches = comparisonsAfter.get(i).matches();
            List<Match> sortedByFirst = new ArrayList<>(comparisonsBefore.get(i).matches());
            sortedByFirst.addAll(comparisonsBefore.get(i).ignoredMatches());
            sortedByFirst.sort(Comparator.comparingInt(Match::startOfFirst));
            for (Match match : matches) {
                int begin = -1;
                for (int k = 0; k < sortedByFirst.size(); k++) {
                    if (sortedByFirst.get(k).startOfFirst() == match.startOfFirst()) {
                        begin = k;
                        break;
                    }
                }
                if (begin == -1) {
                    correctMerges = false;
                } else {
                    int foundToken = 0;
                    while (foundToken < match.length()) {
                        foundToken += sortedByFirst.get(begin).length();
                        begin++;
                        if (foundToken > match.length()) {
                            correctMerges = false;
                        }
                    }
                }
            }
        }
        assertTrue(correctMerges);
    }

    @Test
    @DisplayName("Sanity check for match merging")
    void testSanity() {

        List<Match> matchesBefore = findComparison(comparisonsBefore, "sanityA.java", "sanityB.java").ignoredMatches();
        List<Match> matchesAfter = findComparison(comparisonsAfter, "sanityA.java", "sanityB.java").matches();

        List<Match> expectedBefore = List.of( //
                new Match(5, 3, 6), //
                new Match(11, 12, 6), //
                new Match(0, 0, 3), //
                new Match(3, 18, 2), //
                new Match(17, 20, 2) //
        );

        List<Match> expectedAfter = List.of(new Match(5, 3, 12));

        assertEquals(expectedBefore, matchesBefore);

        assertEquals(expectedAfter, matchesAfter);
    }

    private static JPlagComparison findComparison(List<JPlagComparison> comparisons, String firstName, String secondName) {
        return comparisons.stream()
                .filter(it -> firstName.equals(it.firstSubmission().getName()) && secondName.equals(it.secondSubmission().getName())).findAny()
                .orElseThrow();
    }
}