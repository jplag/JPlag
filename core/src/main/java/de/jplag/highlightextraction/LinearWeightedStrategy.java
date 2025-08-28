package de.jplag.highlightextraction;

/**
 * Strategy that considers rare matches stronger using a linear weighting function.
 */
public class LinearWeightedStrategy implements SimilarityStrategy {
    /**
     * Strategy that considers rare matches stronger than  frequent ones. Weighted with a Linear weighting function.
     * @param minWeight min considered weight
     * @param maxWeight max considered weight
     * @param rarity the normalized relative frequency of a match
     * @return the weight of the match
     */
    @Override
    public double computeWeight(double minWeight, double maxWeight, double rarity) {
        return minWeight + (maxWeight - minWeight) * rarity;
    }
}
