package de.jplag.highlightextraction;

/**
 * Strategy that considers rare matches stronger using a sigmoid weighting function.
 */
public class SigmoidWeightingStrategy implements SimilarityStrategy {
    public double computeWeight(double minWeight, double maxWeight, double rarity) {
        double norm = 1.0 / (1.0 + Math.exp(-10 * (rarity - 0.5)));
        double normMax = 1.0 / (1.0 + Math.exp(-10 * (1 - 0.5)));
        return minWeight + (maxWeight - minWeight) * (norm / normMax);
    }
}