package de.jplag.clustering.algorithm;

import java.util.List;
import java.util.function.DoubleBinaryOperator;

import org.apache.commons.math3.linear.RealMatrix;

public enum InterClusterSimilarity {
    MIN(Float.MAX_VALUE, Math::min),
    MAX(Float.MIN_VALUE, Math::max),
    AVERAGE(0, (a, b) -> a + b);

    private final float neutralElement;
    private final DoubleBinaryOperator accumulator;

    private InterClusterSimilarity(float neutralElement, DoubleBinaryOperator accumulator) {
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

        for (int leftIndex = 0; leftIndex < leftCluster.size(); leftIndex++) {
            int leftSubmission = leftCluster.get(leftIndex);
            for (int rightIndex = 0; rightIndex < rightCluster.size(); rightIndex++) {
                float submissionSimilarity = (float) similarityMatrix.getEntry(leftSubmission, rightCluster.get(rightIndex));
                similarity = (float) this.accumulator.applyAsDouble(similarity, submissionSimilarity);
            }
        }

        if (this == InterClusterSimilarity.AVERAGE) {
            similarity /= leftCluster.size() * rightCluster.size();
        }
        return similarity;
    }
}