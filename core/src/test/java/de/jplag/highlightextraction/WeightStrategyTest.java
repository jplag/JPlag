package de.jplag.highlightextraction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
 * Checks if the frequency value is calculated correctly by each FrequencyStrategy.
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
     * Creates test data by running JPlag to produce a real comparison result.
     * @throws ExitException if building the submission set fails.
     */
    @BeforeEach
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        JPlagResult result = new LongestCommonSubsequenceSearch(options).compareSubmissions(submissionSet);
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
        strategy.processMatch(comparison, match);
        strategy.processMatch(comparison, submatch);
        assertMatchCount(strategy, match, 1.0);

        strategy.processMatch(comparison, match);
        strategy.processMatch(comparison, submatch);
        assertMatchCount(strategy, match, 2.0);
    }

    /**
     * Checks if the frequency value is calculated correctly in the containedMatchesStrategy.
     */
    @Test
    @DisplayName("Match weighted correct containedMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_containedMatchesStrategy() {
        FrequencyStrategy strategy = new ContainedMatchesStrategy(MIN_LENGTH);
        strategy.processMatch(comparison, match);
        strategy.processMatch(comparison, submatch);
        assertMatchCount(strategy, match, 1.0);
        assertMatchCount(strategy, submatch, 1.0);
    }

    /**
     * Checks if the frequency value is calculated correctly in the subMatchStrategy.
     */
    @Test
    @DisplayName("Match weighted correct subMatchStrategy")
    void testWeightMatch_setsCorrectWeight_subMatchStrategy() {
        FrequencyStrategy strategy = new SubmatchesStrategy(MIN_LENGTH);
        strategy.processMatch(comparison, match);
        strategy.processMatch(comparison, submatch);
        assertMatchCount(strategy, match, 2.0);
        assertMatchCount(strategy, submatch, 2.0);
    }

    /**
     * Checks if the frequency value is calculated correctly in the windowOfMatchesStrategy.
     */
    @Test
    @DisplayName("Match weighted correct windowOfMatchesStrategy")
    void testWeightMatch_setsCorrectWeight_windowOfMatchesStrategy() {
        FrequencyStrategy strategy = new WindowOfMatchesStrategy(MIN_LENGTH);
        strategy.processMatch(comparison, match);
        strategy.processMatch(comparison, submatch);
        assertMatchCount(strategy, match, 2.0);
        assertMatchCount(strategy, submatch, 2.0);
    }

    private void assertMatchCount(FrequencyStrategy strategy, Match match, double expected) {
        List<TokenType> matchTokens = TokenSequenceUtil.tokenTypesFor(comparison, match);
        assertEquals(expected, strategy.calculateMatchCount(matchTokens), 0.01);
    }
}
