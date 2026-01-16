package de.jplag.highlightextraction.frequencysimilarity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.highlightextraction.MatchFrequency;
import de.jplag.highlightextraction.MatchFrequencyWeighting;
import de.jplag.highlightextraction.TokenSequenceUtil;
import de.jplag.highlightextraction.weighting.LinearWeighting;
import de.jplag.highlightextraction.weighting.ProportionalWeighting;
import de.jplag.highlightextraction.weighting.QuadraticWeighting;
import de.jplag.highlightextraction.weighting.SigmoidWeighting;
import de.jplag.options.JPlagOptions;

/**
 * Tests the Frequency Weighting class with different parameter combinations.
 */
class FrequencyWeightingTest extends TestBase {
    private static final double ORIGINAL_SIMILARITY = 0.31157;
    private static Submission testSubmission;
    private static Match match;
    private static Match matchShort;
    private static final List<Match> TEST_MATCHES = new LinkedList<>();
    private static List<Match> ignoredMatches = new LinkedList<>();
    private static JPlagComparison comparison;

    /**
     * Creates Test data to validate different match-frequency combinations.
     * @throws ExitException if getJPlagResult fails to create the comparison result.
     */
    @BeforeEach
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        JPlagResult result = getJPlagResult(options);
        JPlagComparison testComparison = result.getAllComparisons().getFirst();
        buildTestMatches(testComparison);
        buildTestComparisons(getTestSubmissions(options));

    }

    /**
     * Creates Test data by running JPlag Methods to get JPlag result for building test data.
     * @param options JPlag options used in this test
     * @return JPlag result for test input
     * @throws ExitException submission set builder can throw this exception
     */
    private JPlagResult getJPlagResult(JPlagOptions options) throws ExitException {
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch subsequenceSearch = new LongestCommonSubsequenceSearch(options);
        return subsequenceSearch.compareSubmissions(submissionSet);
    }

    /**
     * Gets sample matches from the given test comparison to use in test cases. These matches will be used to create
     * different combinations of Match-Frequency.
     * @param testComparison first Comparison from the Test classes here used to get test matches.
     */
    private static void buildTestMatches(JPlagComparison testComparison) {
        testSubmission = testComparison.firstSubmission();
        match = testComparison.matches().getFirst();
        matchShort = new Match(testComparison.matches().getFirst().startOfFirst(), testComparison.matches().getFirst().startOfSecond(), 10, 10);
        ignoredMatches = testComparison.ignoredMatches();
    }

    /**
     * @param options the JPlag options for the test, to for the language
     * @return multiple submissions with the same data but different names for testing
     */
    private static TestSubmissions getTestSubmissions(JPlagOptions options) {
        Submission testSubmissionW = new Submission("W", testSubmission.getRoot(), testSubmission.isNew(), testSubmission.getFiles(),
                options.language());
        Submission testSubmissionX = new Submission("X", testSubmission.getRoot(), testSubmission.isNew(), testSubmission.getFiles(),
                options.language());

        testSubmissionW.setTokenList(testSubmission.getTokenList());
        testSubmissionX.setTokenList(testSubmission.getTokenList());
        return new TestSubmissions(testSubmissionX, testSubmissionW);
    }

    /**
     * Represents four created submissions with identical code but different names, used to simulate various
     * match-comparison combinations for frequency testing.
     * @param testSubmissionW name of a test submission to Identify the testSubmissions
     * @param testSubmissionX name of a test submission to Identify the testSubmissions
     */
    record TestSubmissions(Submission testSubmissionW, Submission testSubmissionX) {
    }

    /**
     * Constructs comparisons using predefined matches and test submissions, creating different combinations of
     * Match-Frequencies between Comparisons.
     * @param testSubmissions multiple submissions with the same data but different names for testing
     */
    private void buildTestComparisons(TestSubmissions testSubmissions) {
        TEST_MATCHES.clear();

        TEST_MATCHES.add(match);
        comparison = new JPlagComparison(testSubmissions.testSubmissionW(), testSubmissions.testSubmissionX(), TEST_MATCHES, ignoredMatches);
    }

    /**
     * Tests the different weighting functions with different weights.
     */
    @Test
    @DisplayName("Test the weighting functions")
    void testWeightingFunction() {
        MatchFrequency matchFrequency = new MatchFrequency();
        Match match2 = TEST_MATCHES.getFirst();
        matchFrequency.put(TokenSequenceUtil.tokenTypesFor(comparison, match2), 5.0);
        matchFrequency.put(TokenSequenceUtil.tokenTypesFor(comparison, matchShort), 1.0);

        List<JPlagComparison> comparisons = List.of(comparison);
        MatchFrequencyWeighting proportionalWeighting = new MatchFrequencyWeighting(comparisons, new ProportionalWeighting(), matchFrequency);
        MatchFrequencyWeighting linearWeighting = new MatchFrequencyWeighting(comparisons, new LinearWeighting(), matchFrequency);
        MatchFrequencyWeighting quadraticWeighting = new MatchFrequencyWeighting(comparisons, new QuadraticWeighting(), matchFrequency);
        MatchFrequencyWeighting sigmoidWeighting = new MatchFrequencyWeighting(comparisons, new SigmoidWeighting(), matchFrequency);

        double proportionalMatchLength = proportionalWeighting.getWeightedMatchLength(comparison, 1, true, new ProportionalWeighting());
        double linearMatchLength = linearWeighting.getWeightedMatchLength(comparison, 1, true, new LinearWeighting());
        double quadraticMatchLength = quadraticWeighting.getWeightedMatchLength(comparison, 1, true, new QuadraticWeighting());
        double sigmoidMatchLength = sigmoidWeighting.getWeightedMatchLength(comparison, 1, true, new SigmoidWeighting());

        // weight 0: weight down
        assertEquals(0, proportionalMatchLength, 0.0001);
        // weight 1: leave unchanged
        assertEquals(315, linearMatchLength, 0.0001);
        assertEquals(315, quadraticMatchLength, 0.0001);
        assertEquals(315, sigmoidMatchLength, 0.0001);

        proportionalMatchLength = proportionalWeighting.frequencySimilarity(comparison, 0);
        linearMatchLength = linearWeighting.frequencySimilarity(comparison, 0);
        quadraticMatchLength = quadraticWeighting.frequencySimilarity(comparison, 0);
        sigmoidMatchLength = sigmoidWeighting.frequencySimilarity(comparison, 0);

        // factor = 0 -> unchanged similarity
        assertEquals(ORIGINAL_SIMILARITY, proportionalMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY, linearMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY, quadraticMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY, sigmoidMatchLength, 0.0001);

        proportionalMatchLength = proportionalWeighting.frequencySimilarity(comparison, 1);
        linearMatchLength = linearWeighting.frequencySimilarity(comparison, 1);
        quadraticMatchLength = quadraticWeighting.frequencySimilarity(comparison, 1);
        sigmoidMatchLength = sigmoidWeighting.frequencySimilarity(comparison, 1);

        assertEquals(0, proportionalMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY, linearMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY, quadraticMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY, sigmoidMatchLength, 0.0001);

        proportionalMatchLength = proportionalWeighting.frequencySimilarity(comparison, 0.5);
        linearMatchLength = linearWeighting.frequencySimilarity(comparison, 0.5);
        quadraticMatchLength = quadraticWeighting.frequencySimilarity(comparison, 0.5);
        sigmoidMatchLength = sigmoidWeighting.frequencySimilarity(comparison, 0.5);

        assertEquals(ORIGINAL_SIMILARITY, linearMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY / 2, proportionalMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY, quadraticMatchLength, 0.0001);
        assertEquals(ORIGINAL_SIMILARITY, sigmoidMatchLength, 0.0001);
    }
}
