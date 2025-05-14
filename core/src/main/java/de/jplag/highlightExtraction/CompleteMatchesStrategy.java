package de.jplag.highlightExtraction;

import java.util.List;
import java.util.Map;

public class CompleteMatchesStrategy implements FrequencyStrategy {
    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int param) {
        String key = String.join(" ", tokens);
        map.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(comparisonId);
    }

    @Override
    public void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int param) {
        String key = String.join(" ", tokens);
        if (!(map.containsKey(key) && map.get(key).contains(comparisonId))) {
            map.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(comparisonId);
        }
    }
}
