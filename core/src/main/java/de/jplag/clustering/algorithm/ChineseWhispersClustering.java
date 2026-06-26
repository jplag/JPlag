package de.jplag.clustering.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.clustering.ClusteringOptions;

/**
 * Chinese Whispers is a graph clustering algorithm, that starts by assigning each node its own label and then in each
 * iteration updating the label of each node to the label that is the most present among its neighbors.
 */
public class ChineseWhispersClustering implements GenericClusteringAlgorithm {
    private static final Logger logger = LoggerFactory.getLogger(ChineseWhispersClustering.class);

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
        List<Integer> permutation = new ArrayList<>(IntStream.range(0, numberOfNodes).boxed().toList());

        // Maps each node to a label
        int[] labels = new int[numberOfNodes];

        int[] newLabelsBuffer = new int[numberOfNodes];
        int[] labelsFromPreviousIteration = new int[numberOfNodes];

        for (int i = 0; i < numberOfNodes; i++) {
            labels[i] = i;
        }

        boolean isConverged = false;
        int numberOfIterations = 0;
        while (!isConverged && numberOfIterations < options.chineseWhispersMaxIterations()) {
            if (options.chineseWhispersClusteringMode() == ChineseWhispersClusteringMode.UPDATE_IMMEDIATELY_RANDOMIZED) {
                Collections.shuffle(permutation);
            }

            isConverged = true;
            for (int i = 0; i < numberOfNodes; i++) {
                int node = permutation.get(i);

                // Maps labels to the "number of appearances" of that label
                Map<Integer, Double> labelCounts = calculateLabelCounts(similarityMatrix, labels, numberOfNodes, node);
                int newLabel = findLabelWithHighestCount(labelCounts);

                if (labels[node] != newLabel) {
                    if (options.chineseWhispersClusteringMode() == ChineseWhispersClusteringMode.UPDATE_IN_BATCHES) {
                        newLabelsBuffer[node] = newLabel;
                    } else {
                        labels[node] = newLabel;
                    }
                    isConverged = false;
                }
            }

            if (options.chineseWhispersClusteringMode() == ChineseWhispersClusteringMode.UPDATE_IN_BATCHES) {
                preventOscillations(labelsFromPreviousIteration, labels, newLabelsBuffer, numberOfNodes);
                System.arraycopy(labels, 0, labelsFromPreviousIteration, 0, numberOfNodes);
                System.arraycopy(newLabelsBuffer, 0, labels, 0, numberOfNodes);
            }
            numberOfIterations++;
        }
        logConvergence(isConverged, numberOfIterations);

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

    private Map<Integer, Double> calculateLabelCounts(RealMatrix similarityMatrix, int[] labels, int numberOfNodes, int node) {
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
        return labelCounts;
    }

    private int findLabelWithHighestCount(Map<Integer, Double> labelCounts) {
        int newLabel = -1;
        double currentMaxCount = Double.NEGATIVE_INFINITY;
        for (Map.Entry<Integer, Double> e : labelCounts.entrySet()) {
            if (e.getValue() > currentMaxCount) {
                currentMaxCount = e.getValue();
                newLabel = e.getKey();
            }
        }
        return newLabel;
    }

    /**
     * The batch updating strategy tends to run into oscillations. This usually happens when, in a cluster (or what is
     * supposed to be a cluster), two labels are equally "powerful". To still get a convergence, we force all oscillating
     * labels to exactly one of the competing labels (the one represented by the smaller integer, though one could have also
     * used any other decisive metric).
     */
    private void preventOscillations(int[] labelsFromPreviousIteration, int[] labels, int[] newLabelsBuffer, int numberOfNodes) {
        if (Arrays.equals(labelsFromPreviousIteration, newLabelsBuffer)) {
            for (int i = 0; i < numberOfNodes; i++) {
                if (labels[i] < newLabelsBuffer[i]) {
                    newLabelsBuffer[i] = labels[i];
                }
            }
        }
    }

    private void logConvergence(boolean isConverged, int numberOfIterations) {
        if (isConverged) {
            logger.info("Chinese Whispers clustering algorithm converged after {} iterations.", numberOfIterations);
        } else {
            logger.info("Chinese Whispers clustering algorithm failed to converge and was terminated after {} iterations.", numberOfIterations);
        }
    }
}
