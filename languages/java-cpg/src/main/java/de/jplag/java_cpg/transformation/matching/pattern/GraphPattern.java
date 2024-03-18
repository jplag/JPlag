package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.GraphTransformation;

/**
 * A {@link GraphPattern} represents a collection of CPG nodes related to each other in specific ways.
 */
public interface GraphPattern {

    /**
     * Returns a collection of the identifiers of all {@link NodePattern}s of this {@link GraphPattern}.
     * @return the identifiers
     */
    Collection<String> getAllIds();

    /**
     * Compares this {@link GraphPattern} to another {@link GraphPattern} in order to generate a {@link GraphTransformation}
     * between the two. The type of {@link GraphPattern} determines how the comparison works.
     * @param targetPattern the target pattern
     * @param compareFunction a comparison function for {@link NodePattern}s
     */
    void compareTo(GraphPattern targetPattern, BiConsumer<NodePattern<?>, NodePattern<?>> compareFunction);

    /**
     * Adds a newly created {@link NodePattern} to this pattern. This occurs when a {@link GraphTransformation} includes the
     * generation of new {@link Node}s.
     */
    <T extends Node> NodePattern<T> addNode(String roleName, NodePattern<T> newNode);

    /**
     * Gets the {@link NodePattern} associated to the given identifier.
     * @param id the identifier
     * @return the node pattern
     */
    NodePattern<?> getPattern(String id);

    /**
     * Gets the identifier that the given {@link NodePattern} is associated to in this {@link GraphPattern}.
     * @param pattern the node pattern
     * @return the identifier
     */
    String getId(NodePattern<?> pattern);

    /**
     * Gets the representing node of this {@link GraphPattern}.
     * @return the representing node
     */
    NodePattern<?> getRepresentingNode();

    /**
     * Returns the list of root {@link NodePattern}s.
     * @return the root(s).
     */
    List<NodePattern<?>> getRoots();

    /**
     * Matches the given candidate {@link Node}s against the respective root {@link NodePattern}s of this
     * {@link GraphPattern}.
     * @param rootCandidates the root candidate {@link Node}s for each root {@link NodePattern}
     * @return the matches
     */
    List<Match> match(Map<NodePattern<?>, List<? extends Node>> rootCandidates);

    /**
     * Verifies that the given match of this {@link GraphPattern} is still valid. After a transformation involving the
     * match's {@link Node}s, a match may be invalidated.
     * @param match the match
     * @return true iff the match is still valid
     */
    boolean validate(Match match);
}
