package de.jplag.highlightextraction;

public interface SimilarityStrategy {
    double computeWeight(double minWeight, double maxWeight, double rarity);
}
