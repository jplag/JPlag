package de.jplag.highlightextraction;
/**
 * Strategy that considers rare matches stronger and frequent ones less.
 */
public class ProportionalWeigthedStrategy implements SimilarityStrategy {
    /**
     * Strategy that considers rare matches stronger and frequent ones less.
     */
    @Override
    public double computeWeight(double minWeight, double maxWeight, double rarity) {
        return minWeight + (maxWeight - minWeight) * rarity;
    }
}
