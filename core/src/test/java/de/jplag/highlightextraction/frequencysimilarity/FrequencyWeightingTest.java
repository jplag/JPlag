package de.jplag.highlightextraction.frequencysimilarity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.frequency.FrequencyUtil;
import de.jplag.frequency.MatchFrequencyWeighting;
import de.jplag.frequency.MatchFrequencyWeightingFunction;
import de.jplag.options.JPlagOptions;

/**
 * Tests the Frequency Weighting class with different parameter combinations.
 */
class FrequencyWeightingTest extends TestBase {
    private static Submission testSubmission;
    private static Match match;
    private static Match matchShort;
    private static final List<Match> TEST_MATCHES = new LinkedList<>();
    private static List<Match> ignoredMatches = new LinkedList<>();
    private static final List<JPlagComparison> TEST_COMPARISONS = new LinkedList<>();

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
        TEST_COMPARISONS.clear();
        TEST_MATCHES.add(match);
        JPlagComparison testComparison = new JPlagComparison(testSubmissions.testSubmissionW(), testSubmissions.testSubmissionX(), TEST_MATCHES,
                ignoredMatches);
        TEST_COMPARISONS.add(testComparison);
    }

    /**
     * Tests the different weighting functions with different weights.
     */
    @Test
    @DisplayName("Test the weighting functions")
    void testWeightingFunction() {
        Map<List<TokenType>, Double> matchFrequency = new HashMap<>();
        List<TokenType> testSubmissionTokenTypes = testSubmission.getTokenList().stream().map(Token::getType).toList();
        matchFrequency.put(FrequencyUtil.matchesToMatchTokenTypes(TEST_MATCHES.getFirst(), testSubmissionTokenTypes), 5.0);
        matchFrequency.put(FrequencyUtil.matchesToMatchTokenTypes(matchShort, testSubmissionTokenTypes), 1.0);

        MatchFrequencyWeighting matchFrequencyWeightingLinear = new MatchFrequencyWeighting(TEST_COMPARISONS, MatchFrequencyWeightingFunction.LINEAR,
                matchFrequency);
        MatchFrequencyWeighting matchFrequencyWeightingProportional = new MatchFrequencyWeighting(TEST_COMPARISONS,
                MatchFrequencyWeightingFunction.PROPORTIONAL, matchFrequency);
        MatchFrequencyWeighting matchFrequencyWeightingQuadratic = new MatchFrequencyWeighting(TEST_COMPARISONS,
                MatchFrequencyWeightingFunction.QUADRATIC, matchFrequency);
        MatchFrequencyWeighting matchFrequencyWeightingSigmoid = new MatchFrequencyWeighting(TEST_COMPARISONS,
                MatchFrequencyWeightingFunction.SIGMOID, matchFrequency);

        double linearWeightedMatchLength = matchFrequencyWeightingLinear.getWeightedMatchLength(TEST_COMPARISONS.getFirst(), 1, true,
                MatchFrequencyWeightingFunction.LINEAR);
        double proportionalWeightedMatchLength = matchFrequencyWeightingProportional.getWeightedMatchLength(TEST_COMPARISONS.getFirst(), 1, true,
                MatchFrequencyWeightingFunction.PROPORTIONAL);
        double quadraticWeightedMatchLength = matchFrequencyWeightingQuadratic.getWeightedMatchLength(TEST_COMPARISONS.getFirst(), 1, true,
                MatchFrequencyWeightingFunction.QUADRATIC);
        double sigmoidWeightedMatchLength = matchFrequencyWeightingSigmoid.getWeightedMatchLength(TEST_COMPARISONS.getFirst(), 1, true,
                MatchFrequencyWeightingFunction.SIGMOID);

        assertEquals(315, linearWeightedMatchLength, 0.0001);
        assertEquals(3, proportionalWeightedMatchLength, 0.0001);
        assertEquals(315, quadraticWeightedMatchLength, 0.0001);
        assertEquals(317, sigmoidWeightedMatchLength, 0.0001);

        Map<List<TokenType>, Double> matchFrequency1 = new HashMap<>();
        matchFrequency1.put(FrequencyUtil.matchesToMatchTokenTypes(TEST_MATCHES.getFirst(), testSubmissionTokenTypes), 5.0);

        MatchFrequencyWeighting matchFrequencyWeightingLinear1 = new MatchFrequencyWeighting(TEST_COMPARISONS, MatchFrequencyWeightingFunction.LINEAR,
                matchFrequency);
        MatchFrequencyWeighting matchFrequencyWeightingProportional1 = new MatchFrequencyWeighting(TEST_COMPARISONS,
                MatchFrequencyWeightingFunction.PROPORTIONAL, matchFrequency);
        MatchFrequencyWeighting matchFrequencyWeightingQuadratic1 = new MatchFrequencyWeighting(TEST_COMPARISONS,
                MatchFrequencyWeightingFunction.QUADRATIC, matchFrequency);
        MatchFrequencyWeighting matchFrequencyWeightingSigmoid1 = new MatchFrequencyWeighting(TEST_COMPARISONS,
                MatchFrequencyWeightingFunction.SIGMOID, matchFrequency);

        linearWeightedMatchLength = matchFrequencyWeightingLinear1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 0);
        proportionalWeightedMatchLength = matchFrequencyWeightingProportional1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 0);
        quadraticWeightedMatchLength = matchFrequencyWeightingQuadratic1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 0);
        sigmoidWeightedMatchLength = matchFrequencyWeightingSigmoid1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 0);

        assertEquals(0.31157, linearWeightedMatchLength, 0.0001);
        assertEquals(0.31157, proportionalWeightedMatchLength, 0.0001);
        assertEquals(0.31157, quadraticWeightedMatchLength, 0.0001);
        assertEquals(0.31157, sigmoidWeightedMatchLength, 0.0001);

        linearWeightedMatchLength = matchFrequencyWeightingLinear1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 1);
        proportionalWeightedMatchLength = matchFrequencyWeightingProportional1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 1);
        quadraticWeightedMatchLength = matchFrequencyWeightingQuadratic1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 1);
        sigmoidWeightedMatchLength = matchFrequencyWeightingSigmoid1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 1);

        assertEquals(0.31157, linearWeightedMatchLength, 0.0001);
        assertEquals(0.00296, proportionalWeightedMatchLength, 0.0001);
        assertEquals(0.31157, quadraticWeightedMatchLength, 0.0001);
        assertEquals(0.31355, sigmoidWeightedMatchLength, 0.0001);

        linearWeightedMatchLength = matchFrequencyWeightingLinear1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 0.5);
        proportionalWeightedMatchLength = matchFrequencyWeightingProportional1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 0.5);
        quadraticWeightedMatchLength = matchFrequencyWeightingQuadratic1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 0.5);
        sigmoidWeightedMatchLength = matchFrequencyWeightingSigmoid1.frequencySimilarity(TEST_COMPARISONS.getFirst(), 0.5);

        assertEquals(0.31157, linearWeightedMatchLength, 0.0001);
        assertEquals(0.15727, proportionalWeightedMatchLength, 0.0001);
        assertEquals(0.31157, quadraticWeightedMatchLength, 0.0001);
        assertEquals(0.31256, sigmoidWeightedMatchLength, 0.0001);
    }
}
