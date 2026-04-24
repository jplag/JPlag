package de.jplag.clustering;

import java.util.Objects;

import de.jplag.clustering.algorithm.AgglomerativeClustering;
import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;
import de.jplag.clustering.algorithm.SpectralClustering;

/**
 * Choosable clustering algorithms.
 */
public enum ClusteringAlgorithm {
    /**
     * {@link AgglomerativeClustering}.
     */
    AGGLOMERATIVE(AgglomerativeClustering::new),
    /**
     * {@link SpectralClustering}.
     */
    SPECTRAL(SpectralClustering::new);

    private final ClusteringAlgorithmSupplier constructor;

    ClusteringAlgorithm(ClusteringAlgorithmSupplier constructor) {
        this.constructor = constructor;
    }

    /**
     * Create GenericClusteringAlgorithm by ClusteringOptions.
     * @param options the options to use for creation
     * @return a new instance of GenericClusteringAlgorithm
     */
    public GenericClusteringAlgorithm create(ClusteringOptions options) {
        Objects.requireNonNull(options);
        return this.constructor.create(options);
    }

    /**
     * Functional interface for supplying clustering algorithm instances.
     */
    @FunctionalInterface
    public interface ClusteringAlgorithmSupplier {
        /**
         * Creates a new clustering algorithm instance with the given options.
         * @param options clustering options to configure the algorithm
         * @return a configured GenericClusteringAlgorithm instance
         */
        GenericClusteringAlgorithm create(ClusteringOptions options);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
