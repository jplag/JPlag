package de.jplag.highlightExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.*;

public class FrequencyDetermination {
    private Map<String, List<String>> tokenFrequencyMap = new HashMap<>();
    private List<JPlagComparison> comparisons;
    private FrequencyStrategy freqencyStrategy;
    private int strategyNumber;



    public FrequencyDetermination(FrequencyStrategy freqencyStrategy, int strategyNumber) {
        this.freqencyStrategy = freqencyStrategy;
        this.strategyNumber = strategyNumber;
    }


    public Map<String, List<String>> runAnalysis(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            Submission left = comparison.firstSubmission();
            List<Token> tokens = left.getTokenList();
            String comparisonId = comparison.toString();

            for (Match match : comparison.matches()) {
                int start = match.startOfFirst();
                int len = match.length();
                if (start + len > tokens.size()) continue;

                List<String> matchTokens = new ArrayList<>();
                for (int i = start; i < start + len; i++) {
                    matchTokens.add(tokens.get(i).toString());
                }

                freqencyStrategy.create(matchTokens, comparisonId, tokenFrequencyMap, strategyNumber);
            }
        }

        for (JPlagComparison comparison : comparisons) {
            Submission left = comparison.firstSubmission();
            List<Token> tokens = left.getTokenList();
            String comparisonId = comparison.toString();

            for (Match match : comparison.matches()) {
                int start = match.startOfFirst();
                int len = match.length();
                if (start + len > tokens.size()) continue;

                List<String> matchTokens = new ArrayList<>();
                for (int i = start; i < start + len; i++) {
                    matchTokens.add(tokens.get(i).toString());
                }

                freqencyStrategy.check(matchTokens, comparisonId, tokenFrequencyMap, strategyNumber);
            }
        }

        return tokenFrequencyMap;
    }

    public Map<String, List<String>> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }
}