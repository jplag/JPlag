package de.jplag.clustering.algorithm;

/**
 * This enum represents different strategies for updating the labels in the Chinese Whispers algorithm.
 */
public enum ChineseWhispersClusteringMode {
    /**
     * During each iteration, all the nodes get visited in a random order and the newly calculated label of a node
     * immediately gets written back, so the remaining nodes in this iteration already use its new label.
     * <p>
     * This is the original Chinese Whispers clustering algorithm, as introduced in “Chinese whispers-an efficient graph
     * clustering algorithm and its application to natural language processing problems” by Chris Biemann. In: Proceedings
     * of TextGraphs: the first workshop on graph based methods for natural language processing. 2006, pp. 73–80.
     */
    UPDATE_IMMEDIATELY_RANDOMIZED,
    /**
     * During each iteration, all the nodes get visited in some arbitrary, but fixed order and the newly calculated label of
     * a node immediately gets written back, so the remaining nodes in this iteration already use its new label.
     */
    UPDATE_IMMEDIATELY_DETERMINISTIC,
    /**
     * During each iteration, all nodes calculate their new label based on the labels from last iteration and write their
     * new label into a buffer. Only at the end of the iteration do the nodes get their new labels assigned, based on the
     * labels from the buffer.
     * <p>
     * This has the advantage, that the clustering result is independent of the order in which we visit the nodes to update
     * their label, does however result in slower convergence.
     */
    UPDATE_IN_BATCHES;

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace('_', ' ');
    }
}
