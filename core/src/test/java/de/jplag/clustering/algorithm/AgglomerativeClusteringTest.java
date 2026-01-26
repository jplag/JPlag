package de.jplag.clustering.algorithm;

import java.util.Collection;

import org.junit.jupiter.api.Test;

class AgglomerativeClusteringTest {

    @Test
    void test() {
        for (ClusteringData testData : ClusteringData.values()) {
            AgglomerativeClustering clustering = new AgglomerativeClustering(testData.getOptions());
            Collection<Collection<Integer>> result = clustering.cluster(testData.getSimilarity());
            testData.assertValid(result);
        }
    }
}
