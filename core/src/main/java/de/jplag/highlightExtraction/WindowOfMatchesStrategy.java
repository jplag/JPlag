package de.jplag.highlightExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WindowOfMatchesStrategy implements FrequencyStrategy{
    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        for (int i = 0; i <= tokens.size() - size; i++) {
            List<String> window = tokens.subList(i, i + size);
            String key = String.join(" ", window);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(comparisonId);
        }
    }

    @Override
    public void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        for (int i = 0; i <= tokens.size() - size; i++) {
            List<String> window = tokens.subList(i, i + size);
            String key = String.join(" ", window);
            if (!(map.containsKey(key) && map.get(key).contains(comparisonId))) {
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(comparisonId);
            }
        }
    }
}
