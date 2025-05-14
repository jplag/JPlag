package de.jplag.highlightExtraction;

import java.util.List;
import java.util.Map;

public interface FrequencyBuilder {
    void build(List<String> tokens, String comparisonId,
               Map<String, List<String>> frequencyMap, int strategyParam);
}
