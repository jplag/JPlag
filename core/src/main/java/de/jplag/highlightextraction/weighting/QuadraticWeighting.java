package de.jplag.highlightextraction.weighting;

import de.jplag.highlightextraction.WeightingFunction;

/**
 * Frequency weighting strategy which emphasizes the rarest matches with limited influence on more frequent matches.
 */
public class QuadraticWeighting implements WeightingFunction {

    private static final int MINIMUM_WEIGHT = 1;
    private static final int MAXIMUM_WEIGHT = 2;

    @Override
    public double computeWeight(double rarity) {
        return MINIMUM_WEIGHT + (MAXIMUM_WEIGHT - MINIMUM_WEIGHT) * (rarity * rarity);
    }

}
