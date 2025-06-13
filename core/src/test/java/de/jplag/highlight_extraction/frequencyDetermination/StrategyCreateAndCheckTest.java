package de.jplag.highlight_extraction.frequencyDetermination;

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
import de.jplag.highlight_extraction.*;
import de.jplag.options.JPlagOptions;

public class StrategyCreateAndCheckTest extends TestBase {
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

    // @Test
    @BeforeEach
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism"); // getDefaultOptions("merging");
        System.out.println(options);
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch strategy = new LongestCommonSubsequenceSearch(options);
        JPlagResult result = strategy.compareSubmissions(submissionSet);
        System.out.println("result: ");
        System.out.println(result);

        // Build test data
        JPlagComparison comparisonForTestData = result.getAllComparisons().getFirst();
        testSubmission = comparisonForTestData.firstSubmission();
        testMatchAOnTimeInComparisons = comparisonForTestData.matches().get(0);
        testMatchShort = new Match(comparisonForTestData.matches().get(0).startOfFirst(), comparisonForTestData.matches().get(0).startOfSecond(), 12,
                12);
        testMatchBTwoTimesInOneComparison = comparisonForTestData.matches().get(1);
        testMatchCTwoTimesInDifferentComparisons = comparisonForTestData.matches().get(2);
        testMatchDThreeTimesInDifferentComparisons = comparisonForTestData.matches().get(3);

        // Show that the matches are different
        System.out.println(testMatchAOnTimeInComparisons.equals(testMatchBTwoTimesInOneComparison));
        System.out.println(testMatchBTwoTimesInOneComparison.equals(testMatchCTwoTimesInDifferentComparisons));
        System.out.println(testMatchCTwoTimesInDifferentComparisons.equals(testMatchDThreeTimesInDifferentComparisons));
        System.out.println(testMatchDThreeTimesInDifferentComparisons.equals(testMatchAOnTimeInComparisons));

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

        System.out.println(comparisons);
    }

    // Test Compleate match strategy
    private void assertTokenFrequencyContainsMatch(Match match, int expectedFrequency, Map<String, List<String>> tokenFrequencyMap) {
        int start = match.startOfFirst();
        int length = match.lengthOfFirst();
        List<String> tokenNames = new LinkedList<>();
        List<Token> tokens = testSubmission.getTokenList().subList(start, start + length);

        for (Token t : tokens) {
            tokenNames.add(t.getType().toString());
        }

        String key = String.join(" ", tokenNames);
        List<String> submissionsContainingMatch = tokenFrequencyMap.get(key);

        if (submissionsContainingMatch == null) {
            throw new AssertionError("Match key [" + key + "] not found in tokenFrequencyMap.");
        }

        assertEquals(expectedFrequency, submissionsContainingMatch.size(),
                "Match key [" + key + "] expected in " + expectedFrequency + " comparisons, but was in: " + submissionsContainingMatch);

        System.out.println("key matched with frequency " + submissionsContainingMatch.size());
    }

    @Test
    @DisplayName("Test Complete Matches Strategy")
    void testCompleteMatchesStrategy() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.runAnalysis(comparisons);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
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
        System.out.println("hi");
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<String, List<String>> frequencyMap = new HashMap<>();
        Match match = testMatchAOnTimeInComparisons;
        List<Token> tokens = testSubmission.getTokenList().subList(match.startOfFirst(), match.startOfFirst() + match.lengthOfFirst());
        List<String> tokenStrings = new ArrayList<>();

        for (Token token : tokens) {
            tokenStrings.add(token.getType().toString());
        }

        if (tokenStrings.size() > windowSize + 3) { // => 4 keys should be found
            tokenStrings = tokenStrings.subList(0, windowSize + 3);
        }

        strategy.create(tokenStrings, "testComparisonId", frequencyMap, windowSize);

        int expectedWindows = tokenStrings.size() - windowSize + 1;  // => should be 4
        assertEquals(expectedWindows, frequencyMap.size(), "Number of windows should be as expected");

        List<String> expectedKeyTokens;
        expectedKeyTokens = tokenStrings.subList(2, tokenStrings.size() - 1);
        assertEquals(windowSize, expectedKeyTokens.size(), "Build False?");

        String expectedKey = String.join(" ", expectedKeyTokens);
        assertTrue(frequencyMap.containsKey(expectedKey), "Frequency map should contain the key for the window");
    }

    @Test
    @DisplayName("Test check() of window strategy")
    void testCheckWindowOfMatchesStrategy() {
        // Build
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        Map<String, List<String>> windowMap = new HashMap<>();
        int windowSize = 5;

        List<Token> tokensListInTestMatch = testSubmission.getTokenList().subList(testMatchShort.startOfFirst(),
                testMatchShort.startOfFirst() + testMatchShort.lengthOfFirst());
        List<String> tokenListTestMatchReadable = tokensListInTestMatch.stream().map(token -> token.getType().toString()).toList();

        strategy.create(tokenListTestMatchReadable, "createId1", windowMap, windowSize);

        // Shold be exact the same
        System.out.println("tokenListTestMatchReadable");
        System.out.println(tokenListTestMatchReadable);
        assertDoesNotThrow(() -> strategy.check(tokenListTestMatchReadable, "testId1", windowMap, windowSize));

        System.out.println("windowMap: ");
        for (String key : windowMap.keySet()) {
            System.out.println(key + " → " + windowMap.get(key));
        }

        List<String> fakeTokenList = List.of("Z", "Y", "X", "W", "V", "U");
        assertThrows(IllegalStateException.class, () -> strategy.check(fakeTokenList, "testFakeTokenId", windowMap, windowSize));

        // all Keys there?
        List<String> expectedKeys = new ArrayList<>();
        for (int i = 0; i <= tokenListTestMatchReadable.size() - windowSize; i++) {
            List<String> expectedKey = tokenListTestMatchReadable.subList(i, i + windowSize);
            expectedKeys.add(String.join(" ", expectedKey));
        }
        for (String key : expectedKeys) {
            assertTrue(windowMap.containsKey(key), "key missing: " + key);
        }

        // every key added once
        assertEquals(expectedKeys.size(), windowMap.size(), "More keys than windows????");

        // every window is added, and example value is correct
        for (String win : expectedKeys) {
            List<String> keyVales = windowMap.get(win);
            assertNotNull(keyVales, "value should be min 1: " + win);
            assertEquals(1, keyVales.size(), "value not 1 for '" + win + "' with " + keyVales.size() + " size.");
            assertEquals("testId1", keyVales.getFirst(), "expected 'testId1' but got: " + keyVales);
        }

        // add entries
        int startIndex = 2;
        List<String> tokenStringsNew = tokenListTestMatchReadable.subList(startIndex, startIndex + windowSize + 1);
        List<String> newWindow1 = tokenStringsNew.subList(0, windowSize);
        List<String> newWindow2 = tokenStringsNew.subList(1, windowSize + 1);
        String keyNew1 = String.join(" ", newWindow1);
        String keyNew2 = String.join(" ", newWindow2);

        assertTrue(windowMap.containsKey(keyNew1), "new key1 exists: " + keyNew1);
        assertTrue(windowMap.containsKey(keyNew2), "new key2 exists: " + keyNew2);

        assertDoesNotThrow(() -> strategy.check(tokenStringsNew, "testId2", windowMap, windowSize));

        List<String> list1 = windowMap.get(keyNew1);
        List<String> list2 = windowMap.get(keyNew2);

        assertEquals(2, list1.size(), "does not contain 2 IDs: " + list1);
        assertTrue(list1.contains("testId1") && list1.contains("testId2"), "not contains testID1 nd testID2: " + list1);

        assertEquals(2, list2.size(), "does not contain 2 IDs: " + list2);
        assertTrue(list2.contains("testId1") && list2.contains("testId2"), "not contains testID1 nd testID2: " + list2);

    }

    // Tets Submatch strategy
    @Test
    void testSubmatchesStrategy() {

        // Create
        SubMatchesStrategy strategy = new SubMatchesStrategy();
        Map<String, List<String>> frequencyMap = new HashMap<>();
        String testId = "testId1";
        String testId2 = "testId2";

        List<Token> tokensListInTestMatch = testSubmission.getTokenList().subList(testMatchShort.startOfFirst(), testMatchShort.startOfFirst() + 5);
        System.out.println(tokensListInTestMatch);
        List<String> tokenListTestMatchReadable = tokensListInTestMatch.stream().map(token -> token.getType().toString()).toList();
        int minSize = 3;

        strategy.create(tokenListTestMatchReadable, testId, frequencyMap, minSize);

        Set<String> expectedKeys = new HashSet<>(
                List.of(String.join(" ", tokenListTestMatchReadable), String.join(" ", tokenListTestMatchReadable.subList(0, 4)),
                        String.join(" ", tokenListTestMatchReadable.subList(1, 5)), String.join(" ", tokenListTestMatchReadable.subList(0, 3)),
                        String.join(" ", tokenListTestMatchReadable.subList(1, 4)), String.join(" ", tokenListTestMatchReadable.subList(2, 5))));

        for (String k : frequencyMap.keySet()) {
            System.out.println(k);
        }

        for (String key : expectedKeys) {
            assertTrue(frequencyMap.containsKey(key), "Map should contain key: " + key);
            assertTrue(frequencyMap.get(key).isEmpty(), "Value list should be empty for key: " + key);
        }
        // Check
        strategy.check(tokenListTestMatchReadable, testId, frequencyMap, minSize);

        for (String key : frequencyMap.keySet()) {
            List<String> id = frequencyMap.get(key);
            assertTrue(id.contains(testId), "Id should be: " + testId);
            assertEquals(1, id.size(), "Should Contain only: " + testId);
            System.out.println("key: " + key + " id: " + id);
        }

        List<String> fakeTokens = List.of("X", "Y", "Z");
        assertDoesNotThrow(() -> strategy.check(fakeTokens, "noId", frequencyMap, minSize));

        List<String> subTokenList = tokenListTestMatchReadable.subList(1, 5);
        System.out.println(subTokenList);
        strategy.check(subTokenList, testId2, frequencyMap, minSize);

        Set<String> expectedUsedKeys = new HashSet<>(List.of(String.join(" ", tokenListTestMatchReadable.subList(1, 5)),
                String.join(" ", tokenListTestMatchReadable.subList(1, 4)), String.join(" ", tokenListTestMatchReadable.subList(2, 5))));

        for (String key : expectedUsedKeys) {
            assertEquals(2, frequencyMap.get(key).size(), "Map should contain two keys: " + key);
            assertTrue(frequencyMap.get(key).contains(testId2), "new Id should be added: " + key);
            expectedKeys.remove(key);
        }
        for (String key : expectedKeys) {
            assertEquals(1, frequencyMap.get(key).size(), "Map should only contain key: " + key);
            assertTrue(frequencyMap.get(key).contains(testId), "Value list should be: " + testId);
        }
    }

    @Test
    void testCompleteMatchesIncludedInContainedStrategyForMatchesLongerMin() {
        int strategynumber = 100;
        FrequencyStrategy strategy = new ContainedStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, strategynumber);
        fd.runAnalysis(comparisons);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();

        List<String> numberOfToken;
        for (String k : tokenFrequencyMap.keySet()) {
            numberOfToken = List.of(k.split(" "));
            assertTrue(numberOfToken.size() >= strategynumber, "!should not exist: " + numberOfToken.size());
        }

        List<String> expectedKeysWithValues = new LinkedList<>();
        List<String> keysWithFrequency = new ArrayList<>();
        List<Integer> frequencyOfKeys = new ArrayList<>();
        for (JPlagComparison comparison : comparisons) {
            for (Match match : comparison.matches()) {
                List<Token> keyToken = testSubmission.getTokenList();
                List<String> keyNames = keyToken.stream().map(token -> token.getType().toString()).toList();
                keyNames = keyNames.subList(match.startOfFirst(), match.startOfFirst() + match.lengthOfFirst());
                String key = String.join(" ", keyNames);
                if (keysWithFrequency.contains(key)) {
                    int index = keysWithFrequency.indexOf(key);
                    frequencyOfKeys.set(index, frequencyOfKeys.get(index) + 1);
                } else {
                    keysWithFrequency.add(key);
                    frequencyOfKeys.add(1);
                }
                expectedKeysWithValues.add(key);
                int size = List.of(key.split(" ")).size();
                if (size >= strategynumber) {
                    assertTrue(tokenFrequencyMap.containsKey(key), "Should contain key: " + key);
                    assertTrue(tokenFrequencyMap.get(key).contains(comparison.toString()), "Should containComparison Id: " + comparison);
                }

            }
        }

        for (int i = 0; i < keysWithFrequency.size(); i++) {
            int size = List.of(keysWithFrequency.get(i).split(" ")).size();
            if (size >= strategynumber) {
                assertEquals(frequencyOfKeys.get(i), tokenFrequencyMap.get(keysWithFrequency.get(i)).size(),
                        "there should be as much Ids as appearance: " + frequencyOfKeys.get(i));

            }
        }

        for (String key : tokenFrequencyMap.keySet()) {
            int size = List.of(key.split(" ")).size();
            if (size >= strategynumber) {
                if (expectedKeysWithValues.contains(key)) {
                    break;
                }
                assertTrue(tokenFrequencyMap.get(key).isEmpty(), "Should not have an Id: " + key);
            }

        }
    }
}
