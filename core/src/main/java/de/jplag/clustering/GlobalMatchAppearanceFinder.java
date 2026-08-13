package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
     * Additionally to matches sometimes describing contradicting situations, another problem that should in theory not
     * happen, but still does in practice, is the fact that the matches are sometimes incomplete. This means, that for
     * example when submissions A, B, C and D all share some section of code, in theory we would expect a match for that
     * section of code between all pairs. However in reality, we might only get the matches A-D, B-C and B-D. If we add them
     * in this order, we first create a node for A and D, then another one for B and C, and then we would add D to the node
     * with B and C. Since the trees however have the invariant, that for each child case they have at most one child with a
     * certain submission, we either have to discard the match B-D, or, what this boolean toggles, merge the two nodes
     * together. Merging them would obviously create the more correct result, however it does typically also result in many
     * more inconsistencies being found, and can therefor change the content and structure of a tree by a considerable
     * amount. Because of this, there is already this toggle implemented, as one might want to turn that into a real setting
     * in the future.
     */
    private static final boolean NODE_MERGING_IS_ENABLED = true;

    /**
     * Compares trees (super-roots) by the appearance of the current first submission (index to first token) in the root.
     */
    private Comparator<TreeNode> currentComparator;

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
        Set<TreeNode> trees = new HashSet<>();
        Map<Submission, Map<Submission, JPlagComparison>> comparisonsToHandleLater = new HashMap<>();
        Set<Submission> remainingSubmissions = new HashSet<>(allSubmissions);
        for (Submission firstSubmission : allSubmissions) {
            List<TreeNode> relevantTrees = findAllRelevantTrees(trees, firstSubmission);
            currentComparator = Comparator.comparingInt(t -> t.getActualTreeFromSuperRoot().getStartInSub(firstSubmission));
            relevantTrees.sort(currentComparator);
            int longestLengthOfATree = relevantTrees.stream().map(TreeNode::getActualTreeFromSuperRoot).mapToInt(TreeNode::getLength).max().orElse(0);

            if (comparisonsToHandleLater.containsKey(firstSubmission)) {
                for (Map.Entry<Submission, JPlagComparison> entry : comparisonsToHandleLater.get(firstSubmission).entrySet()) {
                    for (Match match : entry.getValue().matches()) {
                        longestLengthOfATree = addOrExtend(relevantTrees, longestLengthOfATree, match, firstSubmission, entry.getKey());
                    }
                }
            }

            remainingSubmissions.remove(firstSubmission);
            for (Submission secondSubmission : remainingSubmissions) {
                if (comparisonsMap.containsKey(firstSubmission) && comparisonsMap.get(firstSubmission).containsKey(secondSubmission)) {
                    for (Match match : comparisonsMap.get(firstSubmission).get(secondSubmission).matches()) {
                        longestLengthOfATree = addOrExtend(relevantTrees, longestLengthOfATree, match, firstSubmission, secondSubmission);
                    }
                } else {
                    // This ensures the invariant mentioned above,
                    // that all comparisons with a certain first submission get handled together.
                    JPlagComparison comparisonInOtherDirection = comparisonsMap.get(secondSubmission).get(firstSubmission);
                    addComparisonToMap(comparisonsToHandleLater, secondSubmission, firstSubmission, comparisonInOtherDirection);
                }
            }

            // Add any new trees back to the main set.
            trees.addAll(relevantTrees);
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

    private List<TreeNode> findAllRelevantTrees(Set<TreeNode> allTrees, Submission submissionThatMustAppear) {
        List<TreeNode> relevantTrees = new ArrayList<>();
        for (TreeNode superRoot : allTrees) {
            if (superRoot.getActualTreeFromSuperRoot().startInSubmission.containsKey(submissionThatMustAppear)) {
                relevantTrees.add(superRoot);
            }
        }
        return relevantTrees;
    }

    private int addOrExtend(List<TreeNode> relevantTrees, int longestLengthOfATree, Match match, Submission firstSubmission,
            Submission secondSubmission) {
        List<TreeNode> treesToAddTo = findAllTreesToAddMatchTo(relevantTrees, longestLengthOfATree, match, firstSubmission);
        if (treesToAddTo.isEmpty()) {
            TreeNode newTree = TreeNode.createNewNodeOf(match, firstSubmission, secondSubmission, new CountReference(1));
            TreeNode superRoot = TreeNode.createNewTreeOf(newTree);
            int indexToAddAt = Collections.binarySearch(relevantTrees, superRoot, currentComparator);
            if (indexToAddAt < 0) {
                indexToAddAt = -(indexToAddAt + 1);
            }
            relevantTrees.add(indexToAddAt, superRoot);
            return Math.max(longestLengthOfATree, newTree.length);
        }
        CountReference duplicateCount = new CountReference(treesToAddTo.size());
        for (TreeNode superRoot : treesToAddTo) {
            TreeNode currentNode = superRoot.getActualTreeFromSuperRoot();
            int startIndexBeforeAdding = currentNode.getStartInSub(firstSubmission);
            boolean isAdded;
            try {
                do {
                    int startInNode = currentNode.getStartInSub(firstSubmission);
                    int endInNode = currentNode.getEndInSub(firstSubmission);
                    if (startInNode == match.startOfFirst() && endInNode == match.endOfFirst()) {
                        currentNode.addAppearanceInWithCheck(secondSubmission, match.startOfSecond(), duplicateCount);
                        isAdded = true;
                    } else if (startInNode > match.startOfFirst() && endInNode == match.endOfFirst()) {
                        currentNode = handleChildCase(currentNode, match, firstSubmission, secondSubmission, ChildCase.UP, duplicateCount);
                        isAdded = currentNode == null;
                    } else if (startInNode == match.startOfFirst() && endInNode < match.endOfFirst()) {
                        currentNode = handleChildCase(currentNode, match, firstSubmission, secondSubmission, ChildCase.DOWN, duplicateCount);
                        isAdded = currentNode == null;
                    } else if (startInNode > match.startOfFirst() && endInNode < match.endOfFirst()) {
                        currentNode = handleChildCase(currentNode, match, firstSubmission, secondSubmission, ChildCase.BOTH, duplicateCount);
                        isAdded = currentNode == null;
                    } else { // remaining: == > , > > , < < , < == , < >
                        TreeNode newInBetweenNode = currentNode.createCopyIntersectedWithMatch(match, firstSubmission);

                        TreeNode parentNode = currentNode.parent;
                        parentNode.removeChild(currentNode);
                        parentNode.addChildAsCorrectCase(newInBetweenNode, firstSubmission);
                        newInBetweenNode.addChildAsCorrectCase(currentNode, firstSubmission);

                        int difference = Math.max(match.startOfFirst(), newInBetweenNode.getStartInSub(firstSubmission)) - match.startOfFirst();
                        newInBetweenNode.addAppearanceInWithCheck(secondSubmission, match.startOfSecond() + difference, duplicateCount);
                        ensureAddingProcessIsStillValid(newInBetweenNode, firstSubmission);

                        if (newInBetweenNode.getStartInSub(firstSubmission) != match.startOfFirst()
                                || newInBetweenNode.getEndInSub(firstSubmission) != match.endOfFirst()) {
                            TreeNode newNodeForMatch = TreeNode.createNewNodeOf(match, firstSubmission, secondSubmission, duplicateCount);
                            newInBetweenNode.addChildAsCorrectCase(newNodeForMatch, firstSubmission);
                        } // or else, the match is equal to the in-between node, and is therefor already added
                        isAdded = true;
                    }
                } while (!isAdded);
            } catch (InconsistentMatchException _) {
                // The situation described by this match was inconsistent with the state of the tree, and its inclusion was therefor
                // canceled.
                // Therefor we also need to decrease the count of how many trees this match was added to.
                duplicateCount.count--;
            }
            if (superRoot.getActualTreeFromSuperRoot().getStartInSub(firstSubmission) != startIndexBeforeAdding) {
                // If the root of the current tree changed, then the ordering of the trees might now not be correct anymore.
                // To fix this, we just sort the list again, which does not have any significant performance impact as this case is
                // quite rare (and the sort method is also fairly fast on an almost sorted list).
                relevantTrees.sort(currentComparator);
            }
        }
        return longestLengthOfATree;
    }

    private List<TreeNode> findAllTreesToAddMatchTo(List<TreeNode> allRelevantTrees, int longestLengthOfATree, Match match,
            Submission firstSubmission) {
        // We create a dummy node that wraps our match and returns the end of the match as its "start".
        // That way, binary searching for it gives us one of the furthest back trees whose root still overlaps with the match.
        int largestMatchingTokenIndex = match.endOfFirst();
        TreeNode wrapperNode = new TreeNode(-1);
        wrapperNode.addAppearanceIn(firstSubmission, largestMatchingTokenIndex);
        int lastMatchingTreeIndex = Collections.binarySearch(allRelevantTrees, TreeNode.createNewTreeOf(wrapperNode), currentComparator);
        if (lastMatchingTreeIndex < 0) {
            int insertionPoint = -(lastMatchingTreeIndex + 1);
            lastMatchingTreeIndex = insertionPoint - 1;
        } else {
            // The binary search only gives us any match, we still need to manually walk to the last match.
            while (lastMatchingTreeIndex + 1 < allRelevantTrees.size() && allRelevantTrees.get(lastMatchingTreeIndex + 1).getActualTreeFromSuperRoot()
                    .getStartInSub(firstSubmission) == largestMatchingTokenIndex) {
                lastMatchingTreeIndex++;
            }
        }

        // Same thing for the finding the index to the first tree that is still guaranteed to overlap with the match.
        // All trees before that might, but might also not, overlap with the match, depending on their length.
        int smallestGuaranteedMatchingTokenIndex = match.startOfFirst();
        wrapperNode = new TreeNode(-1);
        wrapperNode.addAppearanceIn(firstSubmission, smallestGuaranteedMatchingTokenIndex);
        int firstGuaranteedMatchingTreeIndex = Collections.binarySearch(allRelevantTrees, TreeNode.createNewTreeOf(wrapperNode), currentComparator);
        if (firstGuaranteedMatchingTreeIndex < 0) {
            firstGuaranteedMatchingTreeIndex = -(firstGuaranteedMatchingTreeIndex + 1);
        } else {
            while (firstGuaranteedMatchingTreeIndex - 1 >= 0 && allRelevantTrees.get(firstGuaranteedMatchingTreeIndex - 1)
                    .getActualTreeFromSuperRoot().getStartInSub(firstSubmission) == smallestGuaranteedMatchingTokenIndex) {
                firstGuaranteedMatchingTreeIndex--;
            }
        }

        // Get all guaranteed matching trees.
        List<TreeNode> treesToAddTo = new ArrayList<>(allRelevantTrees.subList(firstGuaranteedMatchingTreeIndex, lastMatchingTreeIndex + 1));

        // Add all remaining matching trees from the potential ones in the front.
        int currentTreeIndex = firstGuaranteedMatchingTreeIndex - 1;
        while (currentTreeIndex >= 0 && allRelevantTrees.get(currentTreeIndex).getActualTreeFromSuperRoot().getStartInSub(firstSubmission)
                + longestLengthOfATree > match.startOfFirst()) {
            if (allRelevantTrees.get(currentTreeIndex).getActualTreeFromSuperRoot().overlaps(match, firstSubmission)) {
                // The order of the resulting trees is not relevant, so we can just add these at the back.
                treesToAddTo.add(allRelevantTrees.get(currentTreeIndex));
            }
            currentTreeIndex--;
        }
        return treesToAddTo;
    }

    private TreeNode handleChildCase(TreeNode currentNode, Match match, Submission firstSubmission, Submission secondSubmission, ChildCase childCase,
            CountReference duplicateCount) {
        int startDifference = currentNode.getStartInSub(firstSubmission) - match.startOfFirst();
        currentNode.addAppearanceInWithCheck(secondSubmission, match.startOfSecond() + startDifference, duplicateCount);
        ensureAddingProcessIsStillValid(currentNode, firstSubmission);
        Optional<TreeNode> nextNode = currentNode.getChildForSubOfCase(firstSubmission, childCase);
        if (nextNode.isPresent()) {
            return nextNode.get();
        } else {
            TreeNode newNode = TreeNode.createNewNodeOf(match, firstSubmission, secondSubmission, duplicateCount);
            currentNode.addChildWithCheck(newNode, childCase);
            return null;
        }
    }

    private static void ensureAddingProcessIsStillValid(TreeNode currentlyRelevantNode, Submission neededSubmission) {
        if (!currentlyRelevantNode.startInSubmission.containsKey(neededSubmission)) {
            // This is a somewhat rare case that can happen, after calling addAppearanceInWithCheck or addChildWithCheck.
            // What happens is that while calling them, we realize that we now have two children describing the same case, which
            // leads to them getting merged. However while merging, we might realize that the needed submission was involved in an
            // inconsistency, which would lead to it not being added to the merged node. Since the places that call this check
            // method need that submission to be present though, we can not continue, and must therefor cancel the inclusion of this
            // match into this tree.
            throw new InconsistentMatchException();
        }
    }

    private static class InconsistentMatchException extends RuntimeException {
    }

    /**
     * This is a wrapper of an int, which enables storing a reference during the adding process, and updating the count of
     * how many trees a match was added to, whenever it might change.
     */
    public static class CountReference {
        private int count;

        /* package-private */ CountReference(int count) {
            this.count = count;
        }

        /**
         * Gets the current count.
         * @return the count
         */
        public int getCount() {
            return count;
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
     * code section starts in each of the submissions it appears in, and potentially what child nodes it has. Additionally,
     * it also stores for each submission, to how many trees the match, that added the submission to this node, got added
     * to. This will later allow us to make sure that we do not count matches multiple times that got added to multiple
     * trees.
     */
    public static class TreeNode {
        private final Map<Submission, Integer> startInSubmission = new HashMap<>();
        private final int length;

        private final Map<Submission, CountReference> duplicateCounts = new HashMap<>();

        private final List<TreeNode> childUp = new ArrayList<>();
        private final List<TreeNode> childDown = new ArrayList<>();
        private final List<TreeNode> childBoth = new ArrayList<>();

        private TreeNode parent;
        private ChildCase childCaseInParent;

        /* package-private */ TreeNode(int length) {
            this.length = length;
        }

        private static TreeNode createNewNodeOf(Match match, Submission firstSubmission, Submission secondSubmission, CountReference duplicateCount) {
            TreeNode node = new TreeNode(match.minimumLength());
            node.addAppearanceIn(firstSubmission, match.startOfFirst(), duplicateCount);
            node.addAppearanceIn(secondSubmission, match.startOfSecond(), duplicateCount);
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
            TreeNode wrapperNode = new TreeNode(match.minimumLength());
            wrapperNode.addAppearanceIn(firstSubmission, match.startOfFirst());
            return createCopyIntersectedWithNode(wrapperNode, firstSubmission);
        }

        private TreeNode createCopyIntersectedWithNode(TreeNode otherNode, Submission submissionInBoth) {
            int intersectionStart = Math.max(getStartInSub(submissionInBoth), otherNode.getStartInSub(submissionInBoth));
            int intersectionEnd = Math.min(getEndInSub(submissionInBoth), otherNode.getEndInSub(submissionInBoth));
            int intersectionLength = intersectionEnd - intersectionStart + 1;
            int startDifference = intersectionStart - getStartInSub(submissionInBoth);
            TreeNode newCopy = new TreeNode(intersectionLength);
            for (Map.Entry<Submission, Integer> e : startInSubmission.entrySet()) {
                newCopy.addAppearanceIn(e.getKey(), e.getValue() + startDifference, duplicateCounts.get(e.getKey()));
            }
            return newCopy;
        }

        /**
         * Checks if the invariants of the tree are fulfilled. These include, that a child always contains at least the tokens
         * described by the parent, that a child also only extends into the direction according to its child type, and that for
         * each type, there can be at most one child containing the same submission.
         * <p>
         * Checks recursively for this entire subtree.
         * @return true, if the tree is valid, false otherwise
         */
        /* package-private */ boolean checkIfConsistent() {
            for (Submission submission : startInSubmission.keySet()) {
                for (ChildCase childCase : ChildCase.values()) {
                    if (getChildrenOfCase(childCase).stream().filter(n -> n.startInSubmission.containsKey(submission)).count() > 1) {
                        return false;
                    }
                    if (getChildForSubOfCase(submission, childCase).isPresent()) {
                        TreeNode child = getChildForSubOfCase(submission, childCase).get();
                        if (!boundsAreConsistent(child, childCase, submission)) {
                            return false;
                        }
                    }
                }
            }
            for (ChildCase childCase : ChildCase.values()) {
                for (TreeNode child : getChildrenOfCase(childCase)) {
                    if (length != -1) {
                        for (Submission submission : child.getSubmissions()) {
                            if (!startInSubmission.containsKey(submission)) {
                                return false;
                            }
                        }
                    }
                    if (!child.checkIfConsistent()) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Note: This method can result in a reorganization of the tree. This process can result in additional inconsistencies
         * being found, meaning some submissions that were in this node before calling this method might not be in it anymore
         * after it returns.
         */
        private void addAppearanceInWithCheck(Submission appearingInSubmission, int startInThatSubmission, CountReference duplicateCount) {
            if (startInSubmission.containsKey(appearingInSubmission)) {
                if (startInThatSubmission != startInSubmission.get(appearingInSubmission)) {
                    // If we are currently adding a match where both submissions are already part of this tree, we have to careful, since
                    // sometimes matches can describe contradicting situations. Such contradictions result in the tree becoming invalid by a
                    // parent having a child that does not at least cover all the tokens the parent covers.
                    // To prevent this, we must cancel the inclusion of this match into this tree.
                    throw new InconsistentMatchException();
                } else {
                    // Nothing to do, submission is already added.
                    return;
                }
            }
            if (parent.length != -1 && (!parent.startInSubmission.containsKey(appearingInSubmission) || !parent
                    .boundsAreConsistent(startInThatSubmission, startInThatSubmission + length - 1, childCaseInParent, appearingInSubmission))) {
                // The current match also describes a contradiction, if the range does not align with the one defined by the parent, in
                // which case we again, must cancel the inclusion of this match into this tree.
                throw new InconsistentMatchException();
            }

            if (parent.getChildForSubOfCase(appearingInSubmission, childCaseInParent).isPresent()) {
                if (NODE_MERGING_IS_ENABLED) {
                    // In this case, the parent already has a child of the same type as this node that contains the submission to add.
                    // Therefor this node and the other child describe the same code and have to be merged.
                    TreeNode nodeToMergeWith = parent.getChildForSubOfCase(appearingInSubmission, childCaseInParent).get();
                    addAppearanceIn(appearingInSubmission, startInThatSubmission, duplicateCount);
                    mergeWith(nodeToMergeWith, appearingInSubmission);
                } else {
                    // If we are not merging, then this match must be interpreted as a contradiction (as the fact that there are two
                    // separate nodes implies that the two nodes describe different code, while this match, which would result in both nodes
                    // containing the same submissions, implies that the two nodes describe the same section of code), and we therefor have
                    // to cancel adding it.
                    throw new InconsistentMatchException();
                }
            } else {
                addAppearanceIn(appearingInSubmission, startInThatSubmission, duplicateCount);
            }
        }

        /* package-private */ void addAppearanceIn(Submission appearingInSubmission, int startInThatSubmission, CountReference duplicateCount) {
            duplicateCounts.put(appearingInSubmission, duplicateCount);
            addAppearanceIn(appearingInSubmission, startInThatSubmission);
        }

        /* package-private */ void addAppearanceIn(Submission appearingInSubmission, int startInThatSubmission) {
            startInSubmission.put(appearingInSubmission, startInThatSubmission);
        }

        /**
         * For some explanations, see {@link #NODE_MERGING_IS_ENABLED}.
         */
        private void mergeWith(TreeNode nodeToMergeWith, Submission submissionInBoth) {
            TreeNode shorterNode;
            TreeNode longerNode;
            if (getStartInSub(submissionInBoth) == nodeToMergeWith.getStartInSub(submissionInBoth)
                    && getEndInSub(submissionInBoth) == nodeToMergeWith.getEndInSub(submissionInBoth)) {
                parent.removeChild(nodeToMergeWith);
                mergeWithDirectly(nodeToMergeWith);
                checkForAdditionalMerges();
                return;
            } else if (childCaseInParent == ChildCase.UP || childCaseInParent == ChildCase.DOWN) {
                shorterNode = length < nodeToMergeWith.length ? this : nodeToMergeWith;
                longerNode = length < nodeToMergeWith.length ? nodeToMergeWith : this;
            } else {
                TreeNode smallerStart = getStartInSub(submissionInBoth) <= nodeToMergeWith.getStartInSub(submissionInBoth) ? this : nodeToMergeWith;
                TreeNode largerEnd = getEndInSub(submissionInBoth) >= nodeToMergeWith.getEndInSub(submissionInBoth) ? this : nodeToMergeWith;
                if (smallerStart == largerEnd) {
                    shorterNode = smallerStart == this ? nodeToMergeWith : this;
                    longerNode = smallerStart == this ? this : nodeToMergeWith;
                } else {
                    // None is a superset of the other; we need an additional node in between.
                    TreeNode intersection = createCopyIntersectedWithNode(nodeToMergeWith, submissionInBoth);
                    TreeNode parent = this.parent;
                    parent.removeChild(this);
                    parent.removeChild(nodeToMergeWith);
                    parent.addChildAsCorrectCase(intersection, submissionInBoth);
                    ensureAddingProcessIsStillValid(intersection, submissionInBoth);
                    intersection.addChildAsCorrectCase(this, submissionInBoth);
                    intersection.addAllSubmissionsFromNodeAndRemoveInconsistencies(nodeToMergeWith);
                    ensureAddingProcessIsStillValid(nodeToMergeWith, submissionInBoth);
                    intersection.addChildAsCorrectCase(nodeToMergeWith, submissionInBoth);
                    intersection.checkForAdditionalMerges();
                    return;
                }
            }
            parent.removeChild(longerNode);
            shorterNode.addAllSubmissionsFromNodeAndRemoveInconsistencies(longerNode);
            shorterNode.addChildWithCheck(longerNode, childCaseInParent);
            shorterNode.checkForAdditionalMerges();
        }

        /**
         * Directly adds all the information from the nodeToMerge to this node.
         * <p>
         * Note: The nodeToMerge becomes invalid after calling this method, make sure you do not keep using its reference!
         */
        private void mergeWithDirectly(TreeNode nodeToMerge) {
            addAllSubmissionsFromNodeAndRemoveInconsistencies(nodeToMerge);
            for (ChildCase childCase : ChildCase.values()) {
                for (TreeNode child : nodeToMerge.getChildrenOfCase(childCase)) {
                    try {
                        addChildWithCheck(child, childCase);
                    } catch (InconsistentMatchException _) {
                        // It can happen, that adding the child fails, however we catch this exception here, since every child successfully
                        // being added is not actually crucial for merging, meaning we can still continue normally.
                    }
                }
            }
        }

        private void addAllSubmissionsFromNodeAndRemoveInconsistencies(TreeNode nodeToAddAllSubmissionsFrom) {
            for (Submission submission : new HashSet<>(nodeToAddAllSubmissionsFrom.getSubmissions())) {
                try {
                    addAppearanceInWithCheck(submission, nodeToAddAllSubmissionsFrom.getStartInSub(submission),
                            nodeToAddAllSubmissionsFrom.duplicateCounts.get(submission));
                } catch (InconsistentMatchException _) {
                    // If a submission introduces an inconsistency we just do not add it, but we still want to continue with the merge
                    // process, therefor we catch this exception already here.
                    // Additionally, we also remove the invalid submission, so the node can be merged without any issues.
                    nodeToAddAllSubmissionsFrom.removeInvalidSubmissionFromSubtree(submission);
                }
            }
        }

        private void removeInvalidSubmissionFromSubtree(Submission invalidSubmission) {
            startInSubmission.remove(invalidSubmission);
            for (ChildCase childCase : ChildCase.values()) {
                for (TreeNode child : getChildrenOfCase(childCase)) {
                    child.removeInvalidSubmissionFromSubtree(invalidSubmission);
                }
            }
        }

        private void checkForAdditionalMerges() {
            // Very rarely, a merge can result in an illegal state where the parent of this node now has two children in this child
            // case that share a submission. In this case, we need to merge again.
            for (Submission submission : getSubmissions()) {
                Optional<TreeNode> potentialOtherChildToMergeWith = parent.getChildrenOfCase(childCaseInParent).stream()
                        .filter(c -> c != this && c.startInSubmission.containsKey(submission)).findFirst();
                if (potentialOtherChildToMergeWith.isPresent()) {
                    mergeWith(potentialOtherChildToMergeWith.get(), submission);
                    return;
                }
            }
        }

        private boolean boundsAreConsistent(TreeNode proposedChild, ChildCase proposedChildCase, Submission submission) {
            return boundsAreConsistent(proposedChild.getStartInSub(submission), proposedChild.getEndInSub(submission), proposedChildCase, submission);
        }

        private boolean boundsAreConsistent(int startInChild, int endInChild, ChildCase childCase, Submission submission) {
            return !(startInChild > getStartInSub(submission) || endInChild < getEndInSub(submission)
                    || (childCase == ChildCase.UP && endInChild != getEndInSub(submission))
                    || (childCase == ChildCase.DOWN && startInChild != getStartInSub(submission)));
        }

        private void addChildAsCorrectCase(TreeNode childNode, Submission aSubmissionInBoth) {
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
            addChildWithCheck(childNode, childCase);
        }

        private void addChildWithCheck(TreeNode childNode, ChildCase childCase) {
            if (length != -1) {
                for (Submission submission : childNode.startInSubmission.keySet()) {
                    if (!startInSubmission.containsKey(submission)) {
                        // This is a very rare case, where due to a merge, and the submission being inconsistent with the parent, it was not
                        // added to the parent. Therefor we can also not add this child.
                        return;
                    }
                    if (!boundsAreConsistent(childNode, childCase, submission)) {
                        // This is a somewhat rare case, where the child directly contains a contradiction, and we therefor have to cancel the
                        // addition of this match into this tree.
                        throw new InconsistentMatchException();
                    }
                }
                for (Submission submission : childNode.startInSubmission.keySet()) {
                    if (getChildForSubOfCase(submission, childCase).isPresent()) {
                        if (NODE_MERGING_IS_ENABLED) {
                            TreeNode nodeToMerge = getChildForSubOfCase(submission, childCase).get();
                            addChild(childNode, childCase);
                            childNode.mergeWith(nodeToMerge, submission);
                            return;
                        } else {
                            // If we are not merging, we can not add this match without breaking the invariants of this tree, and must therefor
                            // cancel the inclusion of this match.
                            throw new InconsistentMatchException();
                        }
                    }
                }
            }
            addChild(childNode, childCase);
        }

        /* package-private */ void addChild(TreeNode childNode, ChildCase childCase) {
            getChildrenOfCase(childCase).add(childNode);
            childNode.parent = this;
            childNode.childCaseInParent = childCase;
        }

        private void removeChild(TreeNode childToRemove) {
            getChildrenOfCase(childToRemove.childCaseInParent).remove(childToRemove);
            childToRemove.parent = null;
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

        /**
         * Returns the duplicate counts of this node, meaning, for each submission that contains the section of code described
         * by this node, in how many other nodes the match, that added it to this node, got added to.
         * @return the duplicate counts
         */
        public Map<Submission, CountReference> getDuplicateCounts() {
            return duplicateCounts;
        }

        private int getStartInSub(Submission submission) {
            return startInSubmission.get(submission);
        }

        private int getEndInSub(Submission submission) {
            return startInSubmission.get(submission) + length - 1;
        }

        private boolean overlaps(Match match, Submission firstSubmission) {
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
