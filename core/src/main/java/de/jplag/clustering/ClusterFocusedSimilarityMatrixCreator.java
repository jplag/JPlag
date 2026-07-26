package de.jplag.clustering;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.JPlagComparison;
import de.jplag.Submission;

/**
 * This is an advanced implementation of the SimilarityMatrixCreator interface.
 * <p>
 * Its goal is to set the similarity values of the resulting matrix in such a way, that running a clustering algorithm
 * on this matrix results in finding clusters of submissions that contain a lot of code that can be found in most of the
 * submissions in that cluster.
 * <p>
 * One can for example construct a scenario where three different submssions each have an average pairwise similarity of
 * 33% but do not share a single line of code. This matrix creator would, as long as an appropriate weighting mode is
 * being used (like {@link MatchGroupWeightingMode#NO_PAIRS}, or {@link MatchGroupWeightingMode#NO_PAIRS_ANTI_PROP}),
 * set their similarities in the resulting matrix to zero, making sure that they do not end up getting clustered
 * together. While this is an extreme example, it should already have a positive effect even in less extreme scenarios.
 * <p>
 * Note: This algorithm might not work perfectly with MatchMerging enabled, as the {@link GlobalMatchAppearanceFinder}
 * that is being used by this class, assumes that all matches are symmetric.
 */
public class ClusterFocusedSimilarityMatrixCreator implements SimilarityMatrixCreator {
    private final MatchGroupWeightingMode weightingMode;

    /**
     * Constructs a new matrix creator that uses the specified weighting mode.
     * @param weightingMode the weighting strategy to use
     */
    public ClusterFocusedSimilarityMatrixCreator(MatchGroupWeightingMode weightingMode) {
        this.weightingMode = weightingMode;
    }

    @Override
    public RealMatrix createSimilarityMatrix(Collection<JPlagComparison> comparisons, IntegerMapping<Submission> submissionsMap) {
        GlobalMatchAppearanceFinder finder = new GlobalMatchAppearanceFinder();
        List<GlobalMatchAppearanceFinder.TreeNode> trees = finder.findGlobalMatchAppearances(comparisons);
        return createMatrixFromTrees(trees, submissionsMap);
    }

    /**
     * Creates a similarity matrix based on the structure of all reappearing code, as specified by the passed trees.
     * @param trees the trees containing the summarized information about all the found matches
     * @param submissionsMap the map to use, to map submissions to matrix indices
     * @return the similarity matrix
     */
    public RealMatrix createMatrixFromTrees(List<GlobalMatchAppearanceFinder.TreeNode> trees, IntegerMapping<Submission> submissionsMap) {
        RealMatrix similarityMatrix = new Array2DRowRealMatrix(submissionsMap.size(), submissionsMap.size());
        for (GlobalMatchAppearanceFinder.TreeNode root : trees) {
            assignPoints(similarityMatrix, root, 0, submissionsMap);
        }
        makeSymmetric(similarityMatrix);
        return similarityMatrix;
    }

    private void assignPoints(RealMatrix matrix, GlobalMatchAppearanceFinder.TreeNode node, int lengthOfParent,
            IntegerMapping<Submission> submissionsMap) {
        int points = node.getLength() - lengthOfParent;
        double weightedPoints = points * weightingMode.getWeight(node.getSubmissions().size());
        increaseAllPairs(matrix, node.getSubmissions(), weightedPoints, submissionsMap);
        for (GlobalMatchAppearanceFinder.ChildCase childCase : GlobalMatchAppearanceFinder.ChildCase.values()) {
            for (GlobalMatchAppearanceFinder.TreeNode child : node.getChildrenOfCase(childCase)) {
                assignPoints(matrix, child, node.getLength(), submissionsMap);
            }
        }
    }

    private void increaseAllPairs(RealMatrix matrix, Set<Submission> submissions, double valueToIncreaseBy,
            IntegerMapping<Submission> submissionsMap) {
        if (weightingMode.skipGroupOfSize(submissions.size())) {
            return;
        }
        Set<Submission> remainingSubmissions = new HashSet<>(submissions);
        for (Submission submission : submissions) {
            remainingSubmissions.remove(submission);
            for (Submission otherSubmission : remainingSubmissions) {
                increaseSimilarity(matrix, submissionsMap.map(submission), submissionsMap.map(otherSubmission), valueToIncreaseBy);
            }
        }
    }

    private void increaseSimilarity(RealMatrix matrix, int submissionOne, int submissionTwo, double valueToIncreaseBy) {
        int idx1 = submissionOne;
        int idx2 = submissionTwo;
        if (idx1 > idx2) {
            idx1 = submissionTwo;
            idx2 = submissionOne;
        }
        matrix.addToEntry(idx1, idx2, valueToIncreaseBy);
    }

    private void makeSymmetric(RealMatrix matrix) {
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = i + 1; j < matrix.getRowDimension(); j++) {
                matrix.setEntry(j, i, matrix.getEntry(i, j));
            }
        }
    }
}
