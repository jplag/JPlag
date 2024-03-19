package de.jplag.java_cpg.transformation.matching;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * This class finds {@link Match}es of {@link GraphPattern}s in concrete graphs.
 */
public class CpgIsomorphismDetector {

    private static final Logger logger = LoggerFactory.getLogger(CpgIsomorphismDetector.class);
    private TreeMap<Class<? extends Node>, List<Node>> nodeMap;
    private ClassComparator classComparator;

    /**
     * Creates a new {@link CpgIsomorphismDetector}.
     */
    public CpgIsomorphismDetector() {
    }

    /**
     * Sets the current graph to find pattern matches in.
     * @param root The root {@link Node} of the graph.
     */
    public void loadGraph(Node root) {
        classComparator = new ClassComparator();
        nodeMap = new TreeMap<>(classComparator);

        List<Node> allNodes = SubgraphWalker.INSTANCE.flattenAST(root);
        allNodes.forEach(this::registerNode);
    }

    /**
     * Gets an {@link Iterator} over all {@link Match}es of the given {@link GraphPattern} in the currently loaded Graph.
     * @param sourcePattern the {@link GraphPattern} to search matches for
     * @return an {@link Iterator} over all found {@link Match}es
     */
    public List<Match> getMatches(GraphPattern sourcePattern) {
        Map<NodePattern<?>, List<? extends Node>> rootCandidateMap = sourcePattern.getRoots().stream().collect(Collectors.toMap(root -> root,
                root -> root.getCandidateClasses().stream().map(this::getNodesOfType).flatMap(List::stream).distinct().toList()));
        List<Match> matches = sourcePattern.match(rootCandidateMap);
        logger.info("Got %d matches looking at %d nodes".formatted(matches.size(), NodePattern.NodePatternImpl.getCounter()));
        NodePattern.NodePatternImpl.resetCounter();
        return matches;
    }

    /**
     * Registers a concrete {@link Node} of a CPG so that it can be considered as a root candidate of a
     * {@link GraphPattern}.
     * @param node the node
     */
    private void registerNode(Node node) {
        nodeMap.computeIfAbsent(node.getClass(), c -> new ArrayList<>()).add(node);
    }

    /**
     * Gets a {@link List} of all registered {@link Node}s that are assignable to the given type.
     * @param superClass the type class to search
     * @param <T> the type
     * @return a list of assignable nodes
     */
    public <T extends Node> List<T> getNodesOfType(Class<T> superClass) {
        classComparator.setMode(ClassComparator.Mode.FIRST_COMPATIBLE);
        Class<? extends Node> firstCompatible = nodeMap.ceilingKey(superClass);
        classComparator.setMode(ClassComparator.Mode.FIRST_INCOMPATIBLE);
        Class<? extends Node> firstIncompatible = nodeMap.ceilingKey(superClass);
        classComparator.setMode(ClassComparator.Mode.FIRST_COMPATIBLE);

        if (Objects.isNull(firstCompatible)) {
            // no nodes of this type
            return List.of();
        }

        SortedMap<Class<? extends Node>, List<Node>> subMap = nodeMap.tailMap(firstCompatible);
        if (firstIncompatible != null) {
            subMap = subMap.headMap(firstIncompatible);
        }
        List<T> nodes = subMap.values().stream().flatMap(List::stream).map(superClass::cast).toList();

        return nodes;
    }

    /**
     * Verifies that the given match of the source {@link GraphPattern} is still valid. After a transformation involving the
     * match's {@link Node}s, a match may be invalidated.
     * @param match the match
     * @param sourcePattern the source pattern
     * @return true iff the match is still valid
     */
    public boolean validateMatch(Match match, GraphPattern sourcePattern) {
        return sourcePattern.validate(match);
    }

    /**
     * This comparator imposes a total order on classes by using their class hierarchy and name, where
     * <ul>
     * <li>different subclasses of a common superclass are ordered alphanumerically</li>
     * <li>the sublist of subclasses of a superclass comes directly after the superclass</li>
     * </ul>
     */
    private static final class ClassComparator implements Comparator<Class<?>> {

        private Mode mode = Mode.FIRST_COMPATIBLE;

        public void setMode(Mode mode) {
            this.mode = mode;
        }

        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            // classes are compatible with each other? -> Order of hierarchy
            if (Objects.equals(o1, o2))
                return this.mode.equalityValue;
            else if (o1.isAssignableFrom(o2))
                return this.mode.subClassValue;
            else if (o2.isAssignableFrom(o1))
                return this.mode.superClassValue;

            // classes are incompatible; find most special incompatible superclass
            Class<?> super1 = o1.getSuperclass();
            Class<?> super2 = o2.getSuperclass();

            Class<?> comp1, comp2;
            Mode mode = this.mode;
            setMode(Mode.FIRST_COMPATIBLE);
            int res = (int) Math.signum(compare(super1, super2));
            setMode(mode);
            if (res == -1 && super1.isAssignableFrom(super2)) {
                // super1 is common supertype
                comp1 = o1;
                while (!super2.getSuperclass().equals(super1)) {
                    super2 = super2.getSuperclass();
                }
                // o1 and super2 are incompatible subclasses of the same class
                comp2 = super2;
            } else if (res == 0) {
                // o1 and o2 are incompatible subclasses of the same class
                comp1 = o1;
                comp2 = o2;
            } else if (res == 1 && super2.isAssignableFrom(super1)) {
                // super2 is common supertype
                comp2 = o2;
                while (!super1.getSuperclass().equals(super2)) {
                    super1 = super1.getSuperclass();
                }
                // o2 and super1 are incompatible subclasses of the same class
                comp1 = super1;
            } else {
                // super types are incompatible, just use their comparison value
                return res;
            }
            // found most special incompatible supertypes; use alphabetical order on name
            return Comparator.<Class<?>, String>comparing(Class::getName).compare(comp1, comp2);

        }

        enum Mode {
            /**
             * In this mode, a binary search of a class A in a list returns the position of the first class B in the list so that B
             * is a subclass or equal to A.
             */
            FIRST_COMPATIBLE(0, -1, 1),

            /**
             * In this mode, a binary search of a class A in a list returns the position <b>after</b> the last class B in the list
             * so that B is a subclass or equal to A.
             */
            FIRST_INCOMPATIBLE(1, 1, 1);

            /**
             * The value to return for classes (A, B) if B is a true superclass to A.
             */
            public final int superClassValue;
            /**
             * The value to return for classes (A, A).
             */
            private final int equalityValue;
            /**
             * The value to return for classes (A, B) if B is a true subclass to A.
             */
            private final int subClassValue;

            Mode(int equalityValue, int subClassValue, int superClassValue) {
                this.equalityValue = equalityValue;
                this.subClassValue = subClassValue;
                this.superClassValue = superClassValue;
            }
        }
    }

}
