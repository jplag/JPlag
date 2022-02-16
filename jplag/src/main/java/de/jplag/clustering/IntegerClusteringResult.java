package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class IntegerClusteringResult implements ClusteringResult<Integer> {

    private Collection<Cluster<Integer>> clusters = new ArrayList<>();
    private double communityStrength = 0;
    private int size;

    /**
     * Responsible for calculating {@link ClusteringResult#getCommunityStrength}
     */
    public IntegerClusteringResult(Collection<Collection<Integer>> clustering, RealMatrix similarity) {
        int N = similarity.getRowDimension();
        Map<Integer, Integer> submissionIdx2ClusterIdx = new HashMap<>();
        int clusterIdx = 0;
        List<Collection<Integer>> clusters = new ArrayList<>(clustering);
        for (Collection<Integer> cluster : clusters) {
            for (Integer submissionIdx : cluster) {
                submissionIdx2ClusterIdx.put(submissionIdx, clusterIdx);
            }
            clusterIdx++;
        }
        if (clusters.size() > 0) {
            RealMatrix E = new Array2DRowRealMatrix(clusters.size(), clusters.size());
            E = E.scalarMultiply(0);
            for (int i = 0; i < N; i++) {
                if (!submissionIdx2ClusterIdx.containsKey(i))
                    continue;
                int clusterA = submissionIdx2ClusterIdx.get(i);
                for (int j = i + 1; j < N; j++) {
                    if (!submissionIdx2ClusterIdx.containsKey(j))
                        continue;
                    int clusterB = submissionIdx2ClusterIdx.get(j);
                    E.addToEntry(clusterA, clusterB, similarity.getEntry(i, j));
                    E.addToEntry(clusterB, clusterA, similarity.getEntry(i, j));
                }
            }
            E = E.scalarMultiply(1 / Arrays.stream(similarity.getData()).flatMapToDouble(DoubleStream::of).sum());
            for (int i = 0; i < clusters.size(); i++) {
                double outWeightSum = E.getRowVector(i).getL1Norm();
                double clusterCommunityStrength = E.getEntry(i, i) - outWeightSum * outWeightSum;
                this.clusters.add(new Cluster<Integer>(clusters.get(i), (float) clusterCommunityStrength, this));
                communityStrength += clusterCommunityStrength;
            }
        }
        size = this.clusters.stream().mapToInt(x -> x.getMembers().size()).sum();
    }

    @Override
    public Collection<Cluster<Integer>> getClusters() {
        return Collections.unmodifiableCollection(clusters);
    }

    @Override
    public float getCommunityStrength() {
        return (float) communityStrength;
    }

    @Override
    public int size() {
        return size;
    }

}
