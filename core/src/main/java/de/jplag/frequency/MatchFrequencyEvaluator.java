package de.jplag.frequency;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.TokenType;
import de.jplag.frequency.strategy.FrequencyAnalysisStrategy;

/**
 * Calculates weights of the matches and writes them into a map.
 */
class MatchFrequencyEvaluator {
    private final FrequencyAnalysisStrategy strategy;

    /**
     * Constructor defining the used weighting strategy.
     * @param strategy is the strategy used to determine the frequency of a match
     */
    public MatchFrequencyEvaluator(FrequencyAnalysisStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Calculates the weight of each match.
     * @param comparisons list of comparisons to weight
     * @return the weights of the matches
     */
    Map<List<TokenType>, Double> weightAllComparisons(List<JPlagComparison> comparisons) {
        Map<List<TokenType>, Double> matchWeights = new ConcurrentHashMap<>();
        comparisons.parallelStream().forEach(comparison -> weightAllMatches(comparison, matchWeights));
        return matchWeights;
    }

    private void weightAllMatches(JPlagComparison comparison, Map<List<TokenType>, Double> matchWeights) {
        for (Match match : comparison.matches()) {
            List<TokenType> matchTokens = FrequencyUtil.tokenTypesFor(comparison, match);
            matchWeights.computeIfAbsent(matchTokens, strategy::calculateMatchCount);
        }
    }

}
