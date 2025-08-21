package de.jplag.highlightextraction;

/**
 * Strategy that considers rare matches stronger using a linear weighting function.
 */
public class RareTokensWeightedStrategy implements SimilarityStrategy {
    public double computeWeight(double minWeight, double maxWeight, double rarity) {
        return minWeight + (maxWeight - minWeight) * rarity;
    }
}
