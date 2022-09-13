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
    private final ClusteringResultMapper clusteringResultMapper = new ClusteringResultMapper(Submission::getName);

    @Test
    public void test() {
        // given
        JPlagResult resultMock = mock(JPlagResult.class);
        Cluster<Submission> cluster1 = createClusterWith(0.2, 0.4, "1", "2");
        Cluster<Submission> cluster2 = createClusterWith(0.3, 0.6, "3", "4", "5");
        when(resultMock.getClusteringResult()).thenReturn(List.of(new ClusteringResult<>(List.of(cluster1, cluster2), 0.3)));

        // when
        var result = clusteringResultMapper.map(resultMock);

        // then
        assertEquals(List.of(new de.jplag.reporting.reportobject.model.Cluster(0.4, 0.2, List.of("1", "2")),
                new de.jplag.reporting.reportobject.model.Cluster(0.6, 0.3, List.of("3", "4", "5"))

        ), result);
    }

    private Cluster<Submission> createClusterWith(Double communityStrength, Double averageSimilarity, String... ids) {
        var submissions = Arrays.stream(ids).map(this::submissionWithId).toList();
        return new Cluster<>(submissions, communityStrength, averageSimilarity);
    }

    private Submission submissionWithId(String id) {
        Submission submission = mock(Submission.class);
        when(submission.getName()).thenReturn(id);
        return submission;
    }
}
