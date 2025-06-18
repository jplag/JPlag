package de.jplag.highlight_extraction;

import java.util.List;
import java.util.Map;

import de.jplag.Match;

public class MatchWeighting {
    private final FrequencyStrategy strategy;
    private final Map<String, List<String>> frequencyMap;

    public MatchWeighting(FrequencyStrategy strategy, Map<String, List<String>> frequencyMap) {
        this.strategy = strategy;
        this.frequencyMap = frequencyMap;
    }

    public void weightMatch(Match match, List<String> matchToken) {
        double matchWeight = strategy.calculateWeight(match, frequencyMap, matchToken);
        match.setFrequencyWeight(matchWeight);
    }

    public void weightAllMatches(List<Match> matches, List<String> comparisonToken) {
        for (Match match : matches) {
            List<String> matchtoken = comparisonToken.subList(match.getStartOfFirst(), match.endOfFirst());
            weightMatch(match, matchtoken);
        }
    }
}
