package de.jplag.clustering;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

public class ClusterTest {

    private static final double EPSILON = 0.00001;
    Cluster<Character> cluster;

    @Test
    public void testAverageSimilarity() {
        cluster = new Cluster<>(List.of('a', 'b', 'c'), 0);
        float averageSimilarity = cluster.averageSimilarity((a, b) -> {
            return Math.abs((float) (((int) a) - ((int) b)));
        });
        assertEquals((1.f + 2.f + 1.f + 1.f + 2.f + 1.f) / 6, averageSimilarity, EPSILON);
    }

    @Test
    public void testCommunityStrengthPerConnectionOneMember() {
        cluster = new Cluster<>(List.of('a'), 10);
        assertEquals(0.0, cluster.getCommunityStrengthPerConnection(), EPSILON);
    }

    @Test
    public void testCommunityStrengthPerConnectionTwoMembers() {
        cluster = new Cluster<>(List.of('a', 'b'), 10);
        assertEquals(10.0, cluster.getCommunityStrengthPerConnection(), EPSILON);
    }

    @Test
    public void testCommunityStrengthPerConnectionThreeMembers() {
        cluster = new Cluster<>(List.of('a', 'b', 'c'), 10);
        assertEquals(10.0 / 3, cluster.getCommunityStrengthPerConnection(), EPSILON);
    }

    @Test
    public void testNormalizedCommunityStrength() {
        cluster = new Cluster<>(List.of('a', 'b', 'c'), 10);
        @SuppressWarnings("unchecked")
        ClusteringResult<Character> clusteringResult = mock(ClusteringResult.class);
        when(clusteringResult.getClusters()).thenReturn(List.of(cluster, cluster));
        cluster.setClusteringResult(clusteringResult);
        assertEquals(0.5, cluster.getNormalizedCommunityStrengthPerConnection(), EPSILON);
    }

}
