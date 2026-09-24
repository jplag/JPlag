package de.jplag.frequency;

/**
 * Strategy for calculating the weight of a match.
 */
public interface MatchWeightingFunction {
    /**
     * Strategy for calculating the weight of a match.
     * @param rarity the normalized relative frequency of a match
     * @return the weight of the match
     */
    double computeWeight(double rarity);

}
