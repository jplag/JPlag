package de.jplag.highlight_extraction;

import static de.jplag.highlight_extraction.StrategyMethods.createSubKeys;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Strategy that uses submatches from the comparisons and calculates the frequency of their appearance in matches across
 * all submissions.
 */

public class ContainedStrategy implements FrequencyStrategy {
     /**
     * Adds all submatches with min size length of the matches to a map using the token sequence as the key.
     *
     * @param tokens Token list of the match.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param size Minimum length of the considered submatches.
     */
    @Override
    public void createFrequencymap(List<TokenType> tokens, Map<List<TokenType>, Integer> map, int size) {
        List<List<TokenType>> newKeys = createSubKeys(tokens, size);
        for (List<TokenType> subKey : newKeys) {
            map.putIfAbsent(subKey, 0);
        }
        if (tokens.size() >= size) {
            map.put(tokens, map.getOrDefault(tokens, 0) + 1);
        }
    }



}
