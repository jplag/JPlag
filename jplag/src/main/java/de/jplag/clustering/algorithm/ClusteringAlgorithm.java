package de.jplag.clustering.algorithm;

import java.util.Collection;

import org.apache.commons.math3.linear.RealMatrix;

public interface ClusteringAlgorithm {
    Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix);
}
