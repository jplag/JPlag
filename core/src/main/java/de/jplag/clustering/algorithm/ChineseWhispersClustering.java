package de.jplag.clustering.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.ClusteringOptions;

/**
 * Chinese Whispers is a graph clustering algorithm, that starts by assigning each node its own label and then in each
 * iteration updating the label of each node to the label that is the most present among its neighbors.
 */
public class ChineseWhispersClustering implements GenericClusteringAlgorithm {
    private final ClusteringOptions options;

    /**
     * Creates a new Chinese Whispers clustering algorithm with the given options.
     * @param options clustering configuration
     */
    public ChineseWhispersClustering(ClusteringOptions options) {
        this.options = options;
    }

    @Override
    public Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix) {
        int numberOfNodes = similarityMatrix.getRowDimension();

        // Maps each node to a label
        int[] labels = new int[numberOfNodes];

        for (int i = 0; i < numberOfNodes; i++) {
            labels[i] = i;
        }

        boolean isConverged = false;
        int numberOfIterations = 0;
        while (!isConverged && numberOfIterations < options.chineseWhispersMaxIterations()) {
            isConverged = true;
            for (int i = 0; i < numberOfNodes; i++) {
                int node = i;

                // Maps labels to the "number of appearances" of that label
                Map<Integer, Double> labelCounts = new HashMap<>();
                for (int neighbor = 0; neighbor < numberOfNodes; neighbor++) {
                    if (neighbor == node) {
                        continue;
                    }
                    double weight = similarityMatrix.getEntry(node, neighbor);
                    int labelOfThisNeighbor = labels[neighbor];
                    if (labelCounts.containsKey(labelOfThisNeighbor)) {
                        labelCounts.put(labelOfThisNeighbor, labelCounts.get(labelOfThisNeighbor) + weight);
                    } else {
                        labelCounts.put(labelOfThisNeighbor, weight);
                    }
                }

                int newLabel = -1;
                double currentMaxCount = Double.NEGATIVE_INFINITY;
                for (Map.Entry<Integer, Double> e : labelCounts.entrySet()) {
                    if (e.getValue() > currentMaxCount) {
                        currentMaxCount = e.getValue();
                        newLabel = e.getKey();
                    }
                }
                if (labels[node] != newLabel) {
                    labels[node] = newLabel;
                    isConverged = false;
                }
            }
            numberOfIterations++;
        }

        // Maps from labels to a list of nodes that were assigned this label
        Map<Integer, Collection<Integer>> clusters = new HashMap<>();
        for (int node = 0; node < numberOfNodes; node++) {
            int labelOfThisNode = labels[node];
            if (clusters.containsKey(labelOfThisNode)) {
                clusters.get(labelOfThisNode).add(node);
            } else {
                clusters.put(labelOfThisNode, new ArrayList<>(List.of(node)));
            }
        }
        return clusters.values();
    }
}
