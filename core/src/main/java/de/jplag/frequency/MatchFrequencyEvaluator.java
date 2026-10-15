package de.jplag.frequency;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     * @return the weights of the matches
     */
    Map<List<TokenType>, Double> calculateMatchWeights() {
        return strategy.getAllMatches().parallelStream().collect(Collectors.toConcurrentMap(Function.identity(), strategy::calculateMatchCount));
    }

}
