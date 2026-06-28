package de.jplag.highlightextraction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
 * Checks that each FrequencyStrategy calculates the correct match count after one round of processing.
 */
class WeightStrategyTest extends TestBase {

    private static final int MIN_LENGTH = 100;

    private Match match;
    private Match submatch;
    private JPlagComparison comparison;

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

    static Stream<Arguments> strategies() {
        return Stream.of(Arguments.of(new CompleteMatchesStrategy(), 1.0, 1.0), //
                Arguments.of(new ContainedMatchesStrategy(MIN_LENGTH), 1.0, 1.0), //
                Arguments.of(new SubmatchesStrategy(MIN_LENGTH), 2.0, 2.0), //
                Arguments.of(new WindowOfMatchesStrategy(MIN_LENGTH), 2.0, 2.0));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strategies")
    void testMatchCountAfterOneRound(FrequencyStrategy strategy, double expectedMatch, double expectedSubmatch) {
        strategy.processMatch(comparison, match);
        strategy.processMatch(comparison, submatch);
        assertMatchCount(strategy, match, expectedMatch);
        assertMatchCount(strategy, submatch, expectedSubmatch);
    }

    private void assertMatchCount(FrequencyStrategy strategy, Match match, double expected) {
        List<TokenType> matchTokens = TokenSequenceUtil.tokenTypesFor(comparison, match);
        assertEquals(expected, strategy.calculateMatchCount(matchTokens), 0.01);
    }
}
