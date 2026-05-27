package de.jplag.highlightextraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.TokenType;
import de.jplag.highlightextraction.strategy.FrequencyStrategy;

/**
 * Calculates weights of the matches and writes them into a map.
 */
class MatchWeightCalculator {
    private final FrequencyStrategy strategy;

    /**
     * Constructor defining the used weighting strategy.
     * @param strategy is the strategy used to determine the frequency of a match
     */
    public MatchWeightCalculator(FrequencyStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Calculates the weight of each match.
     * @param comparisons list of comparisons to weight
     * @return the weights of the matches
     */
    Map<List<TokenType>, Double> weightAllComparisons(List<JPlagComparison> comparisons) {
        Map<List<TokenType>, Double> matchWeights = new HashMap<>();
        for (JPlagComparison comparison : comparisons) {
            weightAllMatches(comparison, matchWeights);
        }
        return matchWeights;
    }

    /**
     * Calculates the weights for all matches of the comparison.
     * @param comparison is the comparison
     * @return a map of the matches to their weights
     */
    Map<List<TokenType>, Double> weightAllMatches(JPlagComparison comparison) {
        return weightAllMatches(comparison, new HashMap<>());
    }

    private Map<List<TokenType>, Double> weightAllMatches(JPlagComparison comparison, Map<List<TokenType>, Double> matchWeights) {
        for (Match match : comparison.matches()) {
            List<TokenType> matchTokens = TokenSequenceUtil.tokenTypesFor(comparison, match);
            matchWeights.computeIfAbsent(matchTokens, strategy::calculateMatchCount);
        }

        return matchWeights;
    }

}
