package de.jplag.highlightextraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.TokenType;

public record MatchFrequency(Map<List<TokenType>, Double> matchFrequencyMap) {
    public MatchFrequency() {
        this(new HashMap<>());
    }
}
