package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Submission;

/**
 * This class provides {@link #findGlobalMatchAppearances(Collection) a method} for grouping all found matches into a
 * tree structure, summarizing what code is shared by which submissions.
 */
public class GlobalMatchAppearanceFinder {
    /**
     * Groups all the matches in the provided comparisons into a tree structure, summarizing what code is shared by which
     * submissions.
     * <p>
     * A node in the resulting trees always represents some section of code that can be found in all the submissions stored
     * in this node. Whether some submissions share some code or not is always purely decided based on the length and the
     * start indices of the matches (instead of actually matching tokens or something like that). A child of a node thereby
     * always represents an extension of its parent, meaning it describes some section of code that is longer than the one
     * described by the parent, but still fully contains the parent. As you go deeper into the tree, you therefor always
     * find more specific code that is only shared by few, while the roots describe some more generic parts of the code that
     * are likely shared by many.
     * <p>
     * For example if there is a match between submissions A and B that is 5 tokens long and starts at token 20 in A, and
     * there is another match between A and C, that is 10 tokens long and starts at token 22 in A, a tree will be created
     * with a root that starts at token 22 in A, is 3 tokens long, and has two child nodes. The first child (that extends
     * the root upwards) starts at token 20 in A and has a length of 5, and the second child (that extends the root
     * downwards) starts at token 22 and has a length of 10. If we get another match between A and D that starts at token 22
     * in A and has a length of 15, that will be added in an additional node as a child of the second child.
     * <p>
     * Note, that it does not matter that this third match was also between A and someone else, as we know due to
     * transitivity, that if A and C, as well as A and D both share that 10 long section (starting in A at token 22), that C
     * and D will also for sure share at least this 10 tokens long section. Therefor this tree representation of the matches
     * makes sense, as the transitivity guarantees us, that all the submissions in a certain node of the tree actually all
     * share the section of code represented by that node (however in practice, this transitivity might not actually be
     * fulfilled due to a variety of reasons, meaning the resulting trees might not always be 100% correct).
     * @param comparisons the comparisons to include in the trees
     * @return the found trees
     */
    public List<TreeNode> findGlobalMatchAppearances(Collection<JPlagComparison> comparisons) {
        Map<Submission, Map<Submission, JPlagComparison>> comparisonsMap = new HashMap<>();
        Set<Submission> allSubmissions = new HashSet<>();
        for (JPlagComparison comparison : comparisons) {
            addComparisonToMap(comparisonsMap, comparison.firstSubmission(), comparison.secondSubmission(), comparison);
            allSubmissions.add(comparison.firstSubmission());
            allSubmissions.add(comparison.secondSubmission());
        }

        // We need to manually iterate over all pairs of submissions, instead of just directly iterating over all comparisons,
        // as the algorithm assumes that all matches having a certain first sumbission get added together as a group.
        List<TreeNode> trees = new ArrayList<>();
        Map<Submission, Map<Submission, JPlagComparison>> comparisonsToHandleLater = new HashMap<>();
        Set<Submission> remainingSubmissions = new HashSet<>(allSubmissions);
        for (Submission firstSubmission : allSubmissions) {
            if (comparisonsToHandleLater.containsKey(firstSubmission)) {
                for (Map.Entry<Submission, JPlagComparison> entry : comparisonsToHandleLater.get(firstSubmission).entrySet()) {
                    for (Match match : entry.getValue().matches()) {
                        addOrExtend(trees, match, firstSubmission, entry.getKey());
                    }
                }
            }

            remainingSubmissions.remove(firstSubmission);
            for (Submission secondSubmission : remainingSubmissions) {
                if (comparisonsMap.containsKey(firstSubmission) && comparisonsMap.get(firstSubmission).containsKey(secondSubmission)) {
                    for (Match match : comparisonsMap.get(firstSubmission).get(secondSubmission).matches()) {
                        addOrExtend(trees, match, firstSubmission, secondSubmission);
                    }
                } else {
                    // This ensures the invariant mentioned above,
                    // that all comparisons with a certain first submission get handled together.
                    JPlagComparison comparisonInOtherDirection = comparisonsMap.get(secondSubmission).get(firstSubmission);
                    addComparisonToMap(comparisonsToHandleLater, secondSubmission, firstSubmission, comparisonInOtherDirection);
                }
            }
        }

        List<TreeNode> treesWithoutSuperRoot = new ArrayList<>();
        for (TreeNode tree : trees) {
            treesWithoutSuperRoot.add(tree.getActualTreeFromSuperRoot());
        }
        return treesWithoutSuperRoot;
    }

    private void addComparisonToMap(Map<Submission, Map<Submission, JPlagComparison>> comparisonsMap, Submission sub1, Submission sub2,
            JPlagComparison comparison) {
        if (comparisonsMap.containsKey(sub1)) {
            comparisonsMap.get(sub1).put(sub2, comparison);
        } else {
            Map<Submission, JPlagComparison> newInnerMap = new HashMap<>();
            newInnerMap.put(sub2, comparison);
            comparisonsMap.put(sub1, newInnerMap);
        }
    }

    private void addOrExtend(List<TreeNode> trees, Match match, Submission firstSubmission, Submission secondSubmission) {
        List<TreeNode> treesToAddTo = findAllRelevantTrees(trees, match, firstSubmission);
        if (treesToAddTo.isEmpty()) {
            TreeNode newTree = TreeNode.createNewNodeOf(match, firstSubmission, secondSubmission);
            TreeNode superRoot = TreeNode.createNewTreeOf(newTree);
            trees.add(superRoot);
            return;
        }
        for (TreeNode superRoot : treesToAddTo) {
            TreeNode currentNode = superRoot.getActualTreeFromSuperRoot();
            TreeNode parentNode = superRoot;
            boolean isAdded;
            do {
                TreeNode currentNodeBuffer = currentNode;
                int startInNode = currentNode.getStartInSub(firstSubmission);
                int endInNode = currentNode.getEndInSub(firstSubmission);
                if (startInNode == match.startOfFirst() && endInNode == match.endOfFirst()) {
                    currentNode.addAppearanceIn(secondSubmission, match.startOfSecond());
                    isAdded = true;
                } else if (startInNode > match.startOfFirst() && endInNode == match.endOfFirst()) {
                    currentNode = handleChildCase(currentNode, match, firstSubmission, secondSubmission, ChildCase.UP);
                    isAdded = currentNode == null;
                } else if (startInNode == match.startOfFirst() && endInNode < match.endOfFirst()) {
                    currentNode = handleChildCase(currentNode, match, firstSubmission, secondSubmission, ChildCase.DOWN);
                    isAdded = currentNode == null;
                } else if (startInNode > match.startOfFirst() && endInNode < match.endOfFirst()) {
                    currentNode = handleChildCase(currentNode, match, firstSubmission, secondSubmission, ChildCase.BOTH);
                    isAdded = currentNode == null;
                } else {
                    TreeNode newInBetweenNode = currentNode.createCopyIntersectedWithMatch(match, firstSubmission);
                    newInBetweenNode.addAppearanceIn(secondSubmission, match.startOfSecond());

                    // fix pointers
                    parentNode.removeChild(currentNode);
                    parentNode.addChild(newInBetweenNode, firstSubmission);
                    newInBetweenNode.addChild(currentNode, firstSubmission);

                    if (newInBetweenNode.getStartInSub(firstSubmission) != match.startOfFirst()
                            || newInBetweenNode.getEndInSub(firstSubmission) != match.endOfFirst()) {
                        TreeNode newNodeForMatch = TreeNode.createNewNodeOf(match, firstSubmission, secondSubmission);
                        newInBetweenNode.addChild(newNodeForMatch, firstSubmission);
                    } // or else, the match is equal to the in-between node, and is therefor already added
                    isAdded = true;
                }
                parentNode = currentNodeBuffer;
            } while (!isAdded);
        }
    }

    private List<TreeNode> findAllRelevantTrees(List<TreeNode> allTrees, Match match, Submission firstSubmission) {
        List<TreeNode> relevantTrees = new ArrayList<>();
        for (TreeNode superRoot : allTrees) {
            if (superRoot.getActualTreeFromSuperRoot().overlaps(match, firstSubmission)) {
                relevantTrees.add(superRoot);
            }
        }
        return relevantTrees;
    }

    private TreeNode handleChildCase(TreeNode currentNode, Match match, Submission firstSubmission, Submission secondSubmission,
            ChildCase childCase) {
        Optional<TreeNode> nextNode = currentNode.getChildForSubOfCase(firstSubmission, childCase);
        int startDifference = currentNode.getStartInSub(firstSubmission) - match.startOfFirst();
        currentNode.addAppearanceIn(secondSubmission, match.startOfSecond() + startDifference);
        if (nextNode.isPresent()) {
            return nextNode.get();
        } else {
            TreeNode newNode = TreeNode.createNewNodeOf(match, firstSubmission, secondSubmission);
            currentNode.addChild(newNode, childCase);
            return null;
        }
    }

    /**
     * The different cases a node can extend its parent by.
     * <p>
     * An extension can either have a smaller upper bound and the same lower bound, the same upper bound and a greater lower
     * bound, or both, so a smaller upper bound and a larger lower bound.
     */
    public enum ChildCase {
        UP,
        DOWN,
        BOTH
    }

    /**
     * Represents a node in the trees found by the {@link GlobalMatchAppearanceFinder}.
     * <p>
     * Each node represents a certain reappearing section of code, and stores the length of that code section, where that
     * code section starts in each of the submissions it appears in, and potentially what child nodes it has.
     */
    public static class TreeNode {
        private final Map<Submission, Integer> startInSubmission = new HashMap<>();
        private final int length;

        private final List<TreeNode> childUp = new ArrayList<>();
        private final List<TreeNode> childDown = new ArrayList<>();
        private final List<TreeNode> childBoth = new ArrayList<>();

        private ChildCase childCaseInParent;

        /* package-private */ TreeNode(int length) {
            this.length = length;
        }

        private static TreeNode createNewNodeOf(Match match, Submission firstSubmission, Submission secondSubmission) {
            TreeNode node = new TreeNode(match.minimumLength());
            node.addAppearanceIn(firstSubmission, match.startOfFirst());
            node.addAppearanceIn(secondSubmission, match.startOfSecond());
            return node;
        }

        /**
         * Wraps the tree represented by the passed node in the returned node object by making it its child. This means that the
         * reference to this super-root always stays the same, even if the root of the actual part of tree changes. This avoids
         * a lot of awkward special cases, since even changing the root of a tree always automatically gets handled correctly.
         */
        private static TreeNode createNewTreeOf(TreeNode rootOfNewTree) {
            TreeNode superRoot = new TreeNode(-1);
            // The only child of the super-root is always stored in childBoth.
            superRoot.addChild(rootOfNewTree, ChildCase.BOTH);
            return superRoot;
        }

        private TreeNode getActualTreeFromSuperRoot() {
            return childBoth.getFirst();
        }

        private TreeNode createCopyIntersectedWithMatch(Match match, Submission firstSubmission) {
            int intersectionStart = Math.max(getStartInSub(firstSubmission), match.startOfFirst());
            int intersectionEnd = Math.min(getEndInSub(firstSubmission), match.endOfFirst());
            int intersectionLength = intersectionEnd - intersectionStart + 1;
            int startDifference = intersectionStart - getStartInSub(firstSubmission);
            TreeNode newCopy = new TreeNode(intersectionLength);
            for (Map.Entry<Submission, Integer> e : startInSubmission.entrySet()) {
                newCopy.addAppearanceIn(e.getKey(), e.getValue() + startDifference);
            }
            return newCopy;
        }

        /* package-private */ void addAppearanceIn(Submission appearingInSubmission, int startInThatSubmission) {
            startInSubmission.put(appearingInSubmission, startInThatSubmission);
        }

        private void addChild(TreeNode childNode, Submission aSubmissionInBoth) {
            if (length == -1) {
                addChild(childNode, ChildCase.BOTH);
                return;
            }
            ChildCase childCase;
            if (childNode.getStartInSub(aSubmissionInBoth) == getStartInSub(aSubmissionInBoth)) {
                childCase = ChildCase.DOWN;
            } else if (childNode.getEndInSub(aSubmissionInBoth) == getEndInSub(aSubmissionInBoth)) {
                childCase = ChildCase.UP;
            } else {
                childCase = ChildCase.BOTH;
            }
            addChild(childNode, childCase);
        }

        /* package-private */ void addChild(TreeNode childNode, ChildCase childCase) {
            getChildrenOfCase(childCase).add(childNode);
            childNode.childCaseInParent = childCase;
        }

        private void removeChild(TreeNode childToRemove) {
            getChildrenOfCase(childToRemove.childCaseInParent).remove(childToRemove);
        }

        private Optional<TreeNode> getChildForSubOfCase(Submission submission, ChildCase childCase) {
            // The trees are constructed in such a way, that there only exists at most one such possible child.
            return getChildrenOfCase(childCase).stream().filter(n -> n.startInSubmission.containsKey(submission)).findFirst();
        }

        /**
         * Returns all the children of this node that extend it into the specified direction.
         * @param childCase the case the returned children extend this node by
         * @return the children
         */
        public List<TreeNode> getChildrenOfCase(ChildCase childCase) {
            return switch (childCase) {
                case UP -> childUp;
                case DOWN -> childDown;
                case BOTH -> childBoth;
            };
        }

        /**
         * Returns a set of all the submissions that contain the code represented by this node.
         * @return the submissions
         */
        public Set<Submission> getSubmissions() {
            return startInSubmission.keySet();
        }

        /**
         * Returns the length of the section of code represented by this node.
         * @return the length
         */
        public int getLength() {
            return length;
        }

        private int getStartInSub(Submission submission) {
            return startInSubmission.get(submission);
        }

        private int getEndInSub(Submission submission) {
            return startInSubmission.get(submission) + length - 1;
        }

        private boolean overlaps(Match match, Submission firstSubmission) {
            if (!startInSubmission.containsKey(firstSubmission)) {
                return false;
            }
            return getStartInSub(firstSubmission) <= match.endOfFirst() && getEndInSub(firstSubmission) >= match.startOfFirst();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TreeNode treeNode))
                return false;
            return length == treeNode.length && Objects.equals(startInSubmission, treeNode.startInSubmission)
                    && Objects.equals(childUp, treeNode.childUp) && Objects.equals(childDown, treeNode.childDown)
                    && Objects.equals(childBoth, treeNode.childBoth);
        }
    }
}
