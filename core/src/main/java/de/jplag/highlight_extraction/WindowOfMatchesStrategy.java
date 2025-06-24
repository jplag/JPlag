package de.jplag.highlight_extraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

/**
 * Strategy that uses a fixed window size to create submatches of a match sequence in a comparison and calculates the
 * frequencies over all submissions.
 */

public class WindowOfMatchesStrategy implements FrequencyStrategy {

     /**
     * Adds all submatches with window length of the matches to a map using the token sequence as the key.
     * @param tokens Token list of the match.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param size The length of the considered token window.
     */
    @Override
    public void createFrequencymap(List<TokenType> tokens, Map<List<TokenType>, Integer> map, int size) {
        List<List<TokenType>> newKeys = createWindowKeys(tokens, size);
        for (List<TokenType> subKey : newKeys) {
            map.put(subKey, map.getOrDefault(subKey, 0) + 1);
        }

    }

    /**
     * Calculates all possible Sublists with length of size
     * @param tokens tokens Of the Match
     * @param size considered size of the Sublists
     * @return List of all as considered Sublists
     */
    public static List<List<TokenType>> createWindowKeys(List<TokenType> tokens, int size) {
        List<List<TokenType>> newKeys = new LinkedList<>();
        if(tokens.size() >= size) {
            for(int i = 0; i <= tokens.size() - size; i++) {
                List<TokenType> windowList = new ArrayList<>(tokens.subList(i, i + size));
                newKeys.add(windowList);
            }
        }
        return newKeys;
    }
}
