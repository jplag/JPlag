package de.jplag.highlight_extraction.frequencyDetermination;

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
import de.jplag.highlight_extraction.*;
import de.jplag.options.JPlagOptions;

class StrategyCreateAndCheckTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(StrategyCreateAndCheckTest.class);
    private static final StrategyIntegrationTest t = new StrategyIntegrationTest();
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
    List<JPlagComparison> comparisons = new LinkedList<>();

    @BeforeEach
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch strategy = new LongestCommonSubsequenceSearch(options);
        JPlagResult result = strategy.compareSubmissions(submissionSet);

        // Build test data
        JPlagComparison comparisonForTestData = result.getAllComparisons().getFirst();
        testSubmission = comparisonForTestData.firstSubmission();
        testMatchAOnTimeInComparisons = comparisonForTestData.matches().get(0);
        testMatchShort = new Match(comparisonForTestData.matches().get(0).startOfFirst(), comparisonForTestData.matches().get(0).startOfSecond(), 12,
                12);
        testMatchBTwoTimesInOneComparison = comparisonForTestData.matches().get(1);
        testMatchCTwoTimesInDifferentComparisons = comparisonForTestData.matches().get(2);
        testMatchDThreeTimesInDifferentComparisons = comparisonForTestData.matches().get(3);

        // renameSubmission
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

        // buildTestComparisons
        testMatches1.add(testMatchAOnTimeInComparisons);
        testMatches1.add(testMatchCTwoTimesInDifferentComparisons);
        JPlagComparison testComparison1 = new JPlagComparison(testSubmissionW, testSubmissionX, testMatches1, comparisonForTestData.ignoredMatches());

        testMatches2.add(testMatchCTwoTimesInDifferentComparisons);
        testMatches2.add(testMatchDThreeTimesInDifferentComparisons);
        JPlagComparison testComparison2 = new JPlagComparison(testSubmissionX, testSubmissionY, testMatches2, comparisonForTestData.ignoredMatches());

        testMatches3.add(testMatchDThreeTimesInDifferentComparisons);
        JPlagComparison testComparison3 = new JPlagComparison(testSubmissionY, testSubmissionZ, testMatches3, comparisonForTestData.ignoredMatches());

        testMatches4.add(testMatchDThreeTimesInDifferentComparisons);
        testMatches4.add(testMatchBTwoTimesInOneComparison);
        testMatches4.add(testMatchBTwoTimesInOneComparison);
        JPlagComparison testComparison4 = new JPlagComparison(testSubmissionZ, testSubmissionW, testMatches4, comparisonForTestData.ignoredMatches());

        comparisons.add(testComparison1);
        comparisons.add(testComparison2);
        comparisons.add(testComparison3);
        comparisons.add(testComparison4);

        logger.info("Comparisons: {}", comparisons);
    }

    // Test Compleate match strategy
    private void assertTokenFrequencyContainsMatch(Match match, int expectedFrequency, Map<List<TokenType>, Integer> tokenFrequencyMap) {
        int start = match.startOfFirst();
        int length = match.lengthOfFirst();
        List<TokenType> tokenNames = new LinkedList<>();
        List<Token> tokens = testSubmission.getTokenList().subList(start, start + length);

        for (Token token : tokens) {
            tokenNames.add(token.getType());
        }
        Integer submissionsContainingMatch = tokenFrequencyMap.get(tokenNames);
        if (submissionsContainingMatch == null) {
            throw new AssertionError("Match key [" + tokenNames + "] not found in tokenFrequencyMap.");
        }
        assertEquals(expectedFrequency, submissionsContainingMatch,
                "Match key [" + tokenNames + "] expected in " + expectedFrequency + " comparisons, but was in: " + submissionsContainingMatch);
    }

    @Test
    @DisplayName("Test Complete Matches Strategy")
    void testCompleteMatchesStrategy() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 9);
        fd.buildFrequencyMap(comparisons);
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        t.printTestResult(tokenFrequencyMap);

        assertTokenFrequencyContainsMatch(testMatchAOnTimeInComparisons, 1, tokenFrequencyMap);
        assertTokenFrequencyContainsMatch(testMatchBTwoTimesInOneComparison, 2, tokenFrequencyMap);
        assertTokenFrequencyContainsMatch(testMatchCTwoTimesInDifferentComparisons, 2, tokenFrequencyMap);
        assertTokenFrequencyContainsMatch(testMatchDThreeTimesInDifferentComparisons, 3, tokenFrequencyMap);
    }

    // Test Window Strategie
    @Test
    @DisplayName("Test WindowOfMatchesStrategy Create")
    void testWindowOfMatchesStrategyCreateTest() {
        int windowSize = 10;
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<List<TokenType>, Integer> frequencyMap = new HashMap<>();
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
        assertTrue(frequencyMap.containsKey(expectedKeyTokens), "Frequency map should contain the key for the window");
    }

    @Test
    @DisplayName("Test check() of window strategy")
    void testCheckWindowOfMatchesStrategy() {
        // Build
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<List<TokenType>, Integer> windowMap = new HashMap<>();
        int windowSize = 5;

        List<Token> tokensListInTestMatch = testSubmission.getTokenList().subList(testMatchShort.startOfFirst(),
                testMatchShort.startOfFirst() + testMatchShort.lengthOfFirst());
        List<TokenType> tokenListTestMatchReadable = tokensListInTestMatch.stream().map(token -> token.getType()).toList();

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
            assertTrue(windowMap.containsKey(key), "key missing: " + key);
        }

        // every key added once
        assertEquals(expectedKeys.size(), windowMap.size(), "More keys than windows????");

        // every window is added, and example value is correct
        for (List<TokenType> win : expectedKeys) {
            Integer keyVales = windowMap.get(win);
            assertNotNull(keyVales, "value should be min 1: " + win);
            assertTrue(keyVales != 0, "value should be min 1: " + keyVales);
        }

        // add entries
        int startIndex = 2;
        List<TokenType> tokenStringsNew = tokenListTestMatchReadable.subList(startIndex, startIndex + windowSize + 1);
        List<TokenType> keyNew1 = tokenStringsNew.subList(0, windowSize);
        List<TokenType> keyNew2 = tokenStringsNew.subList(1, windowSize + 1);

        assertTrue(windowMap.containsKey(keyNew1), "new key1 exists: " + keyNew1);
        assertTrue(windowMap.containsKey(keyNew2), "new key2 exists: " + keyNew2);

        strategy.addMatchToFrequencyMap(keyNew1, windowMap, windowSize);
        strategy.addMatchToFrequencyMap(keyNew2, windowMap, windowSize);

        Integer list1 = windowMap.get(keyNew1);
        Integer list2 = windowMap.get(keyNew2);

        assertEquals(2, list1, "does not contain 2 IDs: " + list1);
        assertEquals(2, list2, "does not contain 2 IDs: " + list2);
    }

    // Tets Submatch strategy
    @Test
    void testSubmatchesStrategy() {
        // Create
        SubMatchesStrategy strategy = new SubMatchesStrategy();
        Map<List<TokenType>, Integer> frequencyMap = new HashMap<>();

        List<Token> tokensListInTestMatch = testSubmission.getTokenList().subList(testMatchShort.startOfFirst(), testMatchShort.startOfFirst() + 5);
        List<TokenType> tokenListTestMatchReadable = tokensListInTestMatch.stream().map(Token::getType).toList();
        int minSize = 3;

        strategy.addMatchToFrequencyMap(tokenListTestMatchReadable, frequencyMap, minSize);

        Set<List<TokenType>> expectedKeys = new HashSet<>(List.of(tokenListTestMatchReadable, tokenListTestMatchReadable.subList(0, 4),
                tokenListTestMatchReadable.subList(1, 5), tokenListTestMatchReadable.subList(0, 3), tokenListTestMatchReadable.subList(1, 4),
                tokenListTestMatchReadable.subList(2, 5)));

        for (List<TokenType> key : expectedKeys) {
            assertTrue(frequencyMap.containsKey(key), "Map should contain key: " + key);
            assertTrue(frequencyMap.get(key) > 0, "Value list should be min 1 for key: " + key);
        }

        StringBuilder logBuilder = new StringBuilder("FrequencyMap entries:\n");
        for (List<TokenType> key : frequencyMap.keySet()) {
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
            assertEquals(2, frequencyMap.get(key), "Map should contain two keys: " + key);
            expectedKeys.remove(key);
        }
        for (List<TokenType> key : expectedKeys) {
            assertEquals(1, frequencyMap.get(key), "Map should only contain key: " + key);
        }
    }

    @Test
    void testCompleteMatchesIncludedInContainedStrategyForMatchesLongerMin() {
        int strategynumber = 100;
        FrequencyStrategy strategy = new ContainedMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, strategynumber);
        fd.buildFrequencyMap(comparisons);
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();

        for (List<TokenType> key : tokenFrequencyMap.keySet()) {
            assertTrue(key.size() >= strategynumber, "!should not exist: " + key.size());
        }

        List<List<TokenType>> expectedKeysWithValues = new LinkedList<>();
        List<List<TokenType>> keysWithFrequency = new ArrayList<>();
        List<Integer> frequencyOfKeys = new ArrayList<>();
        for (JPlagComparison comparison : comparisons) {
            for (Match match : comparison.matches()) {
                List<Token> keyToken = testSubmission.getTokenList();
                List<TokenType> keyNames = keyToken.stream().map(token -> token.getType()).toList();
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
                    assertTrue(tokenFrequencyMap.containsKey(key), "Should contain key: " + key);
                }

            }
        }

        for (int i = 0; i < keysWithFrequency.size(); i++) {
            int size = keysWithFrequency.get(i).size();
            if (size >= strategynumber) {
                assertEquals(frequencyOfKeys.get(i), tokenFrequencyMap.get(keysWithFrequency.get(i)),
                        "there should be as much Ids as appearance: " + frequencyOfKeys.get(i));

            }
        }

        for (List<TokenType> key : tokenFrequencyMap.keySet()) {
            int size = key.size();
            if (size >= strategynumber) {
                if (expectedKeysWithValues.contains(key)) {
                    break;
                }
                assertTrue(tokenFrequencyMap.get(key) == 0, "Should have count 0 " + key);
            }

        }
    }
}
