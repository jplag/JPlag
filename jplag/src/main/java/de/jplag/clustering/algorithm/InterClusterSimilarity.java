package de.jplag.clustering.algorithm;

import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.math3.linear.RealMatrix;

public enum InterClusterSimilarity {
    MIN(Float.MAX_VALUE, Math::min),
    MAX(Float.MIN_VALUE, Math::max),
    AVERAGE(0, Float::sum);

    private final float neutralElement;
    private final BiFunction<Float, Float, Float> accumulator;

    InterClusterSimilarity(float neutralElement, BiFunction<Float, Float, Float> accumulator) {
        this.neutralElement = neutralElement;
        this.accumulator = accumulator;
    }

    /**
     * Calculates the distance between two clusters.
     * @param leftCluster list of cluster indices in left cluster
     * @param rightCluster list of cluster indices in right cluster
     * @param similarityMatrix matrix containing similarities
     * @return similarity between the two clusters
     */
    public float clusterSimilarity(List<Integer> leftCluster, List<Integer> rightCluster, RealMatrix similarityMatrix) {
        float similarity = this.neutralElement;

        for (int leftSubmission : leftCluster) {
            for (int rightSubmission : rightCluster) {
                float submissionSimilarity = (float) similarityMatrix.getEntry(leftSubmission, rightSubmission);
                similarity = this.accumulator.apply(similarity, submissionSimilarity);
            }
        }

        if (this == InterClusterSimilarity.AVERAGE) {
            similarity /= leftCluster.size() * rightCluster.size();
        }
        return similarity;
    }
}