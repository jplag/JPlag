package de.jplag.highlightextraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.*;

/**
 * Calculates frequencies of match subsequences across all comparisons according to different strategies.
 */
public class FrequencyDetermination {
    private final Map<Integer, Integer> matchFrequencyMap = new HashMap<>();
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
    public Map<Integer, Integer> buildFrequencyMap(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            Submission leftSubmission = comparison.firstSubmission();
            List<Token> submissionTokens = leftSubmission.getTokenList();
            List<TokenType> submissionTokenTypes = new ArrayList<>();
            for (Token token : submissionTokens) {
                submissionTokenTypes.add(token.getType());
            }

            for (Match match : comparison.matches()) {
                int start = match.startOfFirst();
                int len = match.lengthOfFirst();
                if (start + len > submissionTokenTypes.size())
                    continue;

                List<TokenType> matchTokenTypes = new ArrayList<>();
                for (int i = start; i < start + len; i++) {
                    matchTokenTypes.add(submissionTokenTypes.get(i));
                }
                frequencyStrategy.addMatchToFrequencyMap(matchTokenTypes, matchFrequencyMap, strategyNumber);
            }
        }
        return matchFrequencyMap;
    }

    /**
     * @return Map containing (sub-)matches and their frequency according to the strategy.
     */
    public Map<Integer, Integer> getMatchFrequencyMap() {
        return matchFrequencyMap;
    }
}