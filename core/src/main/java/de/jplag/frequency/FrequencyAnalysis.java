package de.jplag.frequency;

import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;

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
     */
    public static JPlagResult applyFrequencyWeighting(JPlagResult result, FrequencyAnalysisOptions options, int minimumTokenMatch) {

        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(options.frequencyStrategy().getStrategy(),
                Math.max(options.frequencyStrategyMinValue(), minimumTokenMatch));
        frequencyDetermination.buildFrequencyMap(result.getAllComparisons());
        MatchWeightCalculator matchWeighting = new MatchWeightCalculator(options.frequencyStrategy().getStrategy(),
                frequencyDetermination.getMatchFrequencyMap());
        MatchFrequency matchFrequency = matchWeighting.weightAllComparisons(result.getAllComparisons());
        MatchFrequencyWeighting similarity = new MatchFrequencyWeighting(result.getAllComparisons(), options.weightingStrategy(), matchFrequency);

        // Weigh matches based on frequency:
        List<JPlagComparison> convertedComparisons = result.getAllComparisons().stream()
                .map(comparison -> similarity.weightedComparisonSimilarity(comparison, options.weightingFactor())).toList();
        return new JPlagResult(convertedComparisons, result.getSubmissions(), result.getDuration(), result.getOptions());
    }

}
