package de.jplag.clustering;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;

public class ClusteringAdapterTest {

    @Test
    public void testClustering() {
        List<Submission> submissions = IntStream.range(0, 4).mapToObj(x -> mock(Submission.class)).collect(Collectors.toList());
        List<JPlagComparison> comparisons = new ArrayList<>(6);
        for (int i = 0; i < submissions.size(); i++) {
            for (int j = i + 1; j < submissions.size(); j++) {
                JPlagComparison comparison = mock(JPlagComparison.class);
                when(comparison.getFirstSubmission()).thenReturn(submissions.get(i));
                when(comparison.getSecondSubmission()).thenReturn(submissions.get(j));
                comparisons.add(comparison);
            }
        }

        // Mock algorithm that returns everything in a single cluster
        GenericClusteringAlgorithm algorithm = mock(GenericClusteringAlgorithm.class);
        when(algorithm.cluster(any(RealMatrix.class))).then((InvocationOnMock invocation) -> {
            RealMatrix arg = invocation.getArgument(0);
            Collection<Collection<Integer>> result = List.of(IntStream.range(0, arg.getRowDimension()).boxed().collect(Collectors.toList()));
            return result;
        });

        ClusteringAdapter clustering = new ClusteringAdapter(comparisons, x -> 0.f);
        ClusteringResult<Submission> clusteringResult = clustering.doClustering(algorithm);

        Collection<Collection<Submission>> expectedResult = List.of(submissions);

        assertEquals(expectedResult, clusteringResult.getClusters().stream().map(Cluster::getMembers).collect(Collectors.toList()));
    }

}
