package de.jplag.highlightextraction.weighting;

import de.jplag.highlightextraction.WeightingFunction;

/**
 * Frequency weighting strategy which emphasizes matches linearly in terms of their rarity.
 */
public class LinearWeighting implements WeightingFunction {

    private static final int MINIMUM_WEIGHT = 1;
    private static final int MAXIMUM_WEIGHT = 2;

    @Override
    public double computeWeight(double rarity) {
        return MINIMUM_WEIGHT + (MAXIMUM_WEIGHT - MINIMUM_WEIGHT) * rarity;
    }

}
