package de.jplag.frequency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.TokenType;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.frequency.strategy.CompleteMatchesStrategy;
import de.jplag.frequency.strategy.FrequencyAnalysisStrategy;
import de.jplag.frequency.weighting.SigmoidWeighting;
import de.jplag.options.JPlagOptions;

/**
 * Tests the frequency analysis pipeline from raw comparisons to weighted similarity scores.
 */
class FrequencyAnalysisTest extends TestBase {

    private JPlagResult rawResult;

    @BeforeEach
    void setUp() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        rawResult = new LongestCommonSubsequenceSearch(options).compareSubmissions(submissionSet);
    }

    @Test
    @DisplayName("The number of comparisons is preserved after frequency weighting")
    void preservesNumberOfComparisons() {
        FrequencyAnalysisOptions options = new FrequencyAnalysisOptions().withEnabled(true).withFrequencyStrategy(new CompleteMatchesStrategy())
                .withWeightingFunction(new SigmoidWeighting());

        JPlagResult weightedResult = FrequencyAnalysis.applyFrequencyWeighting(rawResult, options);

        assertEquals(rawResult.getAllComparisons().size(), weightedResult.getAllComparisons().size());
    }

    @Test
    @DisplayName("Every weighted comparison has the frequency weighting flag set and a non-negative score")
    void allComparisonsHaveFrequencyWeightingFlagSet() {
        FrequencyAnalysisOptions options = new FrequencyAnalysisOptions().withEnabled(true).withFrequencyStrategy(new CompleteMatchesStrategy())
                .withWeightingFunction(new SigmoidWeighting());

        JPlagResult weightedResult = FrequencyAnalysis.applyFrequencyWeighting(rawResult, options);

        for (int i = 0; i < weightedResult.getAllComparisons().size(); i++) {
            JPlagComparison comparison = weightedResult.getAllComparisons().get(i);
            assertTrue(comparison.useFrequencyWeighting(), "comparison " + i + " missing flag");
            assertTrue(comparison.frequencyWeightedSimilarity() >= 0, "comparison " + i + " has negative weighted similarity");
        }
    }

    @Test
    @DisplayName("A weighting factor of zero leaves the weighted similarity identical to the original")
    void weightingFactorZeroPreservesSimilarity() {
        FrequencyAnalysisOptions options = new FrequencyAnalysisOptions().withEnabled(true).withFrequencyStrategy(new CompleteMatchesStrategy())
                .withWeightingFunction(new SigmoidWeighting()).withWeightingFactor(0.0);

        JPlagResult weightedResult = FrequencyAnalysis.applyFrequencyWeighting(rawResult, options);
        List<JPlagComparison> original = rawResult.getAllComparisons();
        List<JPlagComparison> weighted = weightedResult.getAllComparisons();

        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).similarity(), weighted.get(i).frequencyWeightedSimilarity(), 0.001,
                    "weighting factor 0 should preserve original similarity for comparison " + i);
        }
    }

    @Test
    @DisplayName("A weighting factor of one produces valid non-negative weighted similarities for all comparisons")
    void weightingFactorOneProducesNonNegativeWeightedSimilarity() {
        FrequencyAnalysisOptions options = new FrequencyAnalysisOptions().withEnabled(true).withFrequencyStrategy(new CompleteMatchesStrategy())
                .withWeightingFunction(new SigmoidWeighting()).withWeightingFactor(1.0);

        JPlagResult weightedResult = FrequencyAnalysis.applyFrequencyWeighting(rawResult, options);

        for (JPlagComparison comparison : weightedResult.getAllComparisons()) {
            assertTrue(comparison.useFrequencyWeighting());
            assertTrue(comparison.frequencyWeightedSimilarity() >= 0,
                    "weighted similarity should be non-negative, got " + comparison.frequencyWeightedSimilarity());
        }
    }

    @Test
    @DisplayName("The pipeline accepts custom inline strategy and weighting function implementations")
    void respectsCustomStrategyAndWeightingFunction() {
        FrequencyAnalysisStrategy counting = new FrequencyAnalysisStrategy() {
            @Override
            protected void processMatchTokenTypes(List<TokenType> matchTokenTypes) {
                incrementSequence(matchTokenTypes);
            }

            @Override
            public double calculateMatchCount(List<TokenType> matchTokens) {
                return getCount(matchTokens);
            }
        };

        MatchWeightingFunction fixedWeight = rarity -> 0.5;

        FrequencyAnalysisOptions options = new FrequencyAnalysisOptions().withEnabled(true).withFrequencyStrategy(counting)
                .withWeightingFunction(fixedWeight).withWeightingFactor(1.0);

        JPlagResult weightedResult = FrequencyAnalysis.applyFrequencyWeighting(rawResult, options);

        for (JPlagComparison comparison : weightedResult.getAllComparisons()) {
            assertTrue(comparison.useFrequencyWeighting());
            assertTrue(comparison.frequencyWeightedSimilarity() >= 0);
        }
    }
}
