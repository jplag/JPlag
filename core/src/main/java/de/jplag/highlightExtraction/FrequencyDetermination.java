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
    private int magicWindowSize = 5;

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
                completeMatchesCreateStrategy();
                break;
            case containedMatches:
            case subMatches:
                subMatchesCreateStrategy();
                break;
            case windowOfMatches:
                windowOfMatchesCreateStrategy();
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

    private void windowOfMatchesCreateStrategy() {
        while (myMatchTokens.size() >= magicWindowSize) {
            List<String> mySubListKey = myMatchTokens.subList(0, magicWindowSize);
            buildFrequencyMap(mySubListKey);
            myMatchTokens.removeFirst();
        }
    }

    private void windowOfMatchesCheckStrategy() {
        while (myMatchTokens.size() >= magicWindowSize) {
            List<String> mySubListKey = myMatchTokens.subList(0, magicWindowSize);
            checkFrequencyMap(mySubListKey);
            myMatchTokens.removeFirst();
        }
    }

    private void subMatchesCheckStrategy() {
    }

    private void subMatchesCreateStrategy() {
    }

    public void completeMatchesCreateStrategy(){
        buildFrequencyMap(myMatchTokens);

    }

    private void completeMatchesCheckStrategy() {
        checkFrequencyMap(myMatchTokens);
    }

    private void buildFrequencyMap(List<String> tokenList) {
        String myTokenKey = String.join(" ", tokenList);
        if (!tokenFrequencyMap.containsKey(myTokenKey)) {
            List<String> myComparisonIdentifierList = new ArrayList<>();
            myComparisonIdentifierList.add(myComparisonIdentifier);
            tokenFrequencyMap.put(myTokenKey, myComparisonIdentifierList);
        }
    }

    private void checkFrequencyMap(List<String> tokkenList) {
        String myTokenKey = String.join(" ", tokkenList);
        if (!tokenFrequencyMap.get(myTokenKey).contains(myComparisonIdentifier)) {
            tokenFrequencyMap.computeIfAbsent(myTokenKey, k -> new ArrayList<>()).add(myComparisonIdentifier);
        }
    }


    public Map<String, List<String>> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }

}
