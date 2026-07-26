package de.jplag.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Submission;

class GlobalMatchAppearanceFinderTest {
    @Test
    void testMatchTreeFinding() {
        List<JPlagComparison> comparisons = new ArrayList<>();
        Submission subA = createSubmission("A");
        Submission subB = createSubmission("B");
        Submission subC = createSubmission("C");
        Submission subD = createSubmission("D");
        Submission subE = createSubmission("E");
        Submission subF = createSubmission("F");
        fillWithEmptyComparisons(comparisons, List.of(subA, subB, subC, subD, subE, subF));
        comparisons.add(new JPlagComparison(subA, subB, List.of(new Match(1, 5, 4, 4)), List.of()));
        comparisons.add(new JPlagComparison(subA, subC, List.of(new Match(2, 4, 4, 4)), List.of()));
        comparisons.add(new JPlagComparison(subA, subD, List.of(new Match(2, 3, 6, 6)), List.of()));
        comparisons.add(new JPlagComparison(subA, subE, List.of(new Match(1, 20, 7, 7)), List.of()));
        comparisons.add(new JPlagComparison(subC, subF, List.of(new Match(3, 2, 4, 4)), List.of()));
        comparisons.add(new JPlagComparison(subC, subD, List.of(new Match(4, 3, 4, 4)), List.of()));

        GlobalMatchAppearanceFinder finder = new GlobalMatchAppearanceFinder();
        List<GlobalMatchAppearanceFinder.TreeNode> trees = finder.findGlobalMatchAppearances(comparisons);

        // expected
        GlobalMatchAppearanceFinder.TreeNode root = new GlobalMatchAppearanceFinder.TreeNode(3);
        GlobalMatchAppearanceFinder.TreeNode childUp1 = new GlobalMatchAppearanceFinder.TreeNode(4);
        GlobalMatchAppearanceFinder.TreeNode childUp2 = new GlobalMatchAppearanceFinder.TreeNode(4);
        GlobalMatchAppearanceFinder.TreeNode childDown = new GlobalMatchAppearanceFinder.TreeNode(4);
        GlobalMatchAppearanceFinder.TreeNode childDownDown = new GlobalMatchAppearanceFinder.TreeNode(6);
        GlobalMatchAppearanceFinder.TreeNode childBoth = new GlobalMatchAppearanceFinder.TreeNode(7);
        root.addChild(childUp1, GlobalMatchAppearanceFinder.ChildCase.UP);
        root.addChild(childUp2, GlobalMatchAppearanceFinder.ChildCase.UP);
        root.addChild(childDown, GlobalMatchAppearanceFinder.ChildCase.DOWN);
        childDown.addChild(childDownDown, GlobalMatchAppearanceFinder.ChildCase.DOWN);
        root.addChild(childBoth, GlobalMatchAppearanceFinder.ChildCase.BOTH);
        addAll(root, List.of(new Pair<>(subA, 2), new Pair<>(subB, 6), new Pair<>(subC, 4), new Pair<>(subD, 3), new Pair<>(subE, 21),
                new Pair<>(subF, 3)));
        addAll(childUp1, List.of(new Pair<>(subA, 1), new Pair<>(subB, 5)));
        addAll(childUp2, List.of(new Pair<>(subC, 3), new Pair<>(subF, 2)));
        addAll(childDown, List.of(new Pair<>(subA, 2), new Pair<>(subC, 4), new Pair<>(subD, 3)));
        addAll(childDownDown, List.of(new Pair<>(subA, 2), new Pair<>(subD, 3)));
        addAll(childBoth, List.of(new Pair<>(subA, 1), new Pair<>(subE, 20)));

        assertEquals(List.of(root), trees);
    }

    private void fillWithEmptyComparisons(List<JPlagComparison> comparisons, List<Submission> submissions) {
        List<Submission> remainingSubs = new ArrayList<>(submissions);
        for (Submission submission : submissions) {
            remainingSubs.remove(submission);
            for (Submission otherSubmission : remainingSubs) {
                comparisons.add(new JPlagComparison(submission, otherSubmission, List.of(), List.of()));
            }
        }
    }

    private Submission createSubmission(String name) {
        return new Submission(name, null, true, null, null);
    }

    private void addAll(GlobalMatchAppearanceFinder.TreeNode node, List<Pair<Submission, Integer>> starts) {
        for (Pair<Submission, Integer> p : starts) {
            node.addAppearanceIn(p.getFirst(), p.getSecond());
        }
    }
}
