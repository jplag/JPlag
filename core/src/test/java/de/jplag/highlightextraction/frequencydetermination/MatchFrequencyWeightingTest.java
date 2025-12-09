package de.jplag.highlightextraction.frequencydetermination;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
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
import de.jplag.frequency.CompleteMatchesStrategy;
import de.jplag.frequency.ContainedMatchesStrategy;
import de.jplag.frequency.FrequencyStrategy;
import de.jplag.frequency.FrequencyUtil;
import de.jplag.frequency.MatchFrequencyEvaluator;
import de.jplag.frequency.SubMatchesStrategy;
import de.jplag.frequency.WindowOfMatchesStrategy;
import de.jplag.options.JPlagOptions;

/**
 * Checks if the frequency value is calculated and written into the matches correctly.
 */
class MatchFrequencyWeightingTest extends TestBase {
    private static final FrequencyStrategy COMPLETE_MATCHES_STRATEGY = new CompleteMatchesStrategy();
    private static final FrequencyStrategy SUB_MATCHES_STRATEGY = new SubMatchesStrategy();
    private static final FrequencyStrategy CONTAINED_MATCHES_STRATEGY = new ContainedMatchesStrategy();
    private static final FrequencyStrategy WINDOW_OF_MATCHES_STRATEGY = new WindowOfMatchesStrategy();
    private final Map<List<TokenType>, Integer> frequencyMap = new HashMap<>();
    private Match testMatch;
    private Match matchContained;
    private Submission testSubmission;

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
        matchContained = new Match(testMatch.startOfFirst(), testMatch.startOfSecond(), testMatch.lengthOfFirst() - 1,
                testMatch.lengthOfSecond() - 1);
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
     * Checks if the frequency value is calculated correctly in the completeMatchesStrategy.
     */
    @Test
    @DisplayName("Match weighted correct completeMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_completeMatchesStrategy() {
        frequencyMap.clear();
        List<TokenType> matchToken = new ArrayList<>();
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = testMatch.startOfFirst(); i <= testMatch.lengthOfFirst() + testMatch.startOfFirst(); i++) {
            matchToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        COMPLETE_MATCHES_STRATEGY.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 0);
        System.out.println(frequencyMap.get(matchToken));
        double weight = COMPLETE_MATCHES_STRATEGY.calculateMatchFrequency(testMatch, frequencyMap, matchToken);
        System.out.println(weight);
        assertEquals(1.0, weight, 0.01, "only one Match added");

        COMPLETE_MATCHES_STRATEGY.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 0);
        MatchFrequencyEvaluator weighting = new MatchFrequencyEvaluator(COMPLETE_MATCHES_STRATEGY, frequencyMap);
        Map<List<TokenType>, Double> matchFrequency = weighting.computeMatchFrequencies(List.of(testMatch), submissionToken);
        List<TokenType> testSubmissionTokenTypes = testSubmission.getTokenList().stream().map(Token::getType).toList();
        double matchFrequencyCalculated = matchFrequency.get(FrequencyUtil.matchesToMatchTokenTypes(testMatch, testSubmissionTokenTypes));
        assertEquals(0.0, matchFrequencyCalculated, 0.01, "only one Match added twice");
    }

    /**
     * Checks if the frequency value is calculated correctly in the containedMatchesStrategy.
     */
    @Test
    @DisplayName("Match weighted correct containedMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_containedMatchesStrategy() {
        frequencyMap.clear();
        List<TokenType> matchToken = new ArrayList<>();
        List<TokenType> matchContainedToken = new ArrayList<>();
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = testMatch.startOfFirst(); i <= testMatch.lengthOfFirst() + testMatch.startOfFirst(); i++) {
            matchToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = matchContained.startOfFirst(); i <= matchContained.lengthOfFirst() + matchContained.startOfFirst(); i++) {
            matchContainedToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        CONTAINED_MATCHES_STRATEGY.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 100);
        CONTAINED_MATCHES_STRATEGY.processMatchTokenTypes(matchContainedToken, this::addSequenceKey, this::addSequence, 100);
        MatchFrequencyEvaluator weighting = new MatchFrequencyEvaluator(CONTAINED_MATCHES_STRATEGY, frequencyMap);
        weighting.computeMatchFrequencies(List.of(testMatch, matchContained), submissionToken);
        Map<List<TokenType>, Double> matchFrequency = weighting.computeMatchFrequencies(List.of(testMatch), submissionToken);
        List<TokenType> testSubmissionTokenTypes = testSubmission.getTokenList().stream().map(Token::getType).toList();
        double matchFrequencyCalculated = matchFrequency.get(FrequencyUtil.matchesToMatchTokenTypes(testMatch, testSubmissionTokenTypes));
        double matchFrequencyCalculated1 = matchFrequency.get(FrequencyUtil.matchesToMatchTokenTypes(matchContained, testSubmissionTokenTypes));
        assertEquals(1.0, matchFrequencyCalculated, 0.01, "weight for 2 considered subsequences");
        assertEquals(0.0, matchFrequencyCalculated1, 0.01, "once found");
    }

    /**
     * Checks if the frequency value is calculated correctly in the subMatchStrategy.
     */
    @Test
    @DisplayName("Match weighted correct subMatchStrategy")
    void testWeightMatch_setsCorrectWeight_subMatchStrategy() {
        frequencyMap.clear();
        List<TokenType> matchToken = new ArrayList<>();
        List<TokenType> matchContainedToken = new ArrayList<>();
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = testMatch.startOfFirst(); i <= testMatch.lengthOfFirst() + testMatch.startOfFirst(); i++) {
            matchToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = matchContained.startOfFirst(); i <= matchContained.lengthOfFirst() + matchContained.startOfFirst(); i++) {
            matchContainedToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        SUB_MATCHES_STRATEGY.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 100);
        SUB_MATCHES_STRATEGY.processMatchTokenTypes(matchContainedToken, this::addSequenceKey, this::addSequence, 100);
        MatchFrequencyEvaluator weighting = new MatchFrequencyEvaluator(SUB_MATCHES_STRATEGY, frequencyMap);
        weighting.computeMatchFrequencies(List.of(testMatch, matchContained), submissionToken);
        Map<List<TokenType>, Double> matchFrequency = weighting.computeMatchFrequencies(List.of(testMatch), submissionToken);
        List<TokenType> testSubmissionTokenTypes = testSubmission.getTokenList().stream().map(Token::getType).toList();
        double matchFrequencyCalculated = matchFrequency.get(FrequencyUtil.matchesToMatchTokenTypes(testMatch, testSubmissionTokenTypes));
        double matchFrequencyCalculated1 = matchFrequency.get(FrequencyUtil.matchesToMatchTokenTypes(matchContained, testSubmissionTokenTypes));
        assertEquals(2.0, matchFrequencyCalculated, 0.01, "considered subsequences");
        assertEquals(2.0, matchFrequencyCalculated1, 0.01, "considered subsequences");
    }

    /**
     * Checks if the frequency value is calculated correctly in the windowOfMatchesStrategy.
     */
    @Test
    @DisplayName("Match weighted correct windowOfMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_windowOfMatchesStrategy() {
        frequencyMap.clear();
        List<TokenType> matchToken = new ArrayList<>();
        List<TokenType> matchContainedToken = new ArrayList<>();
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = testMatch.startOfFirst(); i <= testMatch.lengthOfFirst() + testMatch.startOfFirst(); i++) {
            matchToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = matchContained.startOfFirst(); i <= matchContained.lengthOfFirst() + matchContained.startOfFirst(); i++) {
            matchContainedToken.add(testSubmission.getTokenList().get(i).getType());
        }
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        WINDOW_OF_MATCHES_STRATEGY.processMatchTokenTypes(matchToken, this::addSequenceKey, this::addSequence, 100);
        WINDOW_OF_MATCHES_STRATEGY.processMatchTokenTypes(matchContainedToken, this::addSequenceKey, this::addSequence, 100);
        MatchFrequencyEvaluator weighting = new MatchFrequencyEvaluator(WINDOW_OF_MATCHES_STRATEGY, frequencyMap);
        weighting.computeMatchFrequencies(List.of(testMatch, matchContained), submissionToken);
        Map<List<TokenType>, Double> matchFrequency = weighting.computeMatchFrequencies(List.of(testMatch), submissionToken);
        List<TokenType> testSubmissionTokenTypes = testSubmission.getTokenList().stream().map(Token::getType).toList();
        double matchFrequencyCalculated = matchFrequency.get(FrequencyUtil.matchesToMatchTokenTypes(testMatch, testSubmissionTokenTypes));
        double matchFrequencyCalculated1 = matchFrequency.get(FrequencyUtil.matchesToMatchTokenTypes(matchContained, testSubmissionTokenTypes));
        assertEquals(2.0, matchFrequencyCalculated, 0.01, "considered subsequences");
        assertEquals(2.0, matchFrequencyCalculated1, 0.01, "considered subsequences");
    }
}
