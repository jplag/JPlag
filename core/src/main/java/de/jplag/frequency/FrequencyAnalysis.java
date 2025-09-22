package de.jplag.frequency;

import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.TokenType;

/**
 * Contains the logic of the frequency based weighting of the Matches in all Comparisons, influencing the similarity
 * between two comparisons according to the FrequencyStrategy and Similarity strategy.
 */
public final class FrequencyAnalysis {

    private FrequencyAnalysis() {
        throw new IllegalStateException(); // private constructor for non-instantiability
    }

    /**
     * Calculates the rarity of all matched token sequences and weighs matches accordingly.
     * @param result are the JPlag results to re-weigh according to frequency of matched section.
     * @param options are the frequency analysis options.
     * @param minimumTokenMatch is the minimum token match value.
     * @return the modified result with re-weighed matches.
     */
    public static JPlagResult applyFrequencyWeighting(JPlagResult result, FrequencyAnalysisOptions options, int minimumTokenMatch) {

        // Compute absolute token sequence frequency:
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(options.frequencyStrategy().getStrategy(),
                Math.max(options.frequencyStrategyMinValue(), minimumTokenMatch));
        Map<List<TokenType>, Integer> tokenSequenceFrequencies = frequencyDetermination.buildFrequencyMap(result.getAllComparisons());

        // Compute absolute match sequence frequency:
        MatchFrequencyEvaluator frequencyEvaluator = new MatchFrequencyEvaluator(options.frequencyStrategy().getStrategy(), tokenSequenceFrequencies);
        Map<List<TokenType>, Double> matchFrequencies = frequencyEvaluator.computeMatchFrequencies(result.getAllComparisons());

        // Weigh matches based on frequency:
        MatchFrequencyWeighting weighting = new MatchFrequencyWeighting(result.getAllComparisons(), options.weightingStrategy(), matchFrequencies);
        List<JPlagComparison> convertedComparisons = result.getAllComparisons().stream()
                .map(comparison -> weighting.weightedComparisonSimilarity(comparison, options.weightingFactor())).toList();
        return new JPlagResult(convertedComparisons, result.getSubmissions(), result.getDuration(), result.getOptions());
    }

}
