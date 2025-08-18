package de.jplag.highlightextraction;

public class ProportionalWeigthedStrategy implements SimilarityStrategy{

    @Override
    public double computeWeight(double minWeight, double maxWeight, double rarity) {
        return minWeight + (maxWeight - minWeight) * rarity;
    }
}
