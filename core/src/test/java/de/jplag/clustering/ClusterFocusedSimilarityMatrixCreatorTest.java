package de.jplag.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;

import de.jplag.Submission;

class ClusterFocusedSimilarityMatrixCreatorTest {
    @Test
    void testMatrixCreation() {
        GlobalMatchAppearanceFinder.TreeNode root = new GlobalMatchAppearanceFinder.TreeNode(3);
        GlobalMatchAppearanceFinder.TreeNode child1 = new GlobalMatchAppearanceFinder.TreeNode(6);
        GlobalMatchAppearanceFinder.TreeNode child2 = new GlobalMatchAppearanceFinder.TreeNode(5);
        GlobalMatchAppearanceFinder.TreeNode child2child = new GlobalMatchAppearanceFinder.TreeNode(8);
        GlobalMatchAppearanceFinder.TreeNode childD = new GlobalMatchAppearanceFinder.TreeNode(4);
        root.addChild(child1, GlobalMatchAppearanceFinder.ChildCase.UP);
        root.addChild(child2, GlobalMatchAppearanceFinder.ChildCase.BOTH);
        child2.addChild(child2child, GlobalMatchAppearanceFinder.ChildCase.UP);
        root.addChild(childD, GlobalMatchAppearanceFinder.ChildCase.UP);
        Submission subA = createSubmission("A");
        Submission subB = createSubmission("B");
        Submission subC = createSubmission("C");
        Submission subD = createSubmission("D");
        addAll(root, List.of(subA, subB, subC, subD));
        addAll(child1, List.of(subA, subB));
        addAll(child2, List.of(subA, subB, subC));
        addAll(child2child, List.of(subB, subC));
        addAll(childD, List.of(subC, subD));

        IntegerMapping<Submission> submissionsMap = new IntegerMapping<>(4);
        submissionsMap.map(subA);
        submissionsMap.map(subB);
        submissionsMap.map(subC);
        submissionsMap.map(subD);

        for (ModeTestSet mode : ModeTestSet.values()) {
            ClusterFocusedSimilarityMatrixCreator matrixCreator = new ClusterFocusedSimilarityMatrixCreator(mode.weightingMode);
            RealMatrix matrix = matrixCreator.createMatrixFromTrees(List.of(root), submissionsMap);
            assertEquals(mode.resultMatrix, matrix);
        }
    }

    @Test
    void testAntiDuplicateWeights() {
        Submission subA = createSubmission("A");
        Submission subB = createSubmission("B");
        GlobalMatchAppearanceFinder.TreeNode root = new GlobalMatchAppearanceFinder.TreeNode(10);
        root.addAppearanceIn(subA, 0, new GlobalMatchAppearanceFinder.CountReference(2));
        root.addAppearanceIn(subB, 0, new GlobalMatchAppearanceFinder.CountReference(2));

        IntegerMapping<Submission> submissionsMap = new IntegerMapping<>(2);
        submissionsMap.map(subA);
        submissionsMap.map(subB);

        ClusterFocusedSimilarityMatrixCreator matrixCreator = new ClusterFocusedSimilarityMatrixCreator(MatchGroupWeightingMode.NO_WEIGHTING);
        RealMatrix matrix = matrixCreator.createMatrixFromTrees(List.of(root), submissionsMap);

        RealMatrix expected = new Array2DRowRealMatrix(new double[][] {new double[] {0, 5}, new double[] {5, 0}});
        assertEquals(expected, matrix);
    }

    private Submission createSubmission(String name) {
        return new Submission(name, null, true, null, null);
    }

    private void addAll(GlobalMatchAppearanceFinder.TreeNode node, List<Submission> submissions) {
        for (Submission submission : submissions) {
            node.addAppearanceIn(submission, 0, new GlobalMatchAppearanceFinder.CountReference(1));
        }
    }

    private enum ModeTestSet {
        NO_WEIGHTING(
                MatchGroupWeightingMode.NO_WEIGHTING,
                new double[][] {new double[] {0, 3 + (6 - 3) + (5 - 3), 3 + (5 - 3), 3},
                        new double[] {3 + (6 - 3) + (5 - 3), 0, 3 + (5 - 3) + (8 - 5), 3},
                        new double[] {3 + (5 - 3), 3 + (5 - 3) + (8 - 5), 0, 3 + (4 - 3)}, new double[] {3, 3, 3 + (4 - 3), 0}}),
        NO_PAIRS(
                MatchGroupWeightingMode.NO_PAIRS,
                new double[][] {new double[] {0, 3 + (5 - 3), 3 + (5 - 3), 3}, new double[] {3 + (5 - 3), 0, 3 + (5 - 3), 3},
                        new double[] {3 + (5 - 3), 3 + (5 - 3), 0, 3}, new double[] {3, 3, 3, 0}}),
        ANTI_PROPORTIONAL(
                MatchGroupWeightingMode.ANTI_PROPORTIONAL,
                new double[][] {new double[] {0, 3.0 / 4 + (6 - 3) / 2.0 + (5 - 3) / 3.0, 3.0 / 4 + (5 - 3) / 3.0, 3.0 / 4},
                        new double[] {3.0 / 4 + (6 - 3) / 2.0 + (5 - 3) / 3.0, 0, 3.0 / 4 + (5 - 3) / 3.0 + (8 - 5) / 2.0, 3.0 / 4},
                        new double[] {3.0 / 4 + (5 - 3) / 3.0, 3.0 / 4 + (5 - 3) / 3.0 + (8 - 5) / 2.0, 0, 3.0 / 4 + (4 - 3) / 2.0},
                        new double[] {3.0 / 4, 3.0 / 4, 3.0 / 4 + (4 - 3) / 2.0, 0}}),
        NO_PAIRS_ANTI_PROP(
                MatchGroupWeightingMode.NO_PAIRS_ANTI_PROP,
                new double[][] {new double[] {0, 3 / 4.0 + (5 - 3) / 3.0, 3 / 4.0 + (5 - 3) / 3.0, 3 / 4.0},
                        new double[] {3 / 4.0 + (5 - 3) / 3.0, 0, 3 / 4.0 + (5 - 3) / 3.0, 3 / 4.0},
                        new double[] {3 / 4.0 + (5 - 3) / 3.0, 3 / 4.0 + (5 - 3) / 3.0, 0, 3 / 4.0}, new double[] {3 / 4.0, 3 / 4.0, 3 / 4.0, 0}});

        private final MatchGroupWeightingMode weightingMode;
        private final RealMatrix resultMatrix;

        ModeTestSet(MatchGroupWeightingMode weightingMode, double[][] resultMatrixArray) {
            this.weightingMode = weightingMode;
            this.resultMatrix = new Array2DRowRealMatrix(resultMatrixArray);
        }
    }
}
