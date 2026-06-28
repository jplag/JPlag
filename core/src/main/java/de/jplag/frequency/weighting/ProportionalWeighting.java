package de.jplag.frequency.weighting;

import de.jplag.frequency.WeightingFunction;

/**
 * Frequency weighting strategy which emphasizes or de-emphasizes matches proportionally in terms of their rarity.
 */
public class ProportionalWeighting implements WeightingFunction {

    private static final int MINIMUM_WEIGHT = 0;
    private static final int MAXIMUM_WEIGHT = 2;

    @Override
    public double computeWeight(double rarity) {
        return MINIMUM_WEIGHT + (MAXIMUM_WEIGHT - MINIMUM_WEIGHT) * rarity;
    }

}
