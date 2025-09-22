package de.jplag.highlightextraction.frequencydetermination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
import de.jplag.highlightextraction.CompleteMatchesStrategy;
import de.jplag.highlightextraction.ContainedMatchesStrategy;
import de.jplag.highlightextraction.FrequencyDetermination;
import de.jplag.highlightextraction.FrequencyStrategy;
import de.jplag.highlightextraction.SubMatchesStrategy;
import de.jplag.highlightextraction.WindowOfMatchesStrategy;
import de.jplag.options.JPlagOptions;

/**
 * Test class to validate the FrequencyStrategies which determine how isFrequencyAnalysisEnabled certain token sequences
 * appear in matches of the comparisons. As the examples use testCode from "PartialPlagiarism" sample-folder and some
 * fictional data.
 */
class StrategyTest extends TestBase {
    private static final StrategyIntegrationTest STRATEGY_INTEGRATION_TEST = new StrategyIntegrationTest();
    private static Submission testSubmission;
    private static Match matchAppearsOnce;
    private static Match matchOccursTwiceInSameComparison;
    private static Match matchOccursTwiceAcrossComparisons;
    private static Match matchOccursThreeTimesAcrossComparisons;
    private static Match matchShort;
    private static final List<Match> MATCHES_APPEARING_ONCE_AND_TWICE = new LinkedList<>();
    private static final List<Match> MATCHES_APPEARING_TWICE_AND_THRICE = new LinkedList<>();
    private static final List<Match> MATCHES_APPEARING_THRICE = new LinkedList<>();
    private static final List<Match> MATCHES_DUPLICATE_AND_THRICE = new LinkedList<>();
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
        matchAppearsOnce = testComparison.matches().get(0);
        matchOccursTwiceInSameComparison = testComparison.matches().get(1);
        matchOccursTwiceAcrossComparisons = testComparison.matches().get(2);
        matchOccursThreeTimesAcrossComparisons = testComparison.matches().get(3);
        matchShort = new Match(testComparison.matches().get(0).startOfFirst(), testComparison.matches().get(0).startOfSecond(), 12, 12);
        ignoredMatches = testComparison.ignoredMatches();
    }

    /**
     * Represents four created submissions with identical code but different names, used to simulate various
     * match-comparison combinations for frequency testing.
     * @param testSubmissionW name of a test submission to Identify the testSubmissions
     * @param testSubmissionX name of a test submission to Identify the testSubmissions
     * @param testSubmissionY name of a test submission to Identify the testSubmissions
     * @param testSubmissionZ name of a test submission to Identify the testSubmissions
     */
    record TestSubmissions(Submission testSubmissionW, Submission testSubmissionX, Submission testSubmissionY, Submission testSubmissionZ) {
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
        Submission testSubmissionY = new Submission("Y", testSubmission.getRoot(), testSubmission.isNew(), testSubmission.getFiles(),
                options.language());
        Submission testSubmissionZ = new Submission("Z", testSubmission.getRoot(), testSubmission.isNew(), testSubmission.getFiles(),
                options.language());

        testSubmissionW.setTokenList(testSubmission.getTokenList());
        testSubmissionX.setTokenList(testSubmission.getTokenList());
        testSubmissionY.setTokenList(testSubmission.getTokenList());
        testSubmissionZ.setTokenList(testSubmission.getTokenList());
        return new TestSubmissions(testSubmissionW, testSubmissionX, testSubmissionY, testSubmissionZ);
    }

    /**
     * Constructs comparisons using predefined matches and test submissions, creating different combinations of
     * Match-Frequencies between Comparisons.
     * @param testSubmissions multiple submissions with the same data but different names for testing
     */
    private void buildTestComparisons(TestSubmissions testSubmissions) {
        MATCHES_APPEARING_ONCE_AND_TWICE.clear();
        MATCHES_APPEARING_TWICE_AND_THRICE.clear();
        MATCHES_APPEARING_THRICE.clear();
        MATCHES_DUPLICATE_AND_THRICE.clear();
        TEST_COMPARISONS.clear();

        MATCHES_APPEARING_ONCE_AND_TWICE.add(matchAppearsOnce);
        MATCHES_APPEARING_ONCE_AND_TWICE.add(matchOccursTwiceAcrossComparisons);
        JPlagComparison comparisonOneAndTwoTimes = new JPlagComparison(testSubmissions.testSubmissionW(), testSubmissions.testSubmissionX(),
                MATCHES_APPEARING_ONCE_AND_TWICE, ignoredMatches);

        MATCHES_APPEARING_TWICE_AND_THRICE.add(matchOccursTwiceAcrossComparisons);
        MATCHES_APPEARING_TWICE_AND_THRICE.add(matchOccursThreeTimesAcrossComparisons);
        JPlagComparison comparisonTwoAndThreeTimes = new JPlagComparison(testSubmissions.testSubmissionX(), testSubmissions.testSubmissionY(),
                MATCHES_APPEARING_TWICE_AND_THRICE, ignoredMatches);

        MATCHES_APPEARING_THRICE.add(matchOccursThreeTimesAcrossComparisons);
        JPlagComparison comparisonThreeTimes = new JPlagComparison(testSubmissions.testSubmissionY(), testSubmissions.testSubmissionZ(),
                MATCHES_APPEARING_THRICE, ignoredMatches);

        MATCHES_DUPLICATE_AND_THRICE.add(matchOccursThreeTimesAcrossComparisons);
        MATCHES_DUPLICATE_AND_THRICE.add(matchOccursTwiceInSameComparison);
        MATCHES_DUPLICATE_AND_THRICE.add(matchOccursTwiceInSameComparison);
        JPlagComparison comparisonDuplicateAndThreeTimes = new JPlagComparison(testSubmissions.testSubmissionZ(), testSubmissions.testSubmissionW(),
                MATCHES_DUPLICATE_AND_THRICE, ignoredMatches);

        TEST_COMPARISONS.add(comparisonOneAndTwoTimes);
        TEST_COMPARISONS.add(comparisonTwoAndThreeTimes);
        TEST_COMPARISONS.add(comparisonThreeTimes);
        TEST_COMPARISONS.add(comparisonDuplicateAndThreeTimes);
    }

    /**
     * Tests whether the Complete Match strategy adds the Match-Frequencies to the Hashmap according to the expected
     * Frequency.
     */
    @Test
    @DisplayName("Test Complete Matches Strategy")
    void testCompleteMatchesStrategy() {
        int strategyNumber = 9;
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(strategy, strategyNumber);
        frequencyDetermination.buildFrequencyMap(TEST_COMPARISONS);
        Map<List<TokenType>, Integer> tokenFrequencyMap = frequencyDetermination.getMatchFrequencyMap();
        STRATEGY_INTEGRATION_TEST.printTestResult(tokenFrequencyMap);

        assertTokenFrequencyAndContainsMatch(matchAppearsOnce, 1, tokenFrequencyMap);
        assertTokenFrequencyAndContainsMatch(matchOccursTwiceInSameComparison, 2, tokenFrequencyMap);
        assertTokenFrequencyAndContainsMatch(matchOccursTwiceAcrossComparisons, 2, tokenFrequencyMap);
        assertTokenFrequencyAndContainsMatch(matchOccursThreeTimesAcrossComparisons, 3, tokenFrequencyMap);
    }

    /**
     * Asserts that a match is contained in the frequency map and that its frequency is as expected.
     * @param match The match to verify
     * @param expectedFrequency How many times the match is expected to appear
     * @param tokenFrequencyMap Map of token sequence hashes for frequency count
     */
    private void assertTokenFrequencyAndContainsMatch(Match match, int expectedFrequency, Map<List<TokenType>, Integer> tokenFrequencyMap) {
        List<TokenType> matchTokenTypes = getMatchTokenTypes(match);
        Integer matchFrequency = tokenFrequencyMap.get(matchTokenTypes);
        if (matchFrequency == null) {
            throw new AssertionError("Match key [" + matchTokenTypes + "] not found in tokenFrequencyMap.");
        }
        assertEquals(expectedFrequency, matchFrequency,
                "Match key [" + matchTokenTypes + "] expected " + expectedFrequency + " times in comparisons, but was " + matchFrequency + " times.");
    }

    /**
     * Creates a list of the TokenTypes from the given Match.
     * @param match for which the TokenType Sequence is wanted.
     * @return A list of TokenTypes representing the matched sequence.
     */
    static List<TokenType> getMatchTokenTypes(Match match) {
        List<Token> tokens = testSubmission.getTokenList().subList(match.startOfFirst(), match.startOfFirst() + match.lengthOfFirst());
        List<TokenType> tokenStrings = new ArrayList<>();
        for (Token token : tokens) {
            tokenStrings.add(token.getType());
        }
        return tokenStrings;
    }

    /**
     * Tests if the Window strategy adds the expected windows and their frequencies to the Hashmap.
     */
    @Test
    @DisplayName("Test check() of window strategy")
    void testWindowOfMatchesStrategy() {
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<List<TokenType>, Integer> windowMap = new HashMap<>();
        int windowSize = 5;
        Consumer<List<TokenType>> addSequenceKey = seq -> windowMap.putIfAbsent(seq, 0);
        Consumer<List<TokenType>> addSequence = seq -> windowMap.put(seq, windowMap.getOrDefault(seq, 0) + 1);

        List<TokenType> matchTokenTypes = getMatchTokenTypes(matchShort);
        strategy.processMatchTokenTypes(matchTokenTypes, addSequenceKey, addSequence, windowSize);

        List<List<TokenType>> expectedKeys = checkIfAllExpectedWindowsAreAdded(matchTokenTypes, windowSize, windowMap);
        assertEquals(expectedKeys.size(), windowMap.size(), "A Key is more than one time used, please check for the rest of the test");
        checkIfAllWindowsAreAddedWithCorrectValue(expectedKeys, windowMap);

        int startIndex = 2;
        List<TokenType> newSequenceForWindows = matchTokenTypes.subList(startIndex, startIndex + windowSize + 1);
        List<TokenType> firstWindow = newSequenceForWindows.subList(0, windowSize);
        List<TokenType> secondWindow = newSequenceForWindows.subList(1, windowSize + 1);

        assertTrue(windowMap.containsKey(firstWindow), "new firstWindow was not added: " + firstWindow);
        assertTrue(windowMap.containsKey(secondWindow), "new secondWindow was not added: " + secondWindow);

        strategy.processMatchTokenTypes(firstWindow, addSequenceKey, addSequence, windowSize);
        strategy.processMatchTokenTypes(secondWindow, addSequenceKey, addSequence, windowSize);

        Integer valueFirstWindow = windowMap.get(firstWindow);
        Integer valueSecondWindow = windowMap.get(secondWindow);

        assertEquals(2, valueFirstWindow, "FirstWindow does not contain two values: " + valueFirstWindow);
        assertEquals(2, valueSecondWindow, "SecondWindow does not contain two values: " + valueSecondWindow);
    }

    private static void checkIfAllWindowsAreAddedWithCorrectValue(List<List<TokenType>> expectedKeys, Map<List<TokenType>, Integer> windowMap) {
        for (List<TokenType> window : expectedKeys) {
            Integer value = windowMap.get(window);
            assertNotNull(value, "value should be at least 1: " + window);
        }
    }

    private static List<List<TokenType>> checkIfAllExpectedWindowsAreAdded(List<TokenType> matchTokenTypes, int windowSize,
            Map<List<TokenType>, Integer> windowMap) {
        List<List<TokenType>> expectedKeys = new ArrayList<>();
        for (int i = 0; i <= matchTokenTypes.size() - windowSize; i++) {
            List<TokenType> expectedKey = matchTokenTypes.subList(i, i + windowSize);
            expectedKeys.add(expectedKey);
        }
        for (List<TokenType> key : expectedKeys) {
            assertTrue(windowMap.containsKey(key), "key missing: " + key);
        }
        return expectedKeys;
    }

    /**
     * Tests if the Submatch strategy adds the expected submatches and their frequencies to the Hashmap.
     */
    @Test
    void testSubmatchesStrategy() {
        int wantedMatchLength = 5;
        SubMatchesStrategy strategy = new SubMatchesStrategy();
        Map<List<TokenType>, Integer> frequencyMap = new HashMap<>();
        List<Token> matchToken = testSubmission.getTokenList().subList(matchShort.startOfFirst(), matchShort.startOfFirst() + wantedMatchLength);
        List<TokenType> matchTokenTypes = matchToken.stream().map(Token::getType).toList();
        int minSubSequenceSize = 3;
        Consumer<List<TokenType>> addSequenceKey = seq -> frequencyMap.putIfAbsent(seq, 0);
        Consumer<List<TokenType>> addSequence = seq -> frequencyMap.put(seq, frequencyMap.getOrDefault(seq, 0) + 1);

        strategy.processMatchTokenTypes(matchTokenTypes, addSequenceKey, addSequence, minSubSequenceSize);

        Set<List<TokenType>> expectedSubSequences = new HashSet<>(List.of(matchTokenTypes, matchTokenTypes.subList(0, 4),
                matchTokenTypes.subList(1, 5), matchTokenTypes.subList(0, 3), matchTokenTypes.subList(1, 4), matchTokenTypes.subList(2, 5)));

        for (List<TokenType> subSequence : expectedSubSequences) {
            assertTrue(frequencyMap.containsKey(subSequence), "Map should contain subSequence: " + subSequence);
            assertTrue(frequencyMap.get(subSequence) > 0, "Value should be min one for subSequence: " + subSequence);
        }

        List<TokenType> newSequence = matchTokenTypes.subList(1, 5);
        strategy.processMatchTokenTypes(newSequence, addSequenceKey, addSequence, minSubSequenceSize);
        Set<List<TokenType>> expectedReUsedKeys = new HashSet<>(
                List.of(matchTokenTypes.subList(1, 5), matchTokenTypes.subList(1, 4), matchTokenTypes.subList(2, 5)));

        for (List<TokenType> key : expectedReUsedKeys) {
            assertEquals(2, frequencyMap.get(key), "Map should contain two keys: " + key);
            expectedSubSequences.remove(key);
        }
        for (List<TokenType> key : expectedSubSequences) {
            assertEquals(1, frequencyMap.get(key), "Map should only contain key: " + key);
        }
    }

    /**
     * Tests if the ContainedMatch strategy adds the expected submatches and the frequencies of the whole matches to the
     * Hashmap.
     */
    @Test
    void testCompleteMatchesIncludedInContainedStrategyForMatchesLongerMin() {
        int strategyNumber = 100;
        FrequencyStrategy strategy = new ContainedMatchesStrategy();
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(strategy, strategyNumber);
        frequencyDetermination.buildFrequencyMap(TEST_COMPARISONS);
        Map<List<TokenType>, Integer> matchFrequencyMap = frequencyDetermination.getMatchFrequencyMap();
        Map<List<TokenType>, Integer> frequencyCount = new HashMap<>();
        for (JPlagComparison comparison : TEST_COMPARISONS) {
            for (Match match : comparison.matches()) {
                List<TokenType> subSequence = getMatchTokenTypes(match);
                frequencyCount.put(subSequence, frequencyCount.getOrDefault(subSequence, 0) + 1);
                if (subSequence.size() >= strategyNumber) {
                    assertTrue(matchFrequencyMap.containsKey(subSequence), "Should contain subSequence: " + subSequence);
                }
            }
        }
        for (Map.Entry<List<TokenType>, Integer> entry : frequencyCount.entrySet()) {
            List<TokenType> key = entry.getKey();
            int frequency = entry.getValue();
            if (key.size() >= strategyNumber) {
                assertEquals(frequency, matchFrequencyMap.get(key), "The frequency is different than the expected frequency: " + frequency);
            }
        }
    }
}
