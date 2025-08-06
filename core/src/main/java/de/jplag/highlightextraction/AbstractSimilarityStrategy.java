package de.jplag.highlightextraction;

import de.jplag.JPlagComparison;

public class AbstractSimilarityStrategy {
    public abstract double calculateSimilarity(int minWeight, int maxWeight, double rarity);
}

