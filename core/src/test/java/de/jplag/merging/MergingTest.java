package de.jplag.merging;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.SharedTokenType;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.Token;
import de.jplag.UiHooks;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.strategy.ComparisonStrategy;
import de.jplag.strategy.ParallelComparisonStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private final ComparisonStrategy comparisonStrategy;
    private final SubmissionSet submissionSet;
    private static final int MINIMUM_NEIGHBOR_LENGTH = 1;
    private static final int MAXIMUM_GAP_SIZE = 10;

    MergingTest() throws ExitException {
        options = getDefaultOptions("merging").withMergingOptions(new MergingOptions(true, MINIMUM_NEIGHBOR_LENGTH, MAXIMUM_GAP_SIZE));

        GreedyStringTiling coreAlgorithm = new GreedyStringTiling(options);
        comparisonStrategy = new ParallelComparisonStrategy(options, coreAlgorithm);

        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        submissionSet = builder.buildSubmissionSet(UiHooks.NullUiHooks);
    }

    @BeforeEach
    void prepareTestState() {
        JPlagResult result = comparisonStrategy.compareSubmissions(submissionSet, UiHooks.NullUiHooks);
        comparisonsBefore = result.getAllComparisons();

        if (options.mergingOptions().enabled()) {
            result = new MatchMerging(options).mergeMatchesOf(result);
        }
        comparisonsAfter = result.getAllComparisons();
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
                if (token.getType().equals(SharedTokenType.FILE_END)) {
                    amountFileEndBefore++;
                }
            }

            for (Token token : tokenRight) {
                if (token.getType().equals(SharedTokenType.FILE_END)) {
                    amountFileEndBefore++;
                }
            }
        }

        int amountFileEndAfter = 0;
        for (JPlagComparison comparison : comparisonsAfter) {
            List<Token> tokenLeft = new ArrayList<>(comparison.firstSubmission().getTokenList());
            List<Token> tokenRight = new ArrayList<>(comparison.secondSubmission().getTokenList());

            for (Token token : tokenLeft) {
                if (token.getType().equals(SharedTokenType.FILE_END)) {
                    amountFileEndAfter++;
                }
            }

            for (Token token : tokenRight) {
                if (token.getType().equals(SharedTokenType.FILE_END)) {
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
}