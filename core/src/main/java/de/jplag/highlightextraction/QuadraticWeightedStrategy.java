package de.jplag.highlightextraction;
/**
 * Strategy that considers rare matches stronger using a quadratic weighting function.
 */
public class QuadraticWeightedStrategy implements SimilarityStrategy {

    @Override
    public double computeWeight(double minWeight, double maxWeight, double rarity) {
        return minWeight + (maxWeight - minWeight) * (rarity * rarity);
    }
}
