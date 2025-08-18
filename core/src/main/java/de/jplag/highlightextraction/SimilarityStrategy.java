package de.jplag.highlightextraction;

import de.jplag.JPlagComparison;

public interface SimilarityStrategy {
    double computeWeight(double minWeight, double maxWeight, double rarity);
}

