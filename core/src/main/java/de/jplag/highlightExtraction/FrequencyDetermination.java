package de.jplag.highlightExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.*;


public class FrequencyDetermination {
    private Map<String, List<String>> tokenFrequencyMap = new HashMap<>();
    private String myComparisonIdentifier;
    private List<String> myMatchTokens;
    private FrequencyDeterminationState state;
    private List<JPlagComparison> comparisons;
    private FrequencyStrategies freqencyStrategy;

    public Map<String, List<String>> frequencyAnalysisStrategies(List<JPlagComparison> comparisons, FrequencyStrategies freqencyStrategy) {
        this.comparisons = comparisons;
        this.freqencyStrategy = freqencyStrategy;
        frequencyAnalysis(FrequencyDeterminationState.CREATE);
        frequencyAnalysis(FrequencyDeterminationState.CHECK);

        return tokenFrequencyMap;
    }

    public void frequencyAnalysis(FrequencyDeterminationState state) {
        for (JPlagComparison myComparison : comparisons) {
            Submission left = myComparison.firstSubmission();
            List<Token> leftTokens = left.getTokenList();

            for (Match match : myComparison.matches()) {
                int start = match.startOfFirst();
                int len = match.length();

                if (start + len > leftTokens.size()) continue;

                myMatchTokens = new ArrayList<>();
                for (int i = start; i < start + len; i++) {
                    myMatchTokens.add(leftTokens.get(i).toString());
                }

                myComparisonIdentifier = myComparison.toString();

                if (state == FrequencyDeterminationState.CREATE) {
                    applyCreateStrategy(freqencyStrategy);
                } else {
                    applyCheckStrategy(freqencyStrategy);
                }
            }
        }
    }

    public void applyCreateStrategy(FrequencyStrategies strategy) {
        switch (strategy){
            case completeMatches:
                completeMatchesBuildStrategy();
                break;
            case containedMatches:
            case subMatches:
                subMatchesBuildStrategy();
                break;
            case windowOfMatches:
                windowOfMatchesBuildStrategy();
                break;
            default:
                throw new IllegalArgumentException("unknown Strategy: " + strategy);

        }
    }

    public void applyCheckStrategy(FrequencyStrategies strategy) {
        switch (strategy){
            case completeMatches:
            case containedMatches:
                completeMatchesCheckStrategy();
                break;
            case subMatches:
                subMatchesCheckStrategy();
                break;
            case windowOfMatches:
                windowOfMatchesCheckStrategy();
                break;
            default:
                throw new IllegalArgumentException("unknown Strategy: " + strategy);
        }
    }

    private void windowOfMatchesCheckStrategy() {
    }

    private void windowOfMatchesBuildStrategy() {
    }

    private void subMatchesCheckStrategy() {
    }

    private void subMatchesBuildStrategy() {
    }

    public void completeMatchesBuildStrategy(){
        String myTokenKey = String.join(" ", myMatchTokens);
        if (!tokenFrequencyMap.containsKey(myTokenKey)) {
            List<String> myComparisonIdentifierList = new ArrayList<>();
            myComparisonIdentifierList.add(myComparisonIdentifier);
            tokenFrequencyMap.put(myTokenKey, myComparisonIdentifierList);
        }

    }

    private void completeMatchesCheckStrategy() {
        String myTokenKey = String.join(" ", myMatchTokens);
        tokenFrequencyMap.computeIfAbsent(myTokenKey, k -> new ArrayList<>()).add(myComparisonIdentifier);
    }

    public Map<String, List<String>> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }

}
