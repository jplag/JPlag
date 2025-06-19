package de.jplag.highlight_extraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StrategyMethods {
    public static String createKey(List<String> tokens) {
        return String.join(" ", tokens);
    }

    /**
     * Creates the submatches to build the keys.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param tokens Token list of the match.
     * @param size Minimum length of the considered submatches.
     */

    static void applyWindowCreate(Map<String, List<String>> map, List<String> tokens, int size) {
        LinkedList<String> copy = new LinkedList<>(tokens);
        while (copy.size() >= size) {
            List<String> subList = copy.subList(0, size);
            String key = createKey(subList);
            addToMap(map, key);
            copy.removeFirst();
        }
    }

    /**
     * Builds the map by adding token subsequences from a match.
     * @param map Map that contains token subsequences and how often they occur across comparisons.
     * @param key Token list of the match as string.
     */

    static void addToMap(Map<String, List<String>> map, String key) {
        map.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public static List<String> generateAllSubKeys(List<String> tokens, int minSize) {
        List<String> keys = new ArrayList<>();
        for (int size = minSize; size <= tokens.size(); size++) {
            LinkedList<String> copy = new LinkedList<>(tokens);
            while (copy.size() >= size) {
                List<String> subList = copy.subList(0, size);
                keys.add(createKey(subList));
                copy.removeFirst();
            }
        }
        return keys;
    }

}
