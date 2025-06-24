package de.jplag.highlight_extraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.jplag.TokenType;

public record StrategyMethods() {

    /**
     * Calculates all possible Sublists with min size length of the Match tokens
     * @param tokens tokens Of the Match
     * @param size minimum considered size of the Sublist
     * @return List of all as considered Sublists
     */
    public static List<List<TokenType>> createSubKeys(List<TokenType> tokens, int size) {
        List<List<TokenType>> newKeys = new LinkedList<>();

        if (tokens.size() >= size) {
            for (int windowSize = size; windowSize <= tokens.size(); windowSize++) {
                for (int i = 0; i <= tokens.size() - windowSize; i++) {
                    List<TokenType> subList = new ArrayList<>(tokens.subList(i, i + windowSize));
                    newKeys.add(subList);
                }
            }
        }

        return newKeys;
    }
}
