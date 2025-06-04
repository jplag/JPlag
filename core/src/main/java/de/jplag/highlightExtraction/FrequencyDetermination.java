package de.jplag.highlightExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.*;

/**
 * Calculates frequencies of match subsequences across all comparisons according to different strategies.
 */

public class FrequencyDetermination {
    private Map<String, List<String>> tokenFrequencyMap = new HashMap<>();
    private FrequencyStrategy freqencyStrategy;
    private int strategyNumber;


    /**
     * Constructor.
     *
     * @param frequencyStrategy The chosen strategy for frequency calculation.
     * @param strategyNumber    Parameter used by certain strategies to determine submatch length.
     */
    public FrequencyDetermination(FrequencyStrategy frequencyStrategy, int strategyNumber) {
        this.freqencyStrategy = frequencyStrategy;
        this.strategyNumber = strategyNumber;
    }


    /**
     * Applies the "create" and "check" methods of the frequency strategy.
     * @param comparisons contains information of matches between two submissions.
     * @return Map containing (sub-)matches and their frequency according to the strategy.
     */
    public Map<String, List<String>> runAnalysis(List<JPlagComparison> comparisons) {
        frequencyBuilder(comparisons, freqencyStrategy::create);
        frequencyBuilder(comparisons, freqencyStrategy::check);
        return tokenFrequencyMap;
    }

    /**
     * Builds the frequency Map.
     * @param comparisons contains information of matches between two submissions.
     * @param builder builds and updates the frequency map with submatches according to the implemented strategy.
     */
    private void frequencyBuilder(List<JPlagComparison> comparisons,
                                  FrequencyBuilder builder) {
        for (JPlagComparison comparison : comparisons) {
            Submission left = comparison.firstSubmission();
            List<Token> tokensNames = left.getTokenList();
            List<TokenType> tokens = new ArrayList<>();
            for (Token token : tokensNames) {
                tokens.add(token.getType());
            }
            String comparisonId = comparison.toString();

            for (Match match : comparison.matches()) {
                int start = match.startOfFirst();
                int len = match.length();
                if (start + len > tokens.size()) continue;

                List<String> matchTokens = new ArrayList<>();
                for (int i = start; i < start + len; i++) {
                    matchTokens.add(tokens.get(i).toString());
                }

                builder.build(matchTokens, comparisonId, tokenFrequencyMap, strategyNumber);
            }
        }
    }

    /**
     * @return Map containing (sub-)matches and their frequency according to the strategy.
     */
    public Map<String, List<String>> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }
}