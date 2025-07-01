package de.jplag.highlightextraction.frequencydetermination;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(StrategyTest.class);
    private static final StrategyIntegrationTest strategyIntegrationTest = new StrategyIntegrationTest();
    private static Submission testSubmission;
    private static Match matchAppearsOnce;
    private static Match matchOccursTwiceInSameComparison;
    private static Match matchOccursTwiceAcrossComparisons;
    private static Match matchOccursThreeTimesAcrossComparisons;
    private static Match matchShort;
    List<Match> matchesAppearingOnceAndTwice = new LinkedList<>();
    List<Match> matchesAppearingTwiceAndThrice = new LinkedList<>();
    List<Match> matchesAppearingThrice = new LinkedList<>();
    List<Match> matchesDuplicateAndThrice = new LinkedList<>();
    static List<Match> ignoredMatches = new LinkedList<>();
    List<JPlagComparison> testComparisons = new LinkedList<>();

    /**
     * Creates Tets data to test different Match-Frequency Combinations in created combinations
     * @throws ExitException getJPlagResult can throw such an Exception
     */
    @BeforeEach
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        JPlagResult result = getJPlagResult(options);

        JPlagComparison testComparison = result.getAllComparisons().getFirst();
        buildTestMatches(testComparison);
        buildTestComparisons(getTestSubmissions(options));

        logger.info("Comparisons: {}", testComparisons);
    }

    /**
     * Creates Test data by running JPlag Methods to get JPlag result, to create Objects that will be used for the Build of
     * test data.
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
     * @param options the JPlag options for the test, to get the language
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
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(strategy, 9);
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
     * Tests if the Window strategy adds the expected windows to the Hashmap.
     */
    @Test
    @DisplayName("Test WindowOfMatchesStrategy Create")
    void testWindowOfMatchesStrategyAddingTheExpectedWindows() {
        int windowSize = 10;
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        Match match = matchAppearsOnce;
        List<TokenType> matchTokenTypes = getMatchTokenTypes(match);

        if (matchTokenTypes.size() > windowSize + 3) { // => to create 4 windows
            matchTokenTypes = matchTokenTypes.subList(0, windowSize + 3);
        }

        strategy.addMatchToFrequencyMap(matchTokenTypes, frequencyMap, windowSize);

        int expectedWindows = matchTokenTypes.size() - windowSize + 1;  // => should be 4
        assertEquals(expectedWindows, frequencyMap.size(), "Number of windows should be as expected");

        List<TokenType> expectedKeyTokens;
        expectedKeyTokens = matchTokenTypes.subList(2, matchTokenTypes.size() - 1);
        assertEquals(windowSize, expectedKeyTokens.size(), "Build False?");
        assertTrue(frequencyMap.containsKey(expectedKeyTokens.hashCode()), "Frequency map should contain the key for the window");
    }

    private static List<TokenType> getMatchTokenTypes(Match match) {
        List<Token> tokens = testSubmission.getTokenList().subList(match.startOfFirst(), match.startOfFirst() + match.lengthOfFirst());
        List<TokenType> tokenStrings = new ArrayList<>();

        for (Token token : tokens) {
            tokenStrings.add(token.getType());
        }
        return tokenStrings;
    }

    @Test
    @DisplayName("Test check() of window strategy")
    void testCheckWindowOfMatchesStrategy() {
        // Build
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<Integer, Integer> windowMap = new HashMap<>();
        int windowSize = 5;

        List<Token> tokensListInTestMatch = testSubmission.getTokenList().subList(matchShort.startOfFirst(),
                matchShort.startOfFirst() + matchShort.lengthOfFirst());
        List<TokenType> tokenListTestMatchReadable = tokensListInTestMatch.stream().map(Token::getType).toList();

        strategy.addMatchToFrequencyMap(tokenListTestMatchReadable, windowMap, windowSize);

        StringBuilder logBuilder = new StringBuilder("windowMap:\n");
        windowMap.forEach((key, value) -> logBuilder.append(String.format("  %s → %s%n", key, value)));
        logger.info(logBuilder.toString());

        // all Keys there?
        List<List<TokenType>> expectedKeys = new ArrayList<>();
        for (int i = 0; i <= tokenListTestMatchReadable.size() - windowSize; i++) {
            List<TokenType> expectedKey = tokenListTestMatchReadable.subList(i, i + windowSize);
            expectedKeys.add(expectedKey);
        }
        for (List<TokenType> key : expectedKeys) {
            assertTrue(windowMap.containsKey(key.hashCode()), "key missing: " + key);
        }

        // every key added once
        assertEquals(expectedKeys.size(), windowMap.size(), "More keys than windows????");

        // every window is added, and example value is correct
        for (List<TokenType> win : expectedKeys) {
            Integer keyVales = windowMap.get(win.hashCode());
            assertNotNull(keyVales, "value should be min 1: " + win);
            assertNotEquals(0, keyVales, "value should be min 1: " + keyVales);
        }

        // add entries
        int startIndex = 2;
        List<TokenType> tokenStringsNew = tokenListTestMatchReadable.subList(startIndex, startIndex + windowSize + 1);
        List<TokenType> keyNew1 = tokenStringsNew.subList(0, windowSize);
        List<TokenType> keyNew2 = tokenStringsNew.subList(1, windowSize + 1);

        assertTrue(windowMap.containsKey(keyNew1.hashCode()), "new key1 exists: " + keyNew1);
        assertTrue(windowMap.containsKey(keyNew2.hashCode()), "new key2 exists: " + keyNew2);

        strategy.addMatchToFrequencyMap(keyNew1, windowMap, windowSize);
        strategy.addMatchToFrequencyMap(keyNew2, windowMap, windowSize);

        Integer list1 = windowMap.get(keyNew1.hashCode());
        Integer list2 = windowMap.get(keyNew2.hashCode());

        assertEquals(2, list1, "does not contain 2 IDs: " + list1);
        assertEquals(2, list2, "does not contain 2 IDs: " + list2);
    }

    // Tets Submatch strategy
    @Test
    void testSubmatchesStrategy() {
        // Create
        SubMatchesStrategy strategy = new SubMatchesStrategy();
        Map<Integer, Integer> frequencyMap = new HashMap<>();

        List<Token> tokensListInTestMatch = testSubmission.getTokenList().subList(matchShort.startOfFirst(), matchShort.startOfFirst() + 5);
        List<TokenType> tokenListTestMatchReadable = tokensListInTestMatch.stream().map(Token::getType).toList();
        int minSize = 3;

        strategy.addMatchToFrequencyMap(tokenListTestMatchReadable, frequencyMap, minSize);

        Set<List<TokenType>> expectedKeys = new HashSet<>(List.of(tokenListTestMatchReadable, tokenListTestMatchReadable.subList(0, 4),
                tokenListTestMatchReadable.subList(1, 5), tokenListTestMatchReadable.subList(0, 3), tokenListTestMatchReadable.subList(1, 4),
                tokenListTestMatchReadable.subList(2, 5)));

        for (List<TokenType> key : expectedKeys) {
            assertTrue(frequencyMap.containsKey(key.hashCode()), "Map should contain key: " + key);
            assertTrue(frequencyMap.get(key.hashCode()) > 0, "Value list should be min 1 for key: " + key);
        }

        StringBuilder logBuilder = new StringBuilder("FrequencyMap entries:\n");
        for (Integer key : frequencyMap.keySet()) {
            int id = frequencyMap.get(key);
            assertTrue(id > 0, "Should be there min 1 time: " + id);
            logBuilder.append("key: ").append(key).append(" id: ").append(id).append("\n");
        }
        logger.info(logBuilder.toString());

        List<TokenType> subTokenList = tokenListTestMatchReadable.subList(1, 5);
        strategy.addMatchToFrequencyMap(subTokenList, frequencyMap, minSize);
        Set<List<TokenType>> expectedUsedKeys = new HashSet<>(List.of(tokenListTestMatchReadable.subList(1, 5),
                tokenListTestMatchReadable.subList(1, 4), tokenListTestMatchReadable.subList(2, 5)));

        for (List<TokenType> key : expectedUsedKeys) {
            assertEquals(2, frequencyMap.get(key.hashCode()), "Map should contain two keys: " + key);
            expectedKeys.remove(key);
        }
        for (List<TokenType> key : expectedKeys) {
            assertEquals(1, frequencyMap.get(key.hashCode()), "Map should only contain key: " + key);
        }
    }

    @Test
    void testCompleteMatchesIncludedInContainedStrategyForMatchesLongerMin() {
        int strategynumber = 100;
        FrequencyStrategy strategy = new ContainedMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, strategynumber);
        fd.buildFrequencyMap(testComparisons);
        Map<Integer, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        Map<List<TokenType>, Integer> frequencyCount = new HashMap<>();
        for (JPlagComparison comparison : testComparisons) {
            for (Match match : comparison.matches()) {
                List<TokenType> key = testSubmission.getTokenList().subList(match.startOfFirst(), match.startOfFirst() + match.lengthOfFirst())
                        .stream().map(Token::getType).toList();
                frequencyCount.put(key, frequencyCount.getOrDefault(key, 0) + 1);
                if (key.size() >= strategynumber) {
                    assertTrue(tokenFrequencyMap.containsKey(key.hashCode()), "Should contain key: " + key);
                }
            }
        }
        for (Map.Entry<List<TokenType>, Integer> entry : frequencyCount.entrySet()) {
            List<TokenType> key = entry.getKey();
            int freq = entry.getValue();
            if (key.size() >= strategynumber) {
                assertEquals(freq, tokenFrequencyMap.get(key.hashCode()), "there should be as much Ids as appearance: " + freq);
            }
        }
    }
}
