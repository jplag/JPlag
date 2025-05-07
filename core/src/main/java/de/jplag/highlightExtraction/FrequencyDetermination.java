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
    private int strategyNumber;

    /**
     * @param comparisons the comparisons result containing the match infos between two submissions
     * @param freqencyStrategy the strategy that will be used to create and check the matches between each other
     * @param strategyNumber numer that ist used for shortest considered submatches
     * @return the build frequencyMap
     */
    public Map<String, List<String>> frequencyAnalysisStrategies(List<JPlagComparison> comparisons, FrequencyStrategies freqencyStrategy, int strategyNumber) {
        this.comparisons = comparisons;
        this.strategyNumber = strategyNumber;
        this.freqencyStrategy = freqencyStrategy;
        frequencyAnalysis(FrequencyDeterminationState.CREATE);
        frequencyAnalysis(FrequencyDeterminationState.CHECK);

        return tokenFrequencyMap;
    }

    /**
     * @param state differs between create and check phase
     */
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
                    applyCreateStrategy(freqencyStrategy); //hilfsmethode
                } else {
                    applyCheckStrategy(freqencyStrategy);
                }
            }
        }
    }

    /**
     * @param strategy the used building strategy
     */
    public void applyCreateStrategy(FrequencyStrategies strategy) {
        switch (strategy){
            case COMPLETEMATCHES:
                completeMatchesCreateStrategy();
                break;
            case CONTAINEDMATCHES:
            case SUBMATCHES:
                subMatchesCreateStrategy(strategyNumber);
                break;
            case WINDOWOFMATCHES:
                windowOfMatchesCreateStrategy(strategyNumber);
                break;
            default:
                throw new IllegalArgumentException("unknown Strategy: " + strategy);

        }
    }

    /**
     * @param strategy the used checking strategy
     */
    public void applyCheckStrategy(FrequencyStrategies strategy) {
        switch (strategy){
            case COMPLETEMATCHES:
            case CONTAINEDMATCHES: //klassenbasiert -> strategie
                completeMatchesCheckStrategy();
                break;
            case SUBMATCHES:
                subMatchesCheckStrategy(strategyNumber);
                break;
            case WINDOWOFMATCHES:
                windowOfMatchesCheckStrategy(strategyNumber);
                break;
            default:
                throw new IllegalArgumentException("unknown Strategy: " + strategy);
        }
    }

    /**
     * @param size the considered windows size
     */
    private void windowOfMatchesCreateStrategy(int size) {
        List<String> myMatchTokenCopy = new ArrayList<>(myMatchTokens);
        while (myMatchTokenCopy.size() >= size) {
            List<String> mySubListKey = myMatchTokenCopy.subList(0, size);
            buildFrequencyMap(mySubListKey);
            myMatchTokenCopy.removeFirst();
        }
    } 

    /**
     * @param size the considered windows size
     */
    private void windowOfMatchesCheckStrategy(int size) {
        List<String> myMatchTokenCopy = new ArrayList<>(myMatchTokens);
        while (myMatchTokenCopy.size() >= size) {
            List<String> mySubListKey = myMatchTokenCopy.subList(0, size);
            checkFrequencyMap(mySubListKey);
            myMatchTokenCopy.removeFirst();
        }
    }

    /**
     * @param subSize the minimum considered Sub-Match size
     */
    private void subMatchesCheckStrategy(int subSize) {
        if (myMatchTokens.size() >= subSize) {
            for (int i = subSize; i <= myMatchTokens.size(); i++) {
                windowOfMatchesCheckStrategy(i);
            }
        }
    }

    /**
     * @param subSize the minimum considered Sub-Match size
     */
    private void subMatchesCreateStrategy(int subSize) {
        if (myMatchTokens.size() >= subSize) {
            for (int j = subSize; j <= myMatchTokens.size(); j++) {
                windowOfMatchesCreateStrategy(j);
            }
        }
        buildFrequencyMap(myMatchTokens);
    }

    /**
     * builds Hashmap based on the matches
     */
    public void completeMatchesCreateStrategy(){
        buildFrequencyMap(myMatchTokens);

    }

    /**
     * counts frequency of all matches
     */
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
        if (!(tokenFrequencyMap.containsKey(myTokenKey) && tokenFrequencyMap.get(myTokenKey).contains(myComparisonIdentifier))){
            tokenFrequencyMap.computeIfAbsent(myTokenKey, k -> new ArrayList<>()).add(myComparisonIdentifier);
        }

    }

    public Map<String, List<String>> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }
}