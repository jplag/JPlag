package de.jplag.highlightextraction.frequencysimilarity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.*;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.highlightextraction.*;
import de.jplag.options.JPlagOptions;

/**
 * Checks if the frequency value is calculated and written into the matches correctly.
 */
public class MatchFrequencTest extends TestBase {
    public static FrequencyStrategy completeMatchesStrategy = new CompleteMatchesStrategy();
    public static FrequencyStrategy subMatchStrategie = new SubMatchesStrategy();
    public static FrequencyStrategy containedMatchesStrategy = new ContainedMatchesStrategy();
    public static FrequencyStrategy windowOfMatchesStrategy = new WindowOfMatchesStrategy();
    public Map<List<TokenType>, Integer> frequencyMap = new HashMap<>();
    Match testMatch;
    Match matchContained;
    Submission testSubmission;

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
     * Creates Test data to validate different match-frequency combinations.
     * @throws ExitException if getJPlagResult fails to create the comparison result.
     */
    @BeforeEach
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        JPlagResult result = getJPlagResult(options);
        JPlagComparison testComparison = result.getAllComparisons().getFirst();
        testMatch = testComparison.matches().get(0);
        testSubmission = testComparison.firstSubmission();
        matchContained = new Match(testMatch.getStartOfFirst(), testMatch.getStartOfSecond(), testMatch.getLengthOfFirst() - 1,
                testMatch.getLengthOfSecond() - 1);
    }

    /**
     * Adds the Sequence to the Frequency map.
     * @param sequence The token sequence whose frequency will be updated.
     */
    private void addSequenceKey(List<TokenType> sequence) {
        frequencyMap.putIfAbsent(sequence, 0);
    }

    /**
     * Updates the frequency of the given sequence in the frequency map.
     * @param sequence The token sequence whose frequency will be updated.
     */
    private void addSequence(List<TokenType> sequence) {
        frequencyMap.put(sequence, frequencyMap.getOrDefault(sequence, 0) + 1);
    }

    /**
     * Checks if the frequency value is calculated correctly in the completeMatchesStrategy
     */
    @Test
    @DisplayName("Match weigthed correct completeMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_completeMatchesStrategy() {
        frequencyMap.clear();
        List<TokenType> matchToken = new ArrayList<>();
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = testMatch.getStartOfFirst(); i <= testMatch.getLengthOfFirst() + testMatch.getStartOfFirst(); i++) {
            matchToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        completeMatchesStrategy.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 0);
        System.out.println(frequencyMap.get(matchToken));
        double weight = completeMatchesStrategy.calculateWeight(testMatch, frequencyMap, matchToken);
        System.out.println(weight);
        assertEquals(1.0, weight, 0.01, "only one Match added");

        completeMatchesStrategy.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 0);
        MatchWeighting weighting = new MatchWeighting(completeMatchesStrategy, frequencyMap);
        weighting.weightAllMatches(List.of(testMatch), submissionToken);
        System.out.println(testMatch.getFrequencyWeight());
        assertEquals(2.0, testMatch.getFrequencyWeight(), 0.01, "only one Match added twice");
    }

    /**
     * Checks if the frequency value is calculated correctly in the containedMatchesStrategy
     */
    @Test
    @DisplayName("Match weigthed correct containedMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_containedMatchesStrategy() {
        frequencyMap.clear();
        List<TokenType> matchToken = new ArrayList<>();
        List<TokenType> matchContainedToken = new ArrayList<>();
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = testMatch.getStartOfFirst(); i <= testMatch.getLengthOfFirst() + testMatch.getStartOfFirst(); i++) {
            matchToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = matchContained.getStartOfFirst(); i <= matchContained.getLengthOfFirst() + matchContained.getStartOfFirst(); i++) {
            matchContainedToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        containedMatchesStrategy.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 100);
        containedMatchesStrategy.processMatchTokenTypes(matchContainedToken, this::addSequenceKey, this::addSequence, 100);
        MatchWeighting weighting = new MatchWeighting(containedMatchesStrategy, frequencyMap);
        weighting.weightAllMatches(List.of(testMatch, matchContained), submissionToken);
        assertEquals(1.0, testMatch.getFrequencyWeight(), 0.01, "weight for 2 considered subsequences");
        assertEquals(1.0, matchContained.getFrequencyWeight(), 0.01, "once found");
    }

    /**
     * Checks if the frequency value is calculated correctly in the subMatchStrategie
     */
    @Test
    @DisplayName("Match weigthed correct subMatchStrategie")
    void testWeightMatch_setsCorrectWeight_subMatchStrategie() {
        frequencyMap.clear();
        List<TokenType> matchToken = new ArrayList<>();
        List<TokenType> matchContainedToken = new ArrayList<>();
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = testMatch.getStartOfFirst(); i <= testMatch.getLengthOfFirst() + testMatch.getStartOfFirst(); i++) {
            matchToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = matchContained.getStartOfFirst(); i <= matchContained.getLengthOfFirst() + matchContained.getStartOfFirst(); i++) {
            matchContainedToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        subMatchStrategie.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 100);
        subMatchStrategie.processMatchTokenTypes(matchContainedToken, this::addSequenceKey, this::addSequence, 100);
        MatchWeighting weighting = new MatchWeighting(subMatchStrategie, frequencyMap);
        weighting.weightAllMatches(List.of(testMatch, matchContained), submissionToken);
        assertEquals(2.0, testMatch.getFrequencyWeight(), 0.01, "considered subsequences");
        assertEquals(2.0, matchContained.getFrequencyWeight(), 0.01, "considered subsequences");
    }

    /**
     * Checks if the frequency value is calculated correctly in the windowOfMatchesStrategy
     */
    @Test
    @DisplayName("Match weigthed correct windowOfMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_windowOfMatchesStrategy() {
        frequencyMap.clear();
        List<TokenType> matchToken = new ArrayList<>();
        List<TokenType> matchContainedToken = new ArrayList<>();
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = testMatch.getStartOfFirst(); i <= testMatch.getLengthOfFirst() + testMatch.getStartOfFirst(); i++) {
            matchToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = matchContained.getStartOfFirst(); i <= matchContained.getLengthOfFirst() + matchContained.getStartOfFirst(); i++) {
            matchContainedToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        windowOfMatchesStrategy.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 100);
        windowOfMatchesStrategy.processMatchTokenTypes(matchContainedToken, this::addSequenceKey, this::addSequence, 100);
        MatchWeighting weighting = new MatchWeighting(windowOfMatchesStrategy, frequencyMap);
        weighting.weightAllMatches(List.of(testMatch, matchContained), submissionToken);
        assertEquals(2.0, testMatch.getFrequencyWeight(), 0.01, "considered subsequences");
        assertEquals(2.0, matchContained.getFrequencyWeight(), 0.01, "considered subsequences");
    }
}
