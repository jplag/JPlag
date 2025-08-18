package de.jplag.highlightextraction;

public class RareTokensWeightedStrategy implements SimilarityStrategy{
    public double computeWeight(double minWeight, double maxWeight, double rarity) {
        return minWeight + (maxWeight - minWeight) * rarity;
    }
}
