package de.jplag.highlightextraction;

import java.util.List;

import de.jplag.JPlagComparison;

/**
 * Contains the logic of the frequency based weighting of the Matches in all Comparisons, influencing the similarity
 * between two comparisons according to the FrequencyStrategy and Similarity strategy. frequencyUsed = false would use
 * the old similarity, same as a weighting factor of 0.
 */
public class FrequencyMatchWeighter {
    /**
     * @param strategy the Frequency Strategy chosen to determine the frequency of a match.
     * @param frequencyStrategyMinValue the minimum considered subsequence size.
     * @param minimumTokenMatch minimum size of a match.
     * @param allComparisons all comparisons.
     * @param similarityStrategy the chosen Similarity strategy, determining how the match frequency influences the
     * similarity value.
     * @param weightingStrategyWeightingFactor determines what the maximum influence on the similarity can be.
     * @param frequencyUsed if false frequency of a match will not be considered.
     * @return the new Comparisons with a weighted similarity.
     */
    public List<JPlagComparison> useMatchFrequencyToInfluenceSimilarity(FrequencyStrategy strategy, int frequencyStrategyMinValue,
            int minimumTokenMatch, List<JPlagComparison> allComparisons, SimilarityStrategy similarityStrategy,
            double weightingStrategyWeightingFactor, boolean frequencyUsed) {
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(strategy, Math.max(frequencyStrategyMinValue, minimumTokenMatch));
        frequencyDetermination.buildFrequencyMap(allComparisons);
        MatchWeighting matchWeighting = new MatchWeighting(strategy, frequencyDetermination.getMatchFrequencyMap());
        MatchFrequency matchFrequency = matchWeighting.weightAllComparisons(allComparisons);
        FrequencySimilarity similarity = new FrequencySimilarity(allComparisons, similarityStrategy, matchFrequency);
        return allComparisons.stream()
                .map(comparison -> similarity.weightedComparisonSimilarity(comparison, weightingStrategyWeightingFactor, frequencyUsed)).toList();
    }

}
