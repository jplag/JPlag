package de.jplag.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Submission;

class GlobalMatchAppearanceFinderTest {
    private final Submission subA = createSubmission("A");
    private final Submission subB = createSubmission("B");
    private final Submission subC = createSubmission("C");
    private final Submission subD = createSubmission("D");
    private final Submission subE = createSubmission("E");
    private final Submission subF = createSubmission("F");

    @Test
    void testMatchTreeFinding() {
        List<JPlagComparison> comparisons = getSharedComparisons();
        addAllMissingComparisons(comparisons, List.of(subA, subB, subC, subD, subE, subF));
        GlobalMatchAppearanceFinder finder = new GlobalMatchAppearanceFinder();
        List<GlobalMatchAppearanceFinder.TreeNode> trees = finder.findGlobalMatchAppearances(comparisons);

        GlobalMatchAppearanceFinder.TreeNode root = getSharedExpected(subA, subB, subC, subD, subE, subF);
        GlobalMatchAppearanceFinder.TreeNode childUp2 = new GlobalMatchAppearanceFinder.TreeNode(4);
        root.addChild(childUp2, GlobalMatchAppearanceFinder.ChildCase.UP);
        addAll(childUp2, List.of(new Pair<>(subC, 3), new Pair<>(subF, 2)));

        assertEquals(List.of(root), trees);
        assertTrue(trees.getFirst().checkIfConsistent());
        ensureAllDuplicateCountsAreOne(trees.getFirst());
    }

    @Test
    void testMatchTreeNodeMerging() {
        // Here, instead of A-B and C-F having a different token at the spot above the root, all four have the same.
        // This is achieved by adding that additional match between F and B, which due to transitivity guarantees us,
        // that all 4 must have the same token at that spot (e.g. if C and F have same, and F and B as well,
        // then we also know that C and B have the same, and so on between all pairs).
        // So in theory, we would also have a match for that token between A and C, and A and F, and the global match finding
        // would work perfectly without any special case handling.
        // However, due to many possible reasons, JPlag might not find these matches, but we still want all four to be
        // grouped together in one node, which is exactly what we test against in this advanced scenario.
        List<JPlagComparison> comparisons = getSharedComparisons();
        comparisons.add(new JPlagComparison(subF, subB, List.of(new Match(2, 5, 4, 4)), List.of()));
        addAllMissingComparisons(comparisons, List.of(subA, subB, subC, subD, subE, subF));
        GlobalMatchAppearanceFinder finder = new GlobalMatchAppearanceFinder();
        List<GlobalMatchAppearanceFinder.TreeNode> trees = finder.findGlobalMatchAppearances(comparisons);

        GlobalMatchAppearanceFinder.TreeNode root = getSharedExpected(subA, subB, subC, subD, subE, subF);
        GlobalMatchAppearanceFinder.TreeNode childUp1 = root.getChildrenOfCase(GlobalMatchAppearanceFinder.ChildCase.UP).getFirst();
        addAll(childUp1, List.of(new Pair<>(subC, 3), new Pair<>(subF, 2)));

        assertEquals(List.of(root), trees);
        assertTrue(trees.getFirst().checkIfConsistent());
        ensureAllDuplicateCountsAreOne(trees.getFirst());
    }

    private List<JPlagComparison> getSharedComparisons() {
        List<JPlagComparison> comparisons = new ArrayList<>();
        comparisons.add(new JPlagComparison(subA, subB, List.of(new Match(1, 5, 4, 4)), List.of()));
        comparisons.add(new JPlagComparison(subA, subC, List.of(new Match(2, 4, 4, 4)), List.of()));
        comparisons.add(new JPlagComparison(subA, subD, List.of(new Match(2, 3, 6, 6)), List.of()));
        comparisons.add(new JPlagComparison(subA, subE, List.of(new Match(1, 20, 7, 7)), List.of()));
        comparisons.add(new JPlagComparison(subC, subF, List.of(new Match(3, 2, 4, 4)), List.of()));
        comparisons.add(new JPlagComparison(subC, subD, List.of(new Match(4, 3, 4, 4)), List.of()));
        return comparisons;
    }

    private GlobalMatchAppearanceFinder.TreeNode getSharedExpected(Submission subA, Submission subB, Submission subC, Submission subD,
            Submission subE, Submission subF) {
        GlobalMatchAppearanceFinder.TreeNode root = new GlobalMatchAppearanceFinder.TreeNode(3);
        GlobalMatchAppearanceFinder.TreeNode childUp1 = new GlobalMatchAppearanceFinder.TreeNode(4);
        GlobalMatchAppearanceFinder.TreeNode childDown = new GlobalMatchAppearanceFinder.TreeNode(4);
        GlobalMatchAppearanceFinder.TreeNode childDownDown = new GlobalMatchAppearanceFinder.TreeNode(6);
        GlobalMatchAppearanceFinder.TreeNode childBoth = new GlobalMatchAppearanceFinder.TreeNode(7);

        root.addChild(childUp1, GlobalMatchAppearanceFinder.ChildCase.UP);
        root.addChild(childDown, GlobalMatchAppearanceFinder.ChildCase.DOWN);
        childDown.addChild(childDownDown, GlobalMatchAppearanceFinder.ChildCase.DOWN);
        root.addChild(childBoth, GlobalMatchAppearanceFinder.ChildCase.BOTH);

        addAll(root, List.of(new Pair<>(subA, 2), new Pair<>(subB, 6), new Pair<>(subC, 4), new Pair<>(subD, 3), new Pair<>(subE, 21),
                new Pair<>(subF, 3)));
        addAll(childUp1, List.of(new Pair<>(subA, 1), new Pair<>(subB, 5)));
        addAll(childDown, List.of(new Pair<>(subA, 2), new Pair<>(subC, 4), new Pair<>(subD, 3)));
        addAll(childDownDown, List.of(new Pair<>(subA, 2), new Pair<>(subD, 3)));
        addAll(childBoth, List.of(new Pair<>(subA, 1), new Pair<>(subE, 20)));
        return root;
    }

    private void addAllMissingComparisons(List<JPlagComparison> comparisons, List<Submission> submissions) {
        List<Submission> remainingSubs = new ArrayList<>(submissions);
        for (Submission submission : submissions) {
            remainingSubs.remove(submission);
            for (Submission otherSubmission : remainingSubs) {
                if (comparisons.stream().noneMatch(c -> c.firstSubmission() == submission && c.secondSubmission() == otherSubmission
                        || c.firstSubmission() == otherSubmission && c.secondSubmission() == submission)) {
                    comparisons.add(new JPlagComparison(submission, otherSubmission, List.of(), List.of()));
                }
            }
        }
    }

    @Test
    void testInvalidMatchesBeingIgnored() {
        // Sometimes, matches describe contradicting situations.
        // If we stumble over such a situation, we need to make sure it still gets handled smoothly.
        // In the following test, the "same" section of code begins in C both at token 0 and at token 30.
        List<JPlagComparison> comparisons = new ArrayList<>();
        comparisons.add(new JPlagComparison(subA, subC, List.of(new Match(0, 0, 10, 10)), List.of()));
        comparisons.add(new JPlagComparison(subA, subB, List.of(new Match(1, 20, 10, 10)), List.of()));
        comparisons.add(new JPlagComparison(subB, subC, List.of(new Match(20, 30, 10, 10)), List.of()));
        GlobalMatchAppearanceFinder finder = new GlobalMatchAppearanceFinder();
        List<GlobalMatchAppearanceFinder.TreeNode> trees = finder.findGlobalMatchAppearances(comparisons);

        // The finder should handle this, by ignoring all the matches that would introduce an inconsistency.
        GlobalMatchAppearanceFinder.TreeNode root = new GlobalMatchAppearanceFinder.TreeNode(9);
        GlobalMatchAppearanceFinder.TreeNode up = new GlobalMatchAppearanceFinder.TreeNode(10);
        GlobalMatchAppearanceFinder.TreeNode down = new GlobalMatchAppearanceFinder.TreeNode(10);
        root.addChild(up, GlobalMatchAppearanceFinder.ChildCase.UP);
        root.addChild(down, GlobalMatchAppearanceFinder.ChildCase.DOWN);
        addAll(root, List.of(new Pair<>(subA, 1), new Pair<>(subB, 20), new Pair<>(subC, 1)));
        addAll(up, List.of(new Pair<>(subA, 0), new Pair<>(subC, 0)));
        addAll(down, List.of(new Pair<>(subA, 1), new Pair<>(subB, 20)));
        assertEquals(List.of(root), trees);
    }

    private void ensureAllDuplicateCountsAreOne(GlobalMatchAppearanceFinder.TreeNode root) {
        for (GlobalMatchAppearanceFinder.CountReference countReference : root.getDuplicateCounts().values()) {
            assertEquals(1, countReference.getCount());
        }
        for (GlobalMatchAppearanceFinder.ChildCase childCase : GlobalMatchAppearanceFinder.ChildCase.values()) {
            for (GlobalMatchAppearanceFinder.TreeNode child : root.getChildrenOfCase(childCase)) {
                ensureAllDuplicateCountsAreOne(child);
            }
        }
    }

    @Test
    void testAntiDuplicateWeights() {
        List<JPlagComparison> comparisons = new ArrayList<>();
        Match m1 = new Match(0, 0, 10, 10);
        Match m2 = new Match(20, 20, 10, 10);
        comparisons.add(new JPlagComparison(subA, subB, List.of(m1, m2), List.of()));
        comparisons.add(new JPlagComparison(subA, subC, List.of(new Match(0, 0, 30, 30)), List.of()));
        comparisons.add(new JPlagComparison(subB, subC, List.of(), List.of()));
        GlobalMatchAppearanceFinder finder = new GlobalMatchAppearanceFinder();
        List<GlobalMatchAppearanceFinder.TreeNode> trees = finder.findGlobalMatchAppearances(comparisons);

        // roots
        assertEquals(2, trees.size());

        assertEquals(3, trees.getFirst().getDuplicateCounts().size());
        assertEquals(1, trees.getFirst().getDuplicateCounts().get(subA).getCount());
        assertEquals(1, trees.getFirst().getDuplicateCounts().get(subB).getCount());
        assertEquals(2, trees.getFirst().getDuplicateCounts().get(subC).getCount());

        assertEquals(3, trees.get(1).getDuplicateCounts().size());
        assertEquals(1, trees.get(1).getDuplicateCounts().get(subA).getCount());
        assertEquals(1, trees.get(1).getDuplicateCounts().get(subB).getCount());
        assertEquals(2, trees.get(1).getDuplicateCounts().get(subC).getCount());

        // children
        GlobalMatchAppearanceFinder.TreeNode child1;
        GlobalMatchAppearanceFinder.TreeNode child2;
        // One tree has the up, and the other has the down child, but we do not know which one has which.
        if (!trees.getFirst().getChildrenOfCase(GlobalMatchAppearanceFinder.ChildCase.UP).isEmpty()) {
            child1 = trees.getFirst().getChildrenOfCase(GlobalMatchAppearanceFinder.ChildCase.UP).getFirst();
            child2 = trees.get(1).getChildrenOfCase(GlobalMatchAppearanceFinder.ChildCase.DOWN).getFirst();
        } else {
            child1 = trees.getFirst().getChildrenOfCase(GlobalMatchAppearanceFinder.ChildCase.DOWN).getFirst();
            child2 = trees.get(1).getChildrenOfCase(GlobalMatchAppearanceFinder.ChildCase.UP).getFirst();
        }
        assertEquals(2, child1.getDuplicateCounts().size());
        assertEquals(2, child1.getDuplicateCounts().get(subA).getCount());
        assertEquals(2, child1.getDuplicateCounts().get(subC).getCount());

        assertEquals(2, child2.getDuplicateCounts().size());
        assertEquals(2, child2.getDuplicateCounts().get(subA).getCount());
        assertEquals(2, child2.getDuplicateCounts().get(subC).getCount());
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
