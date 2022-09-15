package de.jplag.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;

class ClusteringResultTest {

    @Test
    void perfectClustering() {
        RealMatrix similarity = new Array2DRowRealMatrix(4, 4);

        // These are similar
        setEntries(similarity, 0, 1, 1.0);
        setEntries(similarity, 2, 3, 1.0);

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
        setEntries(similarity, 0, 1, 0.1);
        setEntries(similarity, 2, 3, 0.1);

        // Others are dissimilar
        setEntries(similarity, 0, 2, 0.05);
        setEntries(similarity, 0, 3, 0.05);
        setEntries(similarity, 1, 2, 0.05);
        setEntries(similarity, 1, 3, 0.05);

        ClusteringResult<Integer> result = ClusteringResult.fromIntegerCollections(List.of(List.of(0, 1), List.of(2, 3)), similarity);

        assertEquals(0.0, result.getCommunityStrength(), 0.00001);
    }

    @Test
    void averageSimilarity() {
        var similarity = new Array2DRowRealMatrix(4, 4);

        setEntries(similarity, 0, 1, 0.5);
        setEntries(similarity, 0, 2, 0.3);
        setEntries(similarity, 0, 3, 0.4);
        setEntries(similarity, 1, 2, 0.1);
        setEntries(similarity, 1, 3, 0.1);
        setEntries(similarity, 2, 3, 0.7);

        ClusteringResult<Integer> result = ClusteringResult.fromIntegerCollections(List.of(List.of(0, 1, 2, 3)), similarity);

        assertEquals(0.35, result.getClusters().stream().findFirst().orElseThrow().getAverageSimilarity(), 0.00001);
    }

    @Test
    void averageSimilarity2() {
        var similarity = new Array2DRowRealMatrix(6, 6);

        setEntries(similarity, 0, 1, 0.5);
        setEntries(similarity, 0, 2, 0.3);
        setEntries(similarity, 0, 3, 0.4);
        setEntries(similarity, 0, 4, 0.4);
        setEntries(similarity, 1, 2, 0.1);
        setEntries(similarity, 1, 3, 0.1);
        setEntries(similarity, 1, 4, 0.3);
        setEntries(similarity, 2, 3, 0.7);
        setEntries(similarity, 2, 4, 0.2);
        setEntries(similarity, 2, 5, 0.9);
        setEntries(similarity, 3, 4, 0.5);
        setEntries(similarity, 3, 5, 0.05);

        ClusteringResult<Integer> result = ClusteringResult.fromIntegerCollections(List.of(List.of(0, 1, 4), List.of(2, 3, 5)), similarity);
        var clusters = new ArrayList<>(result.getClusters());

        assertEquals(0.4, clusters.get(0).getAverageSimilarity(), 0.00001);
        assertEquals(0.55, clusters.get(1).getAverageSimilarity(), 0.00001);
    }

    @Test
    void averageSimilarityPerfectClustering() {
        RealMatrix similarity = new Array2DRowRealMatrix(4, 4);

        // These are similar
        setEntries(similarity, 0, 1, 1.0);
        setEntries(similarity, 2, 3, 1.0);

        // Others are dissimilar

        ClusteringResult<Integer> result = ClusteringResult.fromIntegerCollections(List.of(List.of(0, 1), List.of(2, 3)), similarity);
        var cluster = result.getClusters().stream().findFirst().orElseThrow();
        assertEquals(1.0, cluster.getAverageSimilarity(), 0.00001);
    }

    private static void setEntries(RealMatrix matrix, int i, int j, double similarity) {
        matrix.setEntry(i, j, similarity);
        matrix.setEntry(j, i, similarity);
    }
}
