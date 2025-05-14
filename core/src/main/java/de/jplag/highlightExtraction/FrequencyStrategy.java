package de.jplag.highlightExtraction;

import java.util.List;
import java.util.Map;

public interface FrequencyStrategy {
    void create(List<String> tokens, String comparisonId, Map<String, List<String>> map, int param);
    void check(List<String> tokens, String comparisonId, Map<String, List<String>> map, int param);
}
