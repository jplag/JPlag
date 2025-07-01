package de.jplag.highlightextraction.frequencydetermination;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.*;
import de.jplag.Match;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.highlightextraction.*;
import de.jplag.options.JPlagOptions;

/**
 * Test class to validate the FrequencyStrategies which determine how frequency certain token sequences appear in
 * matches of the comparisons. As the examples use testCode from "PartialPlagiarism" sample-folder and some fictional
 * data.
 */
class StrategyTest extends TestBase {
    private static final StrategyIntegrationTest strategyIntegrationTest = new StrategyIntegrationTest();
    private static Submission testSubmission;
    private static Match matchAppearsOnce;
    private static Match matchOccursTwiceInSameComparison;
    private static Match matchOccursTwiceAcrossComparisons;
    private static Match matchOccursThreeTimesAcrossComparisons;
    private static Match matchShort;
    private static final List<Match> matchesAppearingOnceAndTwice = new LinkedList<>();
    private static final List<Match> matchesAppearingTwiceAndThrice = new LinkedList<>();
    private static final List<Match> matchesAppearingThrice = new LinkedList<>();
    private static final List<Match> matchesDuplicateAndThrice = new LinkedList<>();
    private static List<Match> ignoredMatches = new LinkedList<>();
    private static final List<JPlagComparison> testComparisons = new LinkedList<>();

    /**
     * Creates Test data to test different Match-Frequency Combinations in created combinations
     * @throws ExitException getJPlagResult may throw such an Exception
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
     * Creates Test data by running JPlag Methods to get JPlag result, to create objects used to build test data.
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
     * Gets sample matches from the given test comparison to use in test scenarios. These matches will be used to create
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
     * @param testSubmissionW name of a new Submission to Identify the testSubmissions
     * @param testSubmissionX name of a new Submission to Identify the testSubmissions
     * @param testSubmissionY name of a new Submission to Identify the testSubmissions
     * @param testSubmissionZ name of a new Submission to Identify the testSubmissions
     */
    private record TestSubmissions(Submission testSubmissionW, Submission testSubmissionX, Submission testSubmissionY, Submission testSubmissionZ) {
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
     * Match-Frequency's between Comparisons.
     * @param testSubmissions multiple submissions with the same data but different names for testing
     */
    private void buildTestComparisons(TestSubmissions testSubmissions) {
        matchesAppearingOnceAndTwice.clear();
        matchesAppearingTwiceAndThrice.clear();
        matchesAppearingThrice.clear();
        matchesDuplicateAndThrice.clear();
        testComparisons.clear();

        matchesAppearingOnceAndTwice.add(matchAppearsOnce);
        matchesAppearingOnceAndTwice.add(matchOccursTwiceAcrossComparisons);
        JPlagComparison comparisonOneAndTwoTimes = new JPlagComparison(testSubmissions.testSubmissionW(), testSubmissions.testSubmissionX(),
                matchesAppearingOnceAndTwice, ignoredMatches);

        matchesAppearingTwiceAndThrice.add(matchOccursTwiceAcrossComparisons);
        matchesAppearingTwiceAndThrice.add(matchOccursThreeTimesAcrossComparisons);
        JPlagComparison comparisonTwoAndThreeTimes = new JPlagComparison(testSubmissions.testSubmissionX(), testSubmissions.testSubmissionY(),
                matchesAppearingTwiceAndThrice, ignoredMatches);

        matchesAppearingThrice.add(matchOccursThreeTimesAcrossComparisons);
        JPlagComparison comparisonThreeTimes = new JPlagComparison(testSubmissions.testSubmissionY(), testSubmissions.testSubmissionZ(),
                matchesAppearingThrice, ignoredMatches);

        matchesDuplicateAndThrice.add(matchOccursThreeTimesAcrossComparisons);
        matchesDuplicateAndThrice.add(matchOccursTwiceInSameComparison);
        matchesDuplicateAndThrice.add(matchOccursTwiceInSameComparison);
        JPlagComparison comparisonDuplicateAndThreeTimes = new JPlagComparison(testSubmissions.testSubmissionZ(), testSubmissions.testSubmissionW(),
                matchesDuplicateAndThrice, ignoredMatches);

        testComparisons.add(comparisonOneAndTwoTimes);
        testComparisons.add(comparisonTwoAndThreeTimes);
        testComparisons.add(comparisonThreeTimes);
        testComparisons.add(comparisonDuplicateAndThreeTimes);
    }

    /**
     * Tests if the Complete Match strategy adds the Match-Frequency's to the Hashmap according to the expected Frequency.
     */
    @Test
    @DisplayName("Test Complete Matches Strategy")
    void testCompleteMatchesStrategy() {
        int strategyNumber = 9;
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(strategy, strategyNumber);
        frequencyDetermination.buildFrequencyMap(testComparisons);
        Map<Integer, Integer> tokenFrequencyMap = frequencyDetermination.getMatchFrequencyMap();
        strategyIntegrationTest.printTestResult(tokenFrequencyMap);

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
    private void assertTokenFrequencyAndContainsMatch(Match match, int expectedFrequency, Map<Integer, Integer> tokenFrequencyMap) {
        List<TokenType> matchTokenTypes = getMatchTokenTypes(match);
        Integer matchFrequency = tokenFrequencyMap.get(matchTokenTypes.hashCode());
        if (matchFrequency == null) {
            throw new AssertionError("Match key [" + matchTokenTypes + "] not found in tokenFrequencyMap.");
        }
        assertEquals(expectedFrequency, matchFrequency,
                "Match key [" + matchTokenTypes + "] expected " + expectedFrequency + " times in comparisons, but was " + matchFrequency + " times.");
    }

    /**
     * Creates a list of the TokenTypes from the Match.
     * @param match for which the TokenType Sequence is wanted.
     * @return A list of TokenTypes representing the matched sequence.
     */
    private static List<TokenType> getMatchTokenTypes(Match match) {
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
        Map<Integer, Integer> windowMap = new HashMap<>();
        int windowSize = 5;

        List<TokenType> matchTokenTypes = getMatchTokenTypes(matchShort);
        strategy.addMatchToFrequencyMap(matchTokenTypes, windowMap, windowSize);

        // all Keys there?
        List<List<TokenType>> expectedKeys = new ArrayList<>();
        for (int i = 0; i <= matchTokenTypes.size() - windowSize; i++) {
            List<TokenType> expectedKey = matchTokenTypes.subList(i, i + windowSize);
            expectedKeys.add(expectedKey);
        }
        for (List<TokenType> key : expectedKeys) {
            assertTrue(windowMap.containsKey(key.hashCode()), "key missing: " + key);
        }

        // every key added once
        assertEquals(expectedKeys.size(), windowMap.size(), "A Key is more than one time used, please check for the rest of the test");

        // every window is added, and example value is correct
        for (List<TokenType> window : expectedKeys) {
            Integer value = windowMap.get(window.hashCode());
            assertNotNull(value, "value should be min 1: " + window);
        }

        // add new entries
        int startIndex = 2;
        List<TokenType> newSequenceForWindows = matchTokenTypes.subList(startIndex, startIndex + windowSize + 1);
        List<TokenType> firstWindow = newSequenceForWindows.subList(0, windowSize);
        List<TokenType> secondWindow = newSequenceForWindows.subList(1, windowSize + 1);

        assertTrue(windowMap.containsKey(firstWindow.hashCode()), "new firstWindow was not added: " + firstWindow);
        assertTrue(windowMap.containsKey(secondWindow.hashCode()), "new secondWindow was not added: " + secondWindow);

        strategy.addMatchToFrequencyMap(firstWindow, windowMap, windowSize);
        strategy.addMatchToFrequencyMap(secondWindow, windowMap, windowSize);

        Integer valueFirstWindow = windowMap.get(firstWindow.hashCode());
        Integer valueSecondWindow = windowMap.get(secondWindow.hashCode());

        assertEquals(2, valueFirstWindow, "FirstWindow does not contain two values: " + valueFirstWindow);
        assertEquals(2, valueSecondWindow, "SecondWindow does not contain two values: " + valueSecondWindow);
    }

    /**
     * Tests if the Submatch strategy adds the expected submatches and their frequencies to the Hashmap.
     */
    @Test
    void testSubmatchesStrategy() {
        int wantedMatchLength = 5;
        // Create
        SubMatchesStrategy strategy = new SubMatchesStrategy();
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        List<Token> matchToken = testSubmission.getTokenList().subList(matchShort.startOfFirst(), matchShort.startOfFirst() + wantedMatchLength);
        List<TokenType> matchTokenTypes = matchToken.stream().map(Token::getType).toList();
        int minSubSequenceSize = 3;

        strategy.addMatchToFrequencyMap(matchTokenTypes, frequencyMap, minSubSequenceSize);

        Set<List<TokenType>> expectedSubSequences = new HashSet<>(List.of(matchTokenTypes, matchTokenTypes.subList(0, 4),
                matchTokenTypes.subList(1, 5), matchTokenTypes.subList(0, 3), matchTokenTypes.subList(1, 4), matchTokenTypes.subList(2, 5)));

        for (List<TokenType> subSequence : expectedSubSequences) {
            assertTrue(frequencyMap.containsKey(subSequence.hashCode()), "Map should contain subSequence: " + subSequence);
            assertTrue(frequencyMap.get(subSequence.hashCode()) > 0, "Value should be min one for subSequence: " + subSequence);
        }

        List<TokenType> newSequence = matchTokenTypes.subList(1, 5);
        strategy.addMatchToFrequencyMap(newSequence, frequencyMap, minSubSequenceSize);
        Set<List<TokenType>> expectedReUsedKeys = new HashSet<>(
                List.of(matchTokenTypes.subList(1, 5), matchTokenTypes.subList(1, 4), matchTokenTypes.subList(2, 5)));

        for (List<TokenType> key : expectedReUsedKeys) {
            assertEquals(2, frequencyMap.get(key.hashCode()), "Map should contain two keys: " + key);
            expectedSubSequences.remove(key);
        }
        for (List<TokenType> key : expectedSubSequences) {
            assertEquals(1, frequencyMap.get(key.hashCode()), "Map should only contain key: " + key);
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
        frequencyDetermination.buildFrequencyMap(testComparisons);
        Map<Integer, Integer> matchFrequencyMap = frequencyDetermination.getMatchFrequencyMap();
        Map<List<TokenType>, Integer> frequencyCount = new HashMap<>();
        for (JPlagComparison comparison : testComparisons) {
            for (Match match : comparison.matches()) {
                List<TokenType> subSequence = getMatchTokenTypes(match);
                frequencyCount.put(subSequence, frequencyCount.getOrDefault(subSequence, 0) + 1);
                if (subSequence.size() >= strategyNumber) {
                    assertTrue(matchFrequencyMap.containsKey(subSequence.hashCode()), "Should contain subSequence: " + subSequence);
                }
            }
        }
        for (Map.Entry<List<TokenType>, Integer> entry : frequencyCount.entrySet()) {
            List<TokenType> key = entry.getKey();
            int frequency = entry.getValue();
            if (key.size() >= strategyNumber) {
                assertEquals(frequency, matchFrequencyMap.get(key.hashCode()),
                        "The frequency is different than the expected frequency: " + frequency);
            }
        }
    }
}
