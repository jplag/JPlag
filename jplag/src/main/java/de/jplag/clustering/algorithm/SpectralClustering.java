package de.jplag.clustering.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.ClusteringResult;

/**
 * Spectral clustering is a clustering algorithm for graph data. Each node is represented as k-dimensional vector,
 * afterwards k-Means is used to generate a clustering with k on that representation. This implementation uses Bayesian
 * Optimization to find an appropriate number for k.
 */
public class SpectralClustering implements GenericClusteringAlgorithm {

    private static final double MULTIPLICITY_EPSILON = 0.05;
    private final ClusteringOptions options;

    public SpectralClustering(ClusteringOptions options) {
        this.options = options;
    }

    @Override
    public Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix) {
        // Calculate points to cluster according to "On spectral clustering: analysis and an algorithm" by Ng, Jordan & Weiss
        // 2001
        int dimension = similarityMatrix.getRowDimension();

        // We don't use the similarity function, we already have some kind of similarity
        RealMatrix weights = similarityMatrix.copy();
        weights.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                if (row == column)
                    return 0;
                return similarityMatrix.getEntry(row, column);
            }
        });

        DiagonalMatrix diagonalPowMinus1Over2 = new DiagonalMatrix(dimension);
        diagonalPowMinus1Over2.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                if (row != column)
                    return 0;
                return 1 / Math.sqrt(weights.getRowVector(row).getL1Norm());
            }
        });

        RealMatrix identity = new Array2DRowRealMatrix(dimension, dimension);
        identity.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                return row == column ? 1 : 0;
            }
        });
        RealMatrix laplacian = identity.subtract(diagonalPowMinus1Over2.multiply(weights).multiply(diagonalPowMinus1Over2));
        EigenDecomposition eigenDecomposition = new EigenDecomposition(laplacian);

        List<Integer> eigenValueIds = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            eigenValueIds.add(i);
        }
        eigenValueIds.sort(Comparator.comparingDouble(eigenDecomposition::getRealEigenvalue));

        // find number of clusters as the multiplicity of eigenvalue 0
        int minClusters = Math.max(2, (int) DoubleStream.of(eigenDecomposition.getRealEigenvalues()).filter(x -> x < MULTIPLICITY_EPSILON).count());
        int maxClusters = (int) Math.ceil(dimension / 2.0);

        // Find number of clusters using bayesian optimization
        RealVector lengthScale = new ArrayRealVector(1, options.spectralKernelBandwidth());
        BayesianOptimization bo = new BayesianOptimization(new ArrayRealVector(1, minClusters), new ArrayRealVector(1, maxClusters),
                options.spectralMinRuns(), options.spectralMaxRuns(), options.spectralGaussianProcessVariance(), lengthScale);
        // bo.debug = true;
        BayesianOptimization.OptimizationResult<Collection<Collection<Integer>>> bayesianOptimizationResult = bo.maximize(r -> {
            int clusters = (int) Math.round(r.getEntry(0));
            clusters = Math.max(minClusters, clusters);
            clusters = Math.min(maxClusters, clusters);
            Collection<Collection<Integer>> clustering = cluster(clusters, dimension, eigenValueIds, eigenDecomposition);
            ClusteringResult<Integer> modularityRes = ClusteringResult.fromIntegerCollections(new ArrayList<>(clustering), similarityMatrix);
            return new BayesianOptimization.OptimizationResult<>(modularityRes.getWorth(similarityMatrix::getEntry), clustering);
        });

        return bayesianOptimizationResult.getValue();
    }

    private Collection<Collection<Integer>> cluster(int numberOfClusters, int dimension, List<Integer> eigenValueIds, EigenDecomposition ed) {
        RealMatrix concatenatedEigenVectors = new Array2DRowRealMatrix(dimension, numberOfClusters);
        concatenatedEigenVectors.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                int eigenVectorId = eigenValueIds.get(column);
                RealVector eigenVector = ed.getEigenvector(eigenVectorId);
                return eigenVector.getEntry(row);
            }
        });

        List<ClusterableEigenVector> normRows = IntStream.range(0, dimension).filter(i -> concatenatedEigenVectors.getRowVector(i).getNorm() > 0)
                .mapToObj(row -> new ClusterableEigenVector(row, concatenatedEigenVectors.getRowVector(row).unitVector())).toList();

        Clusterer<ClusterableEigenVector> clusterer = new KMeansPlusPlusClusterer<>(numberOfClusters, options.spectralMaxKMeansIterationPerRun());
        List<? extends Cluster<ClusterableEigenVector>> clusters = clusterer.cluster(normRows);
        return clusters.stream().map(cluster -> cluster.getPoints().stream().map(eigenVector -> eigenVector.id).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private static class ClusterableEigenVector implements Clusterable {
        private final int id;
        private final double[] eigenVector;

        public ClusterableEigenVector(int id, RealVector eigenVector) {
            this.id = id;
            this.eigenVector = eigenVector.toArray();
        }

        @Override
        public double[] getPoint() {
            return eigenVector;
        }
    }

}
