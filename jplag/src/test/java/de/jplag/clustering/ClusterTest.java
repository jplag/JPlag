package de.jplag.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ClusterTest {

    private static final double EPSILON = 0.00001;
    Cluster<Character> cluster;

    @Test
    public void testCommunityStrengthPerConnectionOneMember() {
        cluster = new Cluster<>(List.of('a'), 10, 0);
        assertEquals(0.0, cluster.getCommunityStrengthPerConnection(), EPSILON);
    }

    @Test
    public void testCommunityStrengthPerConnectionTwoMembers() {
        cluster = new Cluster<>(List.of('a', 'b'), 10, 0);
        assertEquals(10.0, cluster.getCommunityStrengthPerConnection(), EPSILON);
    }

    @Test
    public void testCommunityStrengthPerConnectionThreeMembers() {
        cluster = new Cluster<>(List.of('a', 'b', 'c'), 10, 0);
        assertEquals(10.0 / 3, cluster.getCommunityStrengthPerConnection(), EPSILON);
    }

    @Test
    public void testNormalizedCommunityStrength() {
        cluster = new Cluster<>(List.of('a', 'b', 'c'), 10, 0);
        @SuppressWarnings("unchecked")
        ClusteringResult<Character> clusteringResult = mock(ClusteringResult.class);
        when(clusteringResult.getClusters()).thenReturn(List.of(cluster, cluster));
        cluster.setClusteringResult(clusteringResult);
        assertEquals(0.5, cluster.getNormalizedCommunityStrengthPerConnection(), EPSILON);
    }

}
