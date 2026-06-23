package de.jplag.clustering.algorithm;

import java.util.Collection;

import org.junit.jupiter.api.Test;

class ChineseWhispersClusteringTest {
    @Test
    void test() {
        for (ClusteringData testData : ClusteringData.values()) {
            ChineseWhispersClustering clustering = new ChineseWhispersClustering(testData.getOptions());
            Collection<Collection<Integer>> result = clustering.cluster(testData.getSimilarity());
            testData.assertValid(result);
        }
    }
}
