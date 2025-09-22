package de.jplag.frequency;

import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;

/**
 * Contains the logic of the frequency based weighting of the Matches in all Comparisons, influencing the similarity
 * between two comparisons according to the FrequencyStrategy and Similarity strategy. isFrequencyAnalysisEnabled =
 * false would use the old similarity.
 */
public final class FrequencyMatchWeighter {

    private FrequencyMatchWeighter() {
        throw new IllegalStateException(); // private constructor for non-instantiability
    }

    /**
     * @param options JPlagOptions
     * @param result JPlagResult
     * @return the new Comparisons with a weighted similarity.
     */
    public static List<JPlagComparison> useMatchFrequencyToInfluenceSimilarity(JPlagResult result, FrequencyAnalysisOptions options,
            int minimumTokenMatch) {

        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(options.frequencyStrategy().getStrategy(),
                Math.max(options.frequencyStrategyMinValue(), minimumTokenMatch));
        frequencyDetermination.buildFrequencyMap(result.getAllComparisons());
        MatchWeightCalculator matchWeighting = new MatchWeightCalculator(options.frequencyStrategy().getStrategy(),
                frequencyDetermination.getMatchFrequencyMap());
        MatchFrequency matchFrequency = matchWeighting.weightAllComparisons(result.getAllComparisons());
        MatchFrequencyWeighting similarity = new MatchFrequencyWeighting(result.getAllComparisons(), options.weightingStrategy(), matchFrequency);
        return result.getAllComparisons().stream().map(comparison -> similarity.weightedComparisonSimilarity(comparison, options.weightingFactor()))
                .toList();
    }

    private static boolean isFrequencyAnalysisEnabled(double weightingFactor) {
        return weightingFactor != -1;
    }

}
