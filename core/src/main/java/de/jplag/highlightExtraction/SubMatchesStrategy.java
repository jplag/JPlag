package de.jplag.highlightExtraction;

import de.jplag.JPlagComparison;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SubMatchesStrategy implements FrequencyStrategy{
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
        if (tokens.size() >= size) {
            for (int i = size; i <= tokens.size(); i++) {
                applyWindowCheck(map, comparisonId, tokens, i);
            }
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

    private void applyWindowCheck(Map<String, List<String>> map,
                                  String comparisonId,
                                  List<String> tokens,
                                  int size) {
        LinkedList<String> copy = new LinkedList<>(tokens);
        while (copy.size() >= size) {
            List<String> subList = copy.subList(0, size);
            checkInMap(map, comparisonId, subList);
            copy.removeFirst();
        }
    }

    private void addToMap(Map<String, List<String>> map,
                          String comparisonId,
                          List<String> tokens) {
        String key = String.join(" ", tokens);
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(comparisonId);
    }

    private void checkInMap(Map<String, List<String>> map,
                            String comparisonId,
                            List<String> tokens) {
        String key = String.join(" ", tokens);
        List<String> existing = map.get(key);
        if (existing == null || existing.contains(comparisonId)) {
            return;
        }
        existing.add(comparisonId);
    }
}
