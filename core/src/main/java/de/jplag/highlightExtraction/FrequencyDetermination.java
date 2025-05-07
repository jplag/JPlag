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
    private int magicWindowSize = 180;
    private int magicMinSubSize = 300;

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
        List<String> myMatchTokenCopy = new ArrayList<>(myMatchTokens);
        while (myMatchTokenCopy.size() >= size) {
            List<String> mySubListKey = myMatchTokenCopy.subList(0, size);
            buildFrequencyMap(mySubListKey);
            myMatchTokenCopy.removeFirst();
        }
    }

    private void windowOfMatchesCheckStrategy(int size) {
        List<String> myMatchTokenCopy = new ArrayList<>(myMatchTokens);
        while (myMatchTokenCopy.size() >= size) {
            List<String> mySubListKey = myMatchTokenCopy.subList(0, size);
            checkFrequencyMap(mySubListKey);
            myMatchTokenCopy.removeFirst();
        }
    }

    private void subMatchesCheckStrategy(int subSize) {
        if (myMatchTokens.size() >= subSize) {
            for (int i = subSize; i <= myMatchTokens.size(); i++) {
                windowOfMatchesCheckStrategy(i);
            }
        }
    }

    private void subMatchesCreateStrategy(int subSize) {
        if (myMatchTokens.size() >= subSize) {
            for (int j = subSize; j <= myMatchTokens.size(); j++) {
                windowOfMatchesCreateStrategy(j);
            }
        }
        buildFrequencyMap(myMatchTokens);
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
        tokenFrequencyMap.computeIfAbsent(myTokenKey, k -> new ArrayList<>()).add(myComparisonIdentifier);
    }


    public Map<String, List<String>> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }
}