package de.jplag.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;

class ClusteringResultTest {

    @Test
    void perfectClustering() {
        RealMatrix similarity = new Array2DRowRealMatrix(4, 4);

        // These are similar
        setEntries(similarity, 0, 1, 1f);
        setEntries(similarity, 2, 3, 1f);

        // Others are dissimilar

        ClusteringResult<Integer> result = ClusteringResult.fromIntegerCollections(List.of(List.of(0, 1), List.of(2, 3)), similarity);

        // The maximum of the metric is 1 - 1/k for k clusters
        assertEquals(0.5, result.getCommunityStrength(), 0.00001);
    }

    @Test
    void uniformClustering() {
        RealMatrix similarity = new Array2DRowRealMatrix(4, 4);

        // We'd obtain such weights by pre-selecting the clusters,
        // then randomly picking two clusters and adding weight between random of each
        // cluster

        // These are similar
        setEntries(similarity, 0, 1, 0.1f);
        setEntries(similarity, 2, 3, 0.1f);

        // Others are dissimilar
        setEntries(similarity, 0, 2, 0.05f);
        setEntries(similarity, 0, 3, 0.05f);
        setEntries(similarity, 1, 2, 0.05f);
        setEntries(similarity, 1, 3, 0.05f);

        ClusteringResult<Integer> result = ClusteringResult.fromIntegerCollections(List.of(List.of(0, 1), List.of(2, 3)), similarity);

        assertEquals(0.0, result.getCommunityStrength(), 0.00001);
    }

    private static void setEntries(RealMatrix matrix, int i, int j, double value) {
        matrix.setEntry(i, j, value);
        matrix.setEntry(j, i, value);
    }
}
