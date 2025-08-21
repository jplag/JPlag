package de.jplag.highlightextraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.jplag.Match;
import de.jplag.TokenType;

/**
 * Calculates frequency values and writes them into the matches.
 */
public class MatchWeighting {
    private final FrequencyStrategy strategy;
    private final Map<List<TokenType>, Integer> frequencyMap;

    /**
     * Constructor defining the used frequency strategy and frequency map.
     */
    public MatchWeighting(FrequencyStrategy strategy, Map<List<TokenType>, Integer> frequencyMap) {
        this.strategy = strategy;
        this.frequencyMap = frequencyMap;
    }

    /**
     * Calculates frequency value for a match.
     */
    public void weightMatch(Match match, List<TokenType> matchToken) {
        double matchWeight = strategy.calculateWeight(match, frequencyMap, matchToken);
        match.setFrequencyWeight(matchWeight);
    }

    /**
     * Calculates frequency value for all matches.
     */
    public void weightAllMatches(List<Match> matches, List<TokenType> comparisonToken) {
        for (Match match : matches) {
            int start = match.getStartOfFirst();
            int length = match.getLengthOfFirst();
            if (start + length > comparisonToken.size())
                continue;

            List<TokenType> matchTokens = new ArrayList<>();
            for (int i = start; i <= start + length; i++) {
                matchTokens.add(comparisonToken.get(i));
            }
            weightMatch(match, matchTokens);
        }
    }
}
