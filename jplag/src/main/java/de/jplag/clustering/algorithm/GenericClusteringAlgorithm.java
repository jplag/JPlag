package de.jplag.clustering.algorithm;

import java.util.Collection;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Interface for algorithms that can perform clustering using only a symmetric matrix of similarities. The integers in
 * the returned collection of integer collections denote the rows / columns of the similarity matrix.
 */
public interface GenericClusteringAlgorithm {
    Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix);
}
