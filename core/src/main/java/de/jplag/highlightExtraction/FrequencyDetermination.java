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
    private int magicMinSubSize = 4;

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
                subMatchesCreateStrategy(magicMinSubSize);
                break;
            case windowOfMatches:
                windowOfMatchesCreateStrategy(magicWindowSize);
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
                subMatchesCheckStrategy(magicMinSubSize);
                break;
            case windowOfMatches:
                windowOfMatchesCheckStrategy(magicWindowSize);
                break;
            default:
                throw new IllegalArgumentException("unknown Strategy: " + strategy);
        }
    }

    private void windowOfMatchesCreateStrategy(int size) {
        while (myMatchTokens.size() >= size) {
            List<String> mySubListKey = myMatchTokens.subList(0, size);
            buildFrequencyMap(mySubListKey);
            myMatchTokens.removeFirst();
        }
    }

    private void windowOfMatchesCheckStrategy(int size) {
        while (myMatchTokens.size() >= size) {
            List<String> mySubListKey = myMatchTokens.subList(0, size);
            checkFrequencyMap(mySubListKey);
            myMatchTokens.removeFirst();
        }
    }

    private void subMatchesCheckStrategy(int size) {
        if (myMatchTokens.size() >= size) {
            for (int i = size; i <= myMatchTokens.size(); i++) {
                windowOfMatchesCheckStrategy(size);
            }
        }
    }

    private void subMatchesCreateStrategy(int size) {
        if (myMatchTokens.size() >= size) {
            for (int i = size; i < myMatchTokens.size(); i++) {
                windowOfMatchesCreateStrategy(size);
            }
        }
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
        if (tokenFrequencyMap.containsKey(myTokenKey) && !tokenFrequencyMap.get(myTokenKey).contains(myComparisonIdentifier)) {
            tokenFrequencyMap.computeIfAbsent(myTokenKey, k -> new ArrayList<>()).add(myComparisonIdentifier);
        }
    }


    public Map<String, List<String>> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }
}