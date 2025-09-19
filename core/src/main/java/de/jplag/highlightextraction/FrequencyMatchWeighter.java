package de.jplag.highlightextraction;

import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.options.JPlagOptions;

/**
 * Contains the logic of the frequency based weighting of the Matches in all Comparisons, influencing the similarity
 * between two comparisons according to the FrequencyStrategy and Similarity strategy. isFrequencyAnalysisEnabled =
 * false would use the old similarity, same as a weighting factor of 0.
 */
public class FrequencyMatchWeighter {
    /**
     * @param options JPlagOptions
     * @param result JPlagResult
     * @return the new Comparisons with a weighted similarity.
     */
    public List<JPlagComparison> useMatchFrequencyToInfluenceSimilarity(JPlagOptions options, JPlagResult result) {
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(options.frequencyAnalysisStrategy().getStrategy(),
                Math.max(options.frequencyStrategyMinValue(), options.minimumTokenMatch()));
        frequencyDetermination.buildFrequencyMap(result.getAllComparisons());
        MatchWeightCalculator matchWeighting = new MatchWeightCalculator(options.frequencyAnalysisStrategy().getStrategy(),
                frequencyDetermination.getMatchFrequencyMap());
        MatchFrequency matchFrequency = matchWeighting.weightAllComparisons(result.getAllComparisons());
        MatchFrequencyWeighting similarity = new MatchFrequencyWeighting(result.getAllComparisons(), options.weightingStrategy(), matchFrequency);
        return result.getAllComparisons().stream().map(comparison -> similarity.weightedComparisonSimilarity(comparison,
                options.weightingStrategyWeightingFactor(), options.isFrequencyAnalysisEnabled())).toList();
    }

}
