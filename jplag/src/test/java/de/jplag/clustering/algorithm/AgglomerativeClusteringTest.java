package de.jplag.clustering.algorithm;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Test;

import de.jplag.clustering.ClusteringOptions;

public class AgglomerativeClusteringTest {
    AgglomerativeClustering clustering;

    @Before
    public void init() {
        ClusteringOptions options = new ClusteringOptions.Builder().agglomerativeThreshold(0.4f).build();
        clustering = new AgglomerativeClustering(options);
    }

    @Test
    public void test() {
        RealMatrix similarity = new Array2DRowRealMatrix(4, 4);
        for (int i = 0; i < 4; i++) {
            similarity.setEntry(i, i, 1);
        }
        // These are similar
        similarity.setEntry(0, 1, 0.5f);
        similarity.setEntry(2, 3, 0.5f);

        // Others are dissimilar
        similarity.setEntry(0, 2, 0.1f);
        similarity.setEntry(0, 3, 0.1f);
        similarity.setEntry(1, 2, 0.1f);
        similarity.setEntry(1, 3, 0.1f);

        Collection<Collection<Integer>> result = clustering.cluster(similarity);
        assertTrue("There should be two clusters", result.size() == 2);
        assertTrue("should contain all original numbers", result.stream().reduce(new ArrayList<Integer>(), (a, b) -> {
            ArrayList<Integer> combined = new ArrayList<>();
            combined.addAll(a);
            combined.addAll(b);
            return combined;
        }).size() == 4);
        assertTrue("Should be clustered as {0,1}, {2,3}", result.stream().allMatch(cluster -> {
            return cluster.size() == 2 && cluster.stream().reduce((a, b) -> Math.abs(a - b)).orElse(0) == 1;
        }) && result.stream().map(Collection::size).reduce((a, b) -> a + b).orElse(0) == 4);

    }
}
