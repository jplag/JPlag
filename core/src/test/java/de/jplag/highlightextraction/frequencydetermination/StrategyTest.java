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
 * Test class to validate the FrequencyStrategies and their usage. As the examples use testCode from "PartialPlagiarism"
 * sample-folder and some fictional data.
 */
class StrategyTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(StrategyTest.class);
    private static final StrategyIntegrationTest strategyIntegrationTest = new StrategyIntegrationTest();
    private static Submission testSubmission;
    private static Match testMatchAOnTimeInComparisons;
    private static Match testMatchBTwoTimesInOneComparison;
    private static Match testMatchCTwoTimesInDifferentComparisons;
    private static Match testMatchDThreeTimesInDifferentComparisons;
    private static Match testMatchShort;
    List<Match> testMatches1 = new LinkedList<>();
    List<Match> testMatches2 = new LinkedList<>();
    List<Match> testMatches3 = new LinkedList<>();
    List<Match> testMatches4 = new LinkedList<>();
    static List<Match> ignoredMatches = new LinkedList<>();
    List<JPlagComparison> testComparisons = new LinkedList<>();

    /**
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
     * @param options JPlag options used in this test
     * @return JPlag result
     * @throws ExitException submission set builder can throw this exception
     */
    private JPlagResult getJPlagResult(JPlagOptions options) throws ExitException {
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch subsequenceSearch = new LongestCommonSubsequenceSearch(options);
        return subsequenceSearch.compareSubmissions(submissionSet);
    }

    /**
     * @param testComparison first Comparison from the Test classes here used to get test matches
     */
    private static void buildTestMatches(JPlagComparison testComparison) {
        testSubmission = testComparison.firstSubmission();
        testMatchAOnTimeInComparisons = testComparison.matches().get(0);
        testMatchBTwoTimesInOneComparison = testComparison.matches().get(1);
        testMatchCTwoTimesInDifferentComparisons = testComparison.matches().get(2);
        testMatchDThreeTimesInDifferentComparisons = testComparison.matches().get(3);
        testMatchShort = new Match(testComparison.matches().get(0).startOfFirst(), testComparison.matches().get(0).startOfSecond(), 12, 12);
        ignoredMatches = testComparison.ignoredMatches();
    }

    /**
     * @param testSubmissionW name of a new Submission to Identify the testSubmissions
     * @param testSubmissionX name of a new Submission to Identify the testSubmissions
     * @param testSubmissionY name of a new Submission to Identify the testSubmissions
     * @param testSubmissionZ name of a new Submission to Identify the testSubmissions
     */
    private record getTestSubmissions(Submission testSubmissionW, Submission testSubmissionX, Submission testSubmissionY,
            Submission testSubmissionZ) {
    }

    /**
     * @param options the JPlag options for the test, to get the language
     * @return multiple submissions with the same data but different names for testing
     */
    private static getTestSubmissions getTestSubmissions(JPlagOptions options) {
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
        return new getTestSubmissions(testSubmissionW, testSubmissionX, testSubmissionY, testSubmissionZ);
    }

    /**
     * @param testSubmissions multiple submissions with the same data but different names for testing
     */
    private void buildTestComparisons(getTestSubmissions testSubmissions) {
        testMatches1.add(testMatchAOnTimeInComparisons);
        testMatches1.add(testMatchCTwoTimesInDifferentComparisons);
        JPlagComparison testComparison1 = new JPlagComparison(testSubmissions.testSubmissionW(), testSubmissions.testSubmissionX(), testMatches1,
                ignoredMatches);

        testMatches2.add(testMatchCTwoTimesInDifferentComparisons);
        testMatches2.add(testMatchDThreeTimesInDifferentComparisons);
        JPlagComparison testComparison2 = new JPlagComparison(testSubmissions.testSubmissionX(), testSubmissions.testSubmissionY(), testMatches2,
                ignoredMatches);

        testMatches3.add(testMatchDThreeTimesInDifferentComparisons);
        JPlagComparison testComparison3 = new JPlagComparison(testSubmissions.testSubmissionY(), testSubmissions.testSubmissionZ(), testMatches3,
                ignoredMatches);

        testMatches4.add(testMatchDThreeTimesInDifferentComparisons);
        testMatches4.add(testMatchBTwoTimesInOneComparison);
        testMatches4.add(testMatchBTwoTimesInOneComparison);
        JPlagComparison testComparison4 = new JPlagComparison(testSubmissions.testSubmissionZ(), testSubmissions.testSubmissionW(), testMatches4,
                ignoredMatches);

        testComparisons.add(testComparison1);
        testComparisons.add(testComparison2);
        testComparisons.add(testComparison3);
        testComparisons.add(testComparison4);
    }

    @Test
    @DisplayName("Test Complete Matches Strategy")
    void testCompleteMatchesStrategy() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(strategy, 9);
        frequencyDetermination.buildFrequencyMap(testComparisons);
        Map<Integer, Integer> tokenFrequencyMap = frequencyDetermination.getMatchFrequencyMap();
        strategyIntegrationTest.printTestResult(tokenFrequencyMap);

        assertTokenFrequencyContainsMatch(testMatchAOnTimeInComparisons, 1, tokenFrequencyMap);
        assertTokenFrequencyContainsMatch(testMatchBTwoTimesInOneComparison, 2, tokenFrequencyMap);
        assertTokenFrequencyContainsMatch(testMatchCTwoTimesInDifferentComparisons, 2, tokenFrequencyMap);
        assertTokenFrequencyContainsMatch(testMatchDThreeTimesInDifferentComparisons, 3, tokenFrequencyMap);
    }

    // Test Complete match strategy
    private void assertTokenFrequencyContainsMatch(Match match, int expectedFrequency, Map<Integer, Integer> tokenFrequencyMap) {
        int start = match.startOfFirst();
        int length = match.lengthOfFirst();
        List<TokenType> tokenNames = new LinkedList<>();
        List<Token> tokens = testSubmission.getTokenList().subList(start, start + length);

        for (Token token : tokens) {
            tokenNames.add(token.getType());
        }
        Integer submissionsContainingMatch = tokenFrequencyMap.get(tokenNames.hashCode());
        if (submissionsContainingMatch == null) {
            throw new AssertionError("Match key [" + tokenNames + "] not found in tokenFrequencyMap.");
        }
        assertEquals(expectedFrequency, submissionsContainingMatch,
                "Match key [" + tokenNames + "] expected in " + expectedFrequency + " comparisons, but was in: " + submissionsContainingMatch);
    }

    // Test Window Strategie
    @Test
    @DisplayName("Test WindowOfMatchesStrategy Create")
    void testWindowOfMatchesStrategyCreateTest() {
        int windowSize = 10;
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        Match match = testMatchAOnTimeInComparisons;
        List<Token> tokens = testSubmission.getTokenList().subList(match.startOfFirst(), match.startOfFirst() + match.lengthOfFirst());
        List<TokenType> tokenStrings = new ArrayList<>();

        for (Token token : tokens) {
            tokenStrings.add(token.getType());
        }

        if (tokenStrings.size() > windowSize + 3) { // => 4 keys should be found
            tokenStrings = tokenStrings.subList(0, windowSize + 3);
        }

        strategy.addMatchToFrequencyMap(tokenStrings, frequencyMap, windowSize);

        int expectedWindows = tokenStrings.size() - windowSize + 1;  // => should be 4
        assertEquals(expectedWindows, frequencyMap.size(), "Number of windows should be as expected");

        List<TokenType> expectedKeyTokens;
        expectedKeyTokens = tokenStrings.subList(2, tokenStrings.size() - 1);
        assertEquals(windowSize, expectedKeyTokens.size(), "Build False?");
        assertTrue(frequencyMap.containsKey(expectedKeyTokens.hashCode()), "Frequency map should contain the key for the window");
    }

    @Test
    @DisplayName("Test check() of window strategy")
    void testCheckWindowOfMatchesStrategy() {
        // Build
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<Integer, Integer> windowMap = new HashMap<>();
        int windowSize = 5;

        List<Token> tokensListInTestMatch = testSubmission.getTokenList().subList(testMatchShort.startOfFirst(),
                testMatchShort.startOfFirst() + testMatchShort.lengthOfFirst());
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

        List<Token> tokensListInTestMatch = testSubmission.getTokenList().subList(testMatchShort.startOfFirst(), testMatchShort.startOfFirst() + 5);
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

        // for (Integer key : tokenFrequencyMap.keySet()) {
        // assertTrue(key..size() >= strategynumber, "!should not exist: " + key.size());
        // }

        List<List<TokenType>> expectedKeysWithValues = new LinkedList<>();
        List<List<TokenType>> keysWithFrequency = new ArrayList<>();
        List<Integer> frequencyOfKeys = new ArrayList<>();
        for (JPlagComparison comparison : testComparisons) {
            for (Match match : comparison.matches()) {
                List<Token> keyToken = testSubmission.getTokenList();
                List<TokenType> keyNames = keyToken.stream().map(Token::getType).toList();
                keyNames = keyNames.subList(match.startOfFirst(), match.startOfFirst() + match.lengthOfFirst());
                List<TokenType> key = keyNames;
                if (keysWithFrequency.contains(key)) {
                    int index = keysWithFrequency.indexOf(key);
                    frequencyOfKeys.set(index, frequencyOfKeys.get(index) + 1);
                } else {
                    keysWithFrequency.add(key);
                    frequencyOfKeys.add(1);
                }
                expectedKeysWithValues.add(key);
                int size = key.size();
                if (size >= strategynumber) {
                    assertTrue(tokenFrequencyMap.containsKey(key.hashCode()), "Should contain key: " + key);
                }

            }
        }

        for (int i = 0; i < keysWithFrequency.size(); i++) {
            int size = keysWithFrequency.get(i).size();
            if (size >= strategynumber) {
                assertEquals(frequencyOfKeys.get(i), tokenFrequencyMap.get(keysWithFrequency.get(i).hashCode()),
                        "there should be as much Ids as appearance: " + frequencyOfKeys.get(i));

            }
        }

        // for (Integer key : tokenFrequencyMap.keySet()) {
        // int size = key.toStrisize();
        // if (size >= strategynumber) {
        // if (expectedKeysWithValues.contains(key)) {
        // break;
        // }
        // assertEquals(0, tokenFrequencyMap.get(key), "Should have count 0 " + key);
        // }

        // }
    }
}
