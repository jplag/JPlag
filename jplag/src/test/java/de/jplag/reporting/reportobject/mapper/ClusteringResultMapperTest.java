package de.jplag.reporting.reportobject.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.clustering.Cluster;
import de.jplag.clustering.ClusteringResult;

public class ClusteringResultMapperTest {
    private final ClusteringResultMapper clusteringResultMapper = new ClusteringResultMapper();

    @Test
    public void test() {
        // given
        JPlagResult resultMock = mock(JPlagResult.class);
        Cluster<Submission> cluster1 = createClusterWith(0.2f, 0.4f, "1", "2");
        Cluster<Submission> cluster2 = createClusterWith(0.3f, 0.6f, "3", "4", "5");
        when(resultMock.getClusteringResult()).thenReturn(List.of(new ClusteringResult<>(List.of(cluster1, cluster2), 0.3f)));

        // when
        var result = clusteringResultMapper.map(resultMock);

        // then
        assertEquals(List.of(new de.jplag.reporting.reportobject.model.Cluster(0.4f, 0.2f, List.of("1", "2")),
                new de.jplag.reporting.reportobject.model.Cluster(0.6f, 0.3f, List.of("3", "4", "5"))

        ), result);
    }

    private Cluster<Submission> createClusterWith(Float communityStrength, Float averageSimilarity, String... ids) {
        var submissions = Arrays.stream(ids).map(this::submissionWithId).toList();
        return new Cluster<>(submissions, communityStrength, averageSimilarity);
    }

    private Submission submissionWithId(String id) {
        Submission submission = mock(Submission.class);
        when(submission.getName()).thenReturn(id);
        return submission;
    }
}
