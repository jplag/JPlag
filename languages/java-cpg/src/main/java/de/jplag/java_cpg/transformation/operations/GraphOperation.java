package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge.AnyOfNEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.Match.WildcardMatch;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern.Edge;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern.ParentNodePattern;

/**
 * A {@link GraphOperation} is an arbitrary modification on a Graph and the basic unit of a {@link GraphTransformation}.
 */
public interface GraphOperation {
    /**
     * Applies the {@link GraphOperation} on the graph represented by a {@link Match} indicating which nodes are involved in
     * the operation.
     * @param match the pattern match
     * @param ctx the translation context
     * @throws TransformationException if the graph is malformed.
     */
    void resolveAndApply(Match match, TranslationContext ctx) throws TransformationException;

    /**
     * Gets the {@link NodePattern} representing the {@link Node} where the operation is intended to be applied.
     * @return the target
     */
    NodePattern<?> getTarget();

    /**
     * If the target nodes of this {@link GraphOperation} is a {@link ParentNodePattern}, then this method creates a
     * concrete {@link GraphOperation} with the given {@link Node} and {@link IEdge} from the {@link WildcardMatch}.
     * @param match The {@link WildcardMatch} containing the concrete {@link Node} and {@link PropertyEdge} for the wildcard
     * @return The instantiated {@link GraphOperation}
     */
    GraphOperation instantiateWildcard(Match match);

    /**
     * Returns a copy of this {@link GraphOperation} where the relevant {@link AnyOfNEdge} is replaced with a
     * {@link CpgNthEdge}.
     * @param match the {@link Match} of a {@link GraphTransformation} source pattern
     * @return the instantiated {@link GraphOperation}
     */
    GraphOperation instantiateAnyOfNEdge(Match match);

    /**
     * Determines whether this {@link GraphOperation} is wildcarded.
     * @return true iff this {@link GraphOperation} involves a {@link Edge} that needs to be instantiated.
     */
    boolean isWildcarded();

    /**
     * Determines whether this {@link GraphOperation} is multi-edged.
     * @return true iff this {@link GraphOperation} involves a {@link CpgMultiEdge}.
     */
    boolean isMultiEdged();
}
