package de.jplag.highlightExtraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ContainedStrategy implements FrequencyStrategy{
    @Override
    public void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        if (tokens.size() >= size) {
            for (int j = size; j <= tokens.size(); j++) {
                applyWindowCreate(map, comparisonId, tokens, j);
            }
        }
        addToMap(map, comparisonId, tokens);
    }

    @Override
    public void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int size) {
        String key = String.join(" ", tokens);
        if (!(map.containsKey(key) && map.get(key).contains(comparisonId))) {
            map.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(comparisonId);
        }
    }
    private void applyWindowCreate(Map<String, List<String>> map,
                                   String comparisonId,
                                   List<String> tokens,
                                   int size) {
        LinkedList<String> copy = new LinkedList<>(tokens);
        while (copy.size() >= size) {
            List<String> subList = copy.subList(0, size);
            addToMap(map, comparisonId, subList);
            copy.removeFirst();
        }
    }

    private void addToMap(Map<String, List<String>> map,
                         String comparisonId,
                         List<String> tokens) {
        String key = String.join(" ", tokens);
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(comparisonId);
    }
}

