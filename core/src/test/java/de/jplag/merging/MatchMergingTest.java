package de.jplag.merging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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
import de.jplag.Token;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ComparisonException;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * Tests for the subsequence match merging mechanism implemented in {@link MatchMerging}.
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
        MergingOptions mergingOptions = new MergingOptions(true, MINIMUM_NEIGHBOR_LENGTH, MAXIMUM_GAP_SIZE, MINIMUM_REQUIRED_MERGES);
        options = getDefaultOptions("merging").withMergingOptions(mergingOptions);

        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        submissionSet = builder.buildSubmissionSet();

        comparisonStrategy = new LongestCommonSubsequenceSearch(options);
    }

    @BeforeEach
    void prepareTestState() throws ComparisonException {
        JPlagResult result = comparisonStrategy.compareSubmissions(submissionSet);
        comparisonsBefore = new ArrayList<>(result.getAllComparisons());

        result = new MatchMerging(options).mergeMatchesOf(result);
        comparisonsAfter = new ArrayList<>(result.getAllComparisons());

        comparisonsBefore.sort(Comparator.comparing(Object::toString));
        comparisonsAfter.sort(Comparator.comparing(Object::toString));
    }

    @Test
    @DisplayName("Test if merged matches exceed the minimum token match threshold.")
    void testBufferRemoval() {
        checkMatchLength(JPlagComparison::matches, options.minimumTokenMatch(), comparisonsAfter);
    }

    @Test
    @DisplayName("Test if original matches exceed the minimum token match threshold.")
    void testGSTMatches() {
        checkMatchLength(JPlagComparison::matches, options.minimumTokenMatch(), comparisonsBefore);
    }

    @Test
    @DisplayName("Test if ignored matches exceed the minimum neighbor length threshold.")
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
    @DisplayName("Test if the similarity values increase or stay the same.")
    void testSimilarityIncreased() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            assertTrue(comparisonsAfter.get(i).similarity() >= comparisonsBefore.get(i).similarity());
        }
    }

    @Test
    @DisplayName("Test if the number of matches decreases or stays the same.")
    void testFewerMatches() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            int totalMatchesAfter = comparisonsAfter.get(i).matches().size() + comparisonsAfter.get(i).ignoredMatches().size();
            int totalMatchesBefore = comparisonsBefore.get(i).matches().size() + comparisonsBefore.get(i).ignoredMatches().size();

            assertTrue(totalMatchesAfter <= totalMatchesBefore,
                    "Expected total matches after to be less than or equal to before, but got " + totalMatchesAfter + " > " + totalMatchesBefore);
        }
    }

    @Test
    @DisplayName("Test if the number of matches tokens increases.")
    void testMoreToken() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            int tokensBeforeFirst = comparisonsBefore.get(i).firstSubmission().getNumberOfTokens();
            int tokensBeforeSecond = comparisonsBefore.get(i).secondSubmission().getNumberOfTokens();

            int tokensAfterFirst = comparisonsAfter.get(i).firstSubmission().getNumberOfTokens();
            int tokensAfterSecond = comparisonsAfter.get(i).secondSubmission().getNumberOfTokens();

            assertTrue(tokensAfterFirst >= tokensBeforeFirst);
            assertTrue(tokensAfterSecond >= tokensBeforeSecond);
        }
    }

    @Test
    @DisplayName("Test if number of FILE_END tokens stays the same.")
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
    @DisplayName("Test merging five matches into one.")
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

    @Test
    @DisplayName("Test minimal requires merges with default parameters.")
    void testMinimalRequiredMerges() throws ExitException {
        JPlagResult result = runJPlag("merging", it -> it.withMergingOptions(new MergingOptions().withEnabled(true)));
        List<Integer> matchedTokens = result.getAllComparisons().stream().map(JPlagComparison::getNumberOfMatchedTokens).toList();
        List<Double> similarities = result.getAllComparisons().stream().map(JPlagComparison::similarity).toList();

        // Test matched tokens:
        List<Integer> expectedMatchedTokens = List.of(26, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertIterableEquals(expectedMatchedTokens, matchedTokens);

        // Test similarity values:
        List<Double> expectedSimilarities = List.of(0.8966, 0.5865, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        for (int i = 0; i < expectedSimilarities.size(); i++) {
            assertEquals(expectedSimilarities.get(i), similarities.get(i), DELTA, "Mismatch at index " + i);
        }
    }

    @Test
    @DisplayName("Test for the absence of cross file matches.")
    void testFileBoundaries() throws ExitException {
        MergingOptions mergingOptions = new MergingOptions(true, MINIMUM_NEIGHBOR_LENGTH, MAXIMUM_GAP_SIZE, MINIMUM_REQUIRED_MERGES);
        JPlagOptions customOptions = getDefaultOptions("crossFile").withMergingOptions(mergingOptions);
        SubmissionSetBuilder builder = new SubmissionSetBuilder(customOptions);
        SubmissionSet submissions = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch search = new LongestCommonSubsequenceSearch(customOptions);
        JPlagResult result = search.compareSubmissions(submissions);

        assumeEquals(2, result.getNumberOfSubmissions());
        assumeEquals(1, result.getAllComparisons().size());
        JPlagComparison comparison = result.getAllComparisons().getFirst();
        assumeEquals(2, comparison.matches().size());

        checkForCrossFileMatches(comparison, comparison.matches());

        JPlagResult mergedResult = new MatchMerging(customOptions).mergeMatchesOf(result);
        JPlagComparison mergedComparison = mergedResult.getAllComparisons().getFirst();

        assertEquals(2, mergedResult.getNumberOfSubmissions());
        assertEquals(1, mergedResult.getAllComparisons().size());
        assertEquals(2, mergedComparison.matches().size());

        checkForCrossFileMatches(mergedComparison, mergedComparison.matches());
    }

    private void checkForCrossFileMatches(JPlagComparison comparison, List<Match> matches) {
        for (Match match : matches) {
            List<Token> leftTokens = comparison.firstSubmission().getTokenList().subList(match.startOfFirst(),
                    match.startOfFirst() + match.lengthOfFirst());
            List<Token> rightTokens = comparison.secondSubmission().getTokenList().subList(match.startOfSecond(),
                    match.startOfSecond() + match.lengthOfSecond());
            verifyTokensFromSingleFile(leftTokens);
            verifyTokensFromSingleFile(rightTokens);
        }

    }

    private void verifyTokensFromSingleFile(List<Token> tokens) {
        List<File> files = tokens.stream().map(Token::getFile).toList();
        for (File file : files) {
            assertEquals(files.getFirst(), file, "Two different files in token sequence: " + files.getFirst().getName() + " and " + file.getName());
        }
    }

    private static JPlagComparison findComparison(List<JPlagComparison> comparisons, String firstName, String secondName) {
        return comparisons.stream()
                .filter(it -> firstName.equals(it.firstSubmission().getName()) && secondName.equals(it.secondSubmission().getName())) //
                .findAny().orElseThrow();
    }
}