package de.jplag.highlight_extraction;

import java.util.ArrayList;
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
        if (matchWeight == 0) {
            System.out.println("Achtung null");
            match.setFrequencyWeight(matchWeight);
        }
    }

    public void weightAllMatches(List<Match> matches, List<String> comparisonToken) {
        for (Match match : matches) {
            int start = match.getStartOfFirst();
            int len = match.getLengthOfFirst();
            if (start + len > comparisonToken.size())
                continue;

            List<String> matchTokens = new ArrayList<>();
            for (int i = start; i < start + len; i++) {
                matchTokens.add(comparisonToken.get(i).toString());
            }
            //List<String> matchtoken = comparisonToken.subList(match.getStartOfFirst(), match.endOfFirst()); //todo -1 weg !!!!!!!!!!!!!!!!

            weightMatch(match, matchTokens);
        }
    }
}
