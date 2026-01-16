package de.jplag.highlightextraction.weighting;

import de.jplag.highlightextraction.WeightingFunction;

/**
 * Frequency weighting strategy which emphasizes rare matches and leaves frequent matches unchanged.
 */
public class SigmoidWeighting implements WeightingFunction {

    private static final int MINIMUM_WEIGHT = 1;
    private static final int MAXIMUM_WEIGHT = 2;

    @Override
    public double computeWeight(double rarity) {
        // value approaches 0 and 1 for rarity -INF and +INF
        double sigmoidWeight = 1.0 / (1.0 + Math.exp(-10 * (rarity - 0.5)));

        // value at rarity = 1
        double maxWeight = 1.0 / (1.0 + Math.exp(-10 * (1 - 0.5)));

        // normalize using maxWeight -> value equals 0 and 1 for rarity 0 and 1
        double normedWeight = 0.5 * (sigmoidWeight - 0.5) / (maxWeight - 0.5) + 0.5;
        return MINIMUM_WEIGHT + (MAXIMUM_WEIGHT - MINIMUM_WEIGHT) * normedWeight;
    }

}
