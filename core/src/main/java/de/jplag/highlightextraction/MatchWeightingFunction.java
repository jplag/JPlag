package de.jplag.highlightextraction;

/**
 * Strategy for calculating the weight of a match.
 */
public interface MatchWeightingFunction {
    /**
     * Strategy for calculating the weight of a match.
     * @param minWeight min considered weight
     * @param maxWeight max considered weight
     * @param rarity the normalized relative isFrequencyAnalysisEnabled of a match
     * @return the weight of the match
     */
    double computeWeight(double minWeight, double maxWeight, double rarity);
}
