package de.jplag.highlight_extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.*;

/**
 * Calculates frequencies of match subsequences across all comparisons according to different strategies.
 */
public class FrequencyDetermination {
    private Map<List<TokenType>, Integer> tokenFrequencyMap = new HashMap<>();
    private final FrequencyStrategy frequencyStrategy;
    private final int strategyNumber;

    /**
     * Constructor.
     * @param frequencyStrategy The chosen strategy for frequency calculation.
     * @param strategyNumber Parameter used by certain strategies to determine submatch length.
     */
    public FrequencyDetermination(FrequencyStrategy frequencyStrategy, int strategyNumber) {
        this.frequencyStrategy = frequencyStrategy;
        this.strategyNumber = strategyNumber;
    }

    /**
     * Applies the "create" and "check" methods of the frequency strategy.
     * @param comparisons contains information of matches between two submissions.
     * @return Map containing (sub-)matches and their frequency according to the strategy.
     */
    public Map<List<TokenType>, Integer> runAnalysis(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            Submission left = comparison.firstSubmission();
            List<Token> tokensNames = left.getTokenList();
            List<TokenType> tokens = new ArrayList<>();
            for (Token token : tokensNames) {
                tokens.add(token.getType());
            }

            for (Match match : comparison.matches()) {
                int start = match.startOfFirst();
                int len = match.lengthOfFirst();
                if (start + len > tokens.size())
                    continue;

                List<TokenType> matchTokens = new ArrayList<>();
                for (int i = start; i < start + len; i++) {
                    matchTokens.add(tokens.get(i));
                }
                frequencyStrategy.createFrequencymap(matchTokens, tokenFrequencyMap, strategyNumber);
            }
        }
        return tokenFrequencyMap;
    }

    /**
     * @return Map containing (sub-)matches and their frequency according to the strategy.
     */
    public Map<List<TokenType>, Integer> getTokenFrequencyMap() {
        return tokenFrequencyMap;
    }
}