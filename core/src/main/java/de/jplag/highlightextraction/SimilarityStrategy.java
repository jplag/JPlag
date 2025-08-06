package de.jplag.highlightextraction;

import de.jplag.JPlagComparison;

public interface SimilarityStrategy {
    double calculateSimilarity(int minWeight, int maxWeight, double rarity);
}

