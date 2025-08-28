package de.jplag.highlightextraction;

/**
 * Strategy for calculating the weight of a match
 */
public interface SimilarityStrategy {
    /**
     * Strategy for calculating the weight of a match
     * @param minWeight min considered weight
     * @param maxWeight max considered weight
     * @param rarity the normalized relative frequency of a match
     * @return the weight of the match
     */
    double computeWeight(double minWeight, double maxWeight, double rarity);
}
