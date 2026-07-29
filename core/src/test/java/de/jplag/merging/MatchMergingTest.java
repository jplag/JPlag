package de.jplag.merging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
    private final Submission leftSubmission;
    private final Submission rightSubmission;
    private static final int MINIMUM_NEIGHBOR_LENGTH = 1;
    private static final int MAXIMUM_GAP_SIZE = 10;
    private static final int MINIMUM_REQUIRED_MERGES = 0;

    MatchMergingTest() throws ExitException {
        MergingOptions mergingOptions = new MergingOptions(true, MINIMUM_NEIGHBOR_LENGTH, MAXIMUM_GAP_SIZE, MINIMUM_REQUIRED_MERGES);
        options = getDefaultOptions("merging").withMergingOptions(mergingOptions);

        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        submissionSet = builder.buildSubmissionSet();

        comparisonStrategy = new LongestCommonSubsequenceSearch(options);

        // Largest two single-file submissions provide token lists big enough for hand-built match indices; token content is
        // never inspected.
        List<Submission> singleFileSubmissions = submissionSet.getSubmissions().stream().filter(submission -> submission.getFiles().size() == 1)
                .sorted(Comparator.comparingInt(Submission::getNumberOfTokens).reversed().thenComparing(Submission::getName)).toList();
        leftSubmission = singleFileSubmissions.get(0);
        rightSubmission = singleFileSubmissions.get(1);
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

    @DisplayName("Neighbors merge iff the average gap does not exceed the maximum gap size.")
    @ParameterizedTest(name = "gap of 5, maximumGapSize={0} -> {1} match(es)")
    @CsvSource({"4, 2", "5, 1", "6, 1"})
    void testGapSizeBoundary(int maximumGapSize, int expectedMatchCount) {
        // Two length-3 matches separated by a symmetric gap of 5 tokens.
        List<Match> handBuiltMatches = List.of(new Match(0, 0, 3, 3), new Match(8, 8, 3, 3));
        List<Match> merged = mergeHandBuilt(new MergingOptions(true, 1, maximumGapSize, 0), handBuiltMatches);
        assertEquals(expectedMatchCount, merged.size());
    }

    @Test
    @DisplayName("Matches whose order differs between the two submissions are not neighbors and are not merged.")
    void testReorderedMatchesAreNotMerged() {
        // Matches in opposite order: A then B on the left, B then A on the right.
        Match matchA = new Match(0, 8, 3, 3);
        Match matchB = new Match(4, 0, 3, 3);
        List<Match> merged = mergeHandBuilt(new MergingOptions(true, 1, 10, 0), List.of(matchA, matchB));

        List<Match> sortedResult = merged.stream().sorted(Comparator.comparingInt(Match::startOfFirst)).toList();
        assertIterableEquals(List.of(matchA, matchB), sortedResult, "Reordered matches must remain unmerged");
    }

    @DisplayName("A comparison is only rewritten when the number of merges reaches minimumRequiredMerges.")
    @ParameterizedTest(name = "{0} contiguous matches, minimumRequiredMerges=3 -> {1} match(es)")
    @CsvSource({"4, 1", "3, 3"})
    void testMinimumRequiredMergesBoundary(int chainLength, int expectedMatchCount) {
        // A chain of N contiguous length-2 matches produces N-1 merges. Threshold=3: needs chain of 4 to rewrite.
        List<Match> handBuiltMatches = new ArrayList<>();
        for (int i = 0; i < chainLength; i++) {
            handBuiltMatches.add(new Match(2 * i, 2 * i, 2, 2));
        }
        List<Match> merged = mergeHandBuilt(new MergingOptions(true, 1, 6, 3), handBuiltMatches);
        assertEquals(expectedMatchCount, merged.size());
    }

    @Test
    @DisplayName("An insertion on one side produces an asymmetric merged match that subsumes the gap per side.")
    void testAsymmetricGapMerge() {
        // No gap on the left, four inserted tokens on the right: average gap 2, so the matches merge.
        List<Match> handBuiltMatches = List.of(new Match(0, 0, 3, 3), new Match(3, 7, 3, 3));
        List<Match> merged = mergeHandBuilt(new MergingOptions(true, 1, 6, 0), handBuiltMatches);

        assertEquals(1, merged.size());
        Match mergedMatch = merged.getFirst();
        assertEquals(new Match(0, 0, 6, 10), mergedMatch);
        assertNotEquals(mergedMatch.lengthOfFirst(), mergedMatch.lengthOfSecond(), "The merged match must keep asymmetric side lengths");
    }

    private List<Match> mergeHandBuilt(MergingOptions mergingOptions, List<Match> handBuiltMatches) {
        JPlagOptions unitOptions = options.withMinimumTokenMatch(1).withMergingOptions(mergingOptions);
        JPlagComparison comparison = new JPlagComparison(leftSubmission, rightSubmission, new ArrayList<>(handBuiltMatches), new ArrayList<>());
        JPlagResult result = new JPlagResult(List.of(comparison), submissionSet, 0L, unitOptions);
        return new MatchMerging(unitOptions).mergeMatchesOf(result).getAllComparisons().getFirst().matches();
    }

    private static JPlagComparison findComparison(List<JPlagComparison> comparisons, String firstName, String secondName) {
        return comparisons.stream()
                .filter(it -> firstName.equals(it.firstSubmission().getName()) && secondName.equals(it.secondSubmission().getName())) //
                .findAny().orElseThrow();
    }
}