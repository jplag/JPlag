package de.jplag.frequency;

import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.TokenType;
import de.jplag.frequency.strategy.FrequencyAnalysisStrategy;

/**
 * Applies frequency-based weighting to matches across all comparisons, influencing the similarity score according to
 * the configured {@link FrequencyAnalysisStrategy} and {@link MatchWeightingFunction}.
 */
public final class FrequencyAnalysis {

    private FrequencyAnalysis() {
        throw new IllegalStateException();
    }

    /**
     * Calculates the rarity of all matched token sequences and weighs matches accordingly.
     * @param result the JPlag results whose matches are to be re-weighed.
     * @param options the frequency analysis options.
     * @return a new {@link JPlagResult} with frequency-weighted comparison similarities.
     */
    public static JPlagResult applyFrequencyWeighting(JPlagResult result, FrequencyAnalysisOptions options) {
        List<JPlagComparison> comparisons = result.getAllComparisons();
        FrequencyAnalysisStrategy strategy = options.frequencyStrategy();
        strategy.processMatches(comparisons);
        MatchFrequencyEvaluator matchWeighting = new MatchFrequencyEvaluator(strategy);
        Map<List<TokenType>, Double> matchFrequency = matchWeighting.weightAllComparisons(comparisons);
        MatchFrequencyWeighting similarity = new MatchFrequencyWeighting(options.weightingFunction(), matchFrequency);
        List<JPlagComparison> weightedComparisons = comparisons.parallelStream()
                .map(comparison -> similarity.weightedComparisonSimilarity(comparison, options.weightingFactor())).toList();
        return new JPlagResult(weightedComparisons, result.getSubmissions(), result.getTokenizationDuration(), result.getComparisonDuration(),
                result.getOptions());
    }

}
