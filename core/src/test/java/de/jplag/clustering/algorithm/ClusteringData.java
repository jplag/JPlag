package de.jplag.clustering.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.ClusteringOptions;

/**
 * Enum providing predefined clustering test data sets, including similarity matrices, expected cluster groupings, and
 * clustering options.
 */
public enum ClusteringData {

    /**
     * A test data set with four points, where points 0 and 1 are similar, points 2 and 3 are similar, and others are
     * dissimilar. The expected clusters are {0, 1} and {2, 3}.
     */
    FOUR_POINTS(() -> {
        RealMatrix similarity = new Array2DRowRealMatrix(4, 4);
        for (int i = 0; i < 4; i++) {
            similarity.setEntry(i, i, 1);
        }
        // These are similar
        setEntries(similarity, 0, 1, 0.5);
        setEntries(similarity, 2, 3, 0.5);

        // Others are dissimilar
        setEntries(similarity, 0, 2, 0.1);
        setEntries(similarity, 0, 3, 0.1);
        setEntries(similarity, 1, 2, 0.1);
        setEntries(similarity, 1, 3, 0.1);

        return similarity;
    }, new int[][] {{0, 1}, {2, 3}}, new ClusteringOptions().withAgglomerativeThreshold(0.4));

    private final RealMatrix similarity;
    private final Set<Set<Integer>> expected;
    private final ClusteringOptions options;

    ClusteringData(Supplier<RealMatrix> similarity, int[][] expected, ClusteringOptions options) {
        this.similarity = similarity.get();
        this.expected = makeSets(Arrays.stream(expected).map(intArray -> Arrays.stream(intArray).boxed().toList()).toList());
        this.options = options;
    }

    /**
     * Returns the clustering options associated with this test data.
     * @return clustering options
     */
    public ClusteringOptions getOptions() {
        return options;
    }

    /**
     * Returns the similarity matrix for this test data.
     * @return similarity matrix
     */
    public RealMatrix getSimilarity() {
        return similarity;
    }

    private Set<Set<Integer>> makeSets(Collection<? extends Collection<Integer>> collections) {
        return collections.stream().map(HashSet::new).collect(Collectors.toSet());
    }

    /**
     * Asserts that the actual clustering result matches the expected clusters.
     * @param actual the actual cluster result to check
     * @throws AssertionError if the clusters don't match
     */
    public void assertValid(Collection<? extends Collection<Integer>> actual) {
        assertEquals(expected, makeSets(actual), this.name() + " not clustered correctly");
    }

    private static void setEntries(RealMatrix matrix, int i, int j, double value) {
        matrix.setEntry(i, j, value);
        matrix.setEntry(j, i, value);
    }
}
