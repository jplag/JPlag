package de.jplag.frequency;

/**
 * This class contains the possible weighting functions for a match, in the frequency analysis.
 */
public enum MatchFrequencyWeightingFunction implements MatchWeightingFunction {
    PROPORTIONAL {
        @Override
        public double computeWeight(double minWeight, double maxWeight, double rarity) {
            return minWeight + (maxWeight - minWeight) * rarity;
        }
    },
    LINEAR {
        @Override
        public double computeWeight(double minWeight, double maxWeight, double rarity) {
            return minWeight + rarity;
        }
    },
    QUADRATIC {
        @Override
        public double computeWeight(double minWeight, double maxWeight, double rarity) {
            return minWeight + (maxWeight - minWeight) * (rarity * rarity);
        }
    },
    SIGMOID {
        @Override
        public double computeWeight(double minWeight, double maxWeight, double rarity) {
            double norm = 1.0 / (1.0 + Math.exp(-10 * (rarity - 0.5)));
            double normMax = 1.0 / (1.0 + Math.exp(-10 * (1 - 0.5)));
            return minWeight + (maxWeight - minWeight) * (norm / normMax);
        }
    };
}
