package de.jplag.highlightextraction;

public class RareTokensWeightedSimilarityStrategy implements AbstractSimilarityStrategy{
    public double calculateSimilarity(int minWeight, int maxWeight, double rarity) {
        return minWeight + (maxWeight - minWeight) * rarity;
    }
}
