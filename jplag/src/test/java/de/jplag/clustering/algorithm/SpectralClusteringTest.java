package de.jplag.clustering.algorithm;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class SpectralClusteringTest {

    @Test
    public void test() {
        for (ClusteringData testData : ClusteringData.values()) {
            SpectralClustering clustering = new SpectralClustering(testData.getOptions());
            Collection<Collection<Integer>> result = clustering.cluster(testData.getSimilarity());
            testData.assertValid(result);
        }
    }
}
