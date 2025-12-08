package de.jplag.highlightextraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.TokenType;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.highlightextraction.strategy.CompleteMatchesStrategy;
import de.jplag.highlightextraction.strategy.ContainedMatchesStrategy;
import de.jplag.highlightextraction.strategy.FrequencyStrategy;
import de.jplag.highlightextraction.strategy.SubmatchesStrategy;
import de.jplag.highlightextraction.strategy.WindowOfMatchesStrategy;
import de.jplag.options.JPlagOptions;

/**
 * Checks if the frequency value is calculated and written into the matches correctly.
 */
class WeightStrategyTest extends TestBase {
    /**
     * Minimum length of a submatch or a match window.
     */
    private static final int MIN_LENGTH = 100;
    private Match match;
    private Match submatch;
    private JPlagComparison comparison;

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
        comparison = result.getAllComparisons().getFirst();
        match = comparison.matches().getFirst();
        submatch = new Match(match.startOfFirst(), match.startOfSecond(), match.lengthOfFirst() - 1, match.lengthOfSecond() - 1);
    }

    /**
     * Checks if the frequency value is calculated correctly in the completeMatchesStrategy.
     */
    @Test
    @DisplayName("Match weighted correct completeMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_completeMatchesStrategy() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        Map<List<TokenType>, Double> matchCounts = setupMatchCounts(strategy);
        assertWeight(matchCounts, match, 1.0);

        matchCounts = setupMatchCounts(strategy);
        assertWeight(matchCounts, match, 2.0);
    }

    /**
     * Checks if the frequency value is calculated correctly in the containedMatchesStrategy.
     */
    @Test
    @DisplayName("Match weighted correct containedMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_containedMatchesStrategy() {
        Map<List<TokenType>, Double> matchWeights = setupMatchCounts(new ContainedMatchesStrategy(MIN_LENGTH));
        assertWeight(matchWeights, match, 1.0);
        assertWeight(matchWeights, submatch, 1.0);
    }

    /**
     * Checks if the frequency value is calculated correctly in the subMatchStrategy.
     */
    @Test
    @DisplayName("Match weighted correct subMatchStrategy")
    void testWeightMatch_setsCorrectWeight_subMatchStrategy() {
        Map<List<TokenType>, Double> matchCounts = setupMatchCounts(new SubmatchesStrategy(MIN_LENGTH));
        assertWeight(matchCounts, match, 2.0);
        assertWeight(matchCounts, submatch, 2.0);
    }

    /**
     * Checks if the frequency value is calculated correctly in the windowOfMatchesStrategy.
     */
    @Test
    @DisplayName("Match weighted correct windowOfMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_windowOfMatchesStrategy() {
        Map<List<TokenType>, Double> matchCounts = setupMatchCounts(new WindowOfMatchesStrategy(MIN_LENGTH));
        assertWeight(matchCounts, match, 2.0);
        assertWeight(matchCounts, submatch, 2.0);
    }

    private Map<List<TokenType>, Double> setupMatchCounts(FrequencyStrategy strategy) {
        strategy.processMatch(comparison, match);
        strategy.processMatch(comparison, submatch);
        MatchWeightCalculator weighting = new MatchWeightCalculator(strategy);

        JPlagComparison comparison1 = new JPlagComparison(comparison.firstSubmission(), comparison.secondSubmission(), List.of(match, submatch),
                List.of());
        return weighting.weightAllMatches(comparison1);
    }

    private void assertWeight(Map<List<TokenType>, Double> matchWeights, Match match, double expected) {
        List<TokenType> matchTokens = TokenSequenceUtil.tokenTypesFor(comparison, match);
        assertTrue(matchWeights.containsKey(matchTokens));
        assertEquals(expected, matchWeights.get(matchTokens), 0.01);
    }
}
