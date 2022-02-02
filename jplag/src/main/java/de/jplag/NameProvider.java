package de.jplag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class constructing unique short names for provided named elements.
 * <p>
 * It builds a suffix tree of each name and finds entries used by one element in it.
 * </p>
 * @param <T> Type of the elements to give a name.
 */
public class NameProvider<T> {
    /**
     * @param <E> Type of the element to give a name.
     */
    private static class NameNode<E> {
        /**
         * Tree of tails of name parts.
         */
        private Map<String, NameNode<E>> childNodes = new LinkedHashMap<>();

        /**
         * Unique element at this node. {@code null} if more than one element reaches this node.
         */
        private E element;

        /**
         * @param element Element that reached this node for the first time (and thus is unique by definition at creation time).
         */
        public NameNode(E element) {
            this.element = element;
        }

        /**
         * Remove the sub-trees branching from this node.
         */
        public void clear() {
            childNodes.clear();
        }

        /**
         * Add a child name to a node, possibly making a new branch.
         * @param childName Name for moving to the next node.
         * @param element Element to give a name.
         * @return The next node.
         */
        public NameNode<E> appendName(String childName, E element) {
            NameNode<E> nextNode = childNodes.get(childName);
            if (nextNode == null) { // First element reaching the child node.
                nextNode = new NameNode<>(element);
                childNodes.put(childName, nextNode);
                return nextNode;
            }
            // At least the second element reaching the child node, the path is not unique any more.
            nextNode.element = null;
            return nextNode;
        }

        /**
         * Collect unique shortest possible elements from the node and its sub-trees.
         * @param pathName Names of the path from the root to this node.
         * @param foundElements Collected elements so far, together with their best names.
         */
        public void collectElements(List<String> pathName, Map<E, List<String>> foundElements) {
            // Add the unique element of this node if available.
            if (element != null) {
                List<String> otherName = foundElements.get(element);
                if (otherName == null || otherName.size() > pathName.size()) {
                    foundElements.put(element, new ArrayList<>(pathName));
                }
                // A non-null element at the node means no other element ever reached this node.
                // Traversing sub-trees can at best find more candidate names for the same element, but all
                // those names are longer than at this node and we are looking for the shortest possible name.
                return;
            }

            // Explore sub-trees.
            int freeIndex = pathName.size();
            pathName.add(null);
            for (Entry<String, NameNode<E>> entry : childNodes.entrySet()) {
                pathName.set(freeIndex, entry.getKey());
                entry.getValue().collectElements(pathName, foundElements);
            }
            pathName.remove(freeIndex);
        }
    }

    /**
     * Root of the tree.
     */
    private final NameNode<T> rootNode = new NameNode<>(null);

    /**
     * Provided elements.
     */
    private List<T> elements = new ArrayList<>();

    /**
     * Elements that could be given a name.
     * <p>
     * Field is initialized in {@link #collectElementNames}.
     * </p>
     */
    private Map<T, List<String>> namedElements = null;

    /**
     * Reset the class to its initial state.
     */
    public void reset() {
        rootNode.clear();
        elements.clear();
        namedElements = null;
    }

    /**
     * Store an element in the tree.
     * @param element Element to give a name.
     * @param parts Parts of the name to store in the tree.
     */
    public void storeElement(T element, List<String> parts) {
        elements.add(element);

        namedElements = null; // Anything in the namedElements is now invalid.
        List<NameNode<T>> openNodes = new ArrayList<>(parts.size());
        for (String part : parts) {
            openNodes.add(rootNode);
            for (int idx = 0; idx < openNodes.size(); idx++) {
                NameNode<T> node = openNodes.get(idx);
                openNodes.set(idx, node.appendName(part, element));
            }
        }
    }

    /**
     * Convenience method to store an element by giving its name as a single string.
     * @param element Element to store.
     * @param name Name of the element as a single string.
     * @param sep Separator text for splitting the name into parts.
     */
    public void storeElement(T element, String name, String sep) {
        if (sep.isEmpty()) {
            throw new AssertionError("String splitting needs a non-empty separator.");
        }

        List<String> names = new ArrayList<>();
        int idx = 0;
        for (;;) {
            int sepIdx = name.indexOf(sep, idx);
            if (sepIdx < 0) {
                names.add(name.substring(idx));
                break;
            }
            names.add(name.substring(idx, sepIdx));
            idx = sepIdx + sep.length();
        }
        storeElement(element, names);
    }

    /**
     * Traverse the stored name tails tree, and collect elements with their best name.
     * <p>
     * If all name sequences of the stored elements are unique, this will return all elements. Missing elements implies a
     * name sequence collision between them.
     * </p>
     * @return Found elements with their names.
     * @see #collectUnnamedElements
     */
    public Map<T, List<String>> collectNamedElements() {
        namedElements = new LinkedHashMap<>();
        List<String> pathNames = new ArrayList<>();
        rootNode.collectElements(pathNames, namedElements);
        return namedElements;
    }

    /**
     * Retrieve elements that do not have a unique name.
     */
    public List<T> collectUnnamedElements() {
        if (namedElements == null) {
            throw new AssertionError("Use 'collectUnnamedElements' after 'collectNamedElements'.");
        }

        List<T> unnamedElements = new ArrayList<>();

        if (elements.size() == namedElements.size()) { // Everything has a name, nothing left to check.
            return unnamedElements;
        }

        for (T element : elements) {
            if (!namedElements.containsKey(element)) {
                unnamedElements.add(element);
            }
        }
        return unnamedElements;
    }
}
