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

    private final ClusteringOptions options;

    public SpectralClustering(ClusteringOptions options) {
        this.options = options;
    }

    @Override
    public Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix) {
        // Calculate points to cluster according to "On spectral clustering: analysis and an algorithm" by Ng, Jordan & Weiss
        // 2001
        int N = similarityMatrix.getRowDimension();

        // We don't use the similarity function, we already have some kind of similarity
        RealMatrix W = similarityMatrix.copy();
        W.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                if (row == column)
                    return 0;
                return similarityMatrix.getEntry(row, column);
            }
        });

        DiagonalMatrix D_pow_minus_1_over_2 = new DiagonalMatrix(N);
        D_pow_minus_1_over_2.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                if (row != column)
                    return 0;
                return 1 / Math.sqrt(W.getRowVector(row).getL1Norm());
            }
        });

        RealMatrix I = new Array2DRowRealMatrix(N, N);
        I.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                return row == column ? 1 : 0;
            }
        });
        RealMatrix L = I.subtract(D_pow_minus_1_over_2.multiply(W).multiply(D_pow_minus_1_over_2));
        EigenDecomposition ed = new EigenDecomposition(L);

        List<Integer> eigenValueIds = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            eigenValueIds.add(i);
        }
        eigenValueIds.sort(Comparator.comparingDouble(ed::getRealEigenvalue));

        // find number of clusters as the multiplicity of eigenvalue 0
        double eps = 0.05;
        int minK = Math.max(2, (int) DoubleStream.of(ed.getRealEigenvalues()).filter(x -> x < eps).count());
        int maxK = (int) Math.ceil(N / 2.0);

        // Find number of clusters using bayesian optimization
        RealVector lengthScale = new ArrayRealVector(1, options.getSpectralKernelBandwidth());
        BayesianOptimization bo = new BayesianOptimization(new ArrayRealVector(1, minK), new ArrayRealVector(1, maxK), options.getSpectralMinRuns(),
                options.getSpectralMaxRuns(), options.getSpectralGaussianProcessVariance(), lengthScale);
        // bo.debug = true;
        BayesianOptimization.OptimizationResult<Collection<Collection<Integer>>> boResult = bo.maximize(r -> {
            int k = (int) Math.round(r.getEntry(0));
            k = Math.max(minK, k);
            k = Math.min(maxK, k);
            Collection<Collection<Integer>> clustering = cluster(k, N, eigenValueIds, ed);
            ClusteringResult<Integer> modularityRes = ClusteringResult.fromIntegerCollections(new ArrayList<>(clustering), similarityMatrix);
            return new BayesianOptimization.OptimizationResult<>(modularityRes.getWorth((a, b) -> (float) similarityMatrix.getEntry(a, b)),
                    clustering);
        });

        return boResult.getValue();
    }

    private Collection<Collection<Integer>> cluster(int k, int n, List<Integer> eigenValueIds, EigenDecomposition ed) {
        RealMatrix Q = new Array2DRowRealMatrix(n, k);
        Q.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            public double visit(int row, int column, double value) {
                int eigenVectorId = eigenValueIds.get(column);
                RealVector eigenVector = ed.getEigenvector(eigenVectorId);
                return eigenVector.getEntry(row);
            };
        });

        List<ClusterableEigenVector> normRows = IntStream.range(0, n).filter(i -> Q.getRowVector(i).getNorm() > 0)
                .mapToObj(row -> new ClusterableEigenVector(row, Q.getRowVector(row).unitVector())).collect(Collectors.toList());

        Clusterer<ClusterableEigenVector> clusterer = new KMeansPlusPlusClusterer<>(k, options.getSpectralMaxKMeansIterationPerRun());
        List<? extends Cluster<ClusterableEigenVector>> clusters = clusterer.cluster(normRows);
        return clusters.stream().map(cluster -> {
            return cluster.getPoints().stream().map(x -> x.id).collect(Collectors.toList());
        }).collect(Collectors.toList());
    }

    private static class ClusterableEigenVector implements Clusterable {
        private int id;
        private double[] eigenVector;

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
