package de.jplag.highlightextraction;

import java.util.List;

import de.jplag.JPlagComparison;

public class FrequencyMatchWeighter {
    public void useMatchFrequencyToInfluenceSimilarity(FrequencyStrategy strategy, int frequencyStrategyMinValue, int minimumTokenMatch,
            List<JPlagComparison> allComparisons, SimilarityStrategy similarityStrategy, double weightingStrategyWeightingFactor, boolean frequency) {
        FrequencyDetermination frequencyDetermination = new FrequencyDetermination(strategy, Math.max(frequencyStrategyMinValue, minimumTokenMatch));
        frequencyDetermination.buildFrequencyMap(allComparisons);
        MatchWeighting matchWeighting = new MatchWeighting(strategy, frequencyDetermination.getMatchFrequencyMap());
        MatchFrequency matchFrequency = matchWeighting.weightAllComparisons(allComparisons);
        FrequencySimilarity similarity = new FrequencySimilarity(allComparisons, similarityStrategy, matchFrequency);
        for (JPlagComparison comparison : allComparisons) {
            double score = similarity.weightedComparisonSimilarity(comparison, weightingStrategyWeightingFactor);
            JPlagComparison.setFrequencyWeightedScore(score);
            JPlagComparison.setFrequency(frequency);
        }
    }

}
