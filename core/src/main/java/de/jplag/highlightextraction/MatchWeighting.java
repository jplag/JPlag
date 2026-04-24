package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.TokenType;
import de.jplag.highlightextraction.strategy.FrequencyStrategy;

/**
 * Contains the logic of the frequency based weighting of the Matches in all Comparisons, influencing the similarity
 * between two comparisons according to the FrequencyStrategy and Similarity strategy. isFrequencyAnalysisEnabled =
 * false would use the old similarity.
 */
public class MatchWeighting {

    private final FrequencyAnalysisOptions options;

    /**
     * Creates a new MatchWeighting object according to the given FrequencyAnalysisOptions.
     * @param options are the FrequencyAnalysisOptions
     */
    public MatchWeighting(FrequencyAnalysisOptions options) {
        this.options = options;
    }

    /**
     * @param result JPlagResult
     * @return the new Comparisons with a weighted similarity.
     */
    public List<JPlagComparison> useMatchFrequencyToInfluenceSimilarity(JPlagResult result) {
        List<JPlagComparison> comparisons = result.getAllComparisons();
        // count match occurrences
        FrequencyStrategy frequencyStrategy = options.analysisStrategy();
        frequencyStrategy.processMatches(comparisons);
        MatchWeightCalculator matchWeighting = new MatchWeightCalculator(frequencyStrategy);
        Map<List<TokenType>, Double> matchFrequency = matchWeighting.weightAllComparisons(comparisons);
        MatchFrequencyWeighting similarity = new MatchFrequencyWeighting(comparisons, options.weightingFunction(), matchFrequency);
        return comparisons.stream().map(comparison -> similarity.weightedComparisonSimilarity(comparison, options.weightingFactor())).toList();
    }

}
