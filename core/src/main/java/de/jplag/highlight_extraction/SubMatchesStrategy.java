package de.jplag.highlight_extraction;

import static de.jplag.highlight_extraction.StrategyMethods.createSubKeys;

import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Strategy that uses submatches of matches from the comparisons and calculates their frequency in the matches across
 * all submissions.
 */
public class SubMatchesStrategy implements FrequencyStrategy {

     /**
     * Creates submatches to build the keys and adds their frequencies to the map.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param tokens List of tokens representing the match.
     * @param size Minimum length of the considered submatches.
     */
    @Override
    public void createFrequencymap(List<TokenType> tokens, Map<List<TokenType>, Integer> map, int size) {
        List<List<TokenType>> newKeys = createSubKeys(tokens, size);
        for (List<TokenType> subKey : newKeys) {
            map.put(subKey, map.getOrDefault(subKey, 0) + 1);
        }

    }
}
