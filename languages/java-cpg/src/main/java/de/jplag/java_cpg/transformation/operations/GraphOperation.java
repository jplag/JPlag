package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.Match.WildcardMatch;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern.ParentNodePattern;

/**
 * A {@link GraphOperation} is an arbitrary modification on a Graph and the basic unit of a {@link GraphTransformation}.
 */
public interface GraphOperation {
    /**
     * Applies the {@link GraphOperation} on the graph represented by a {@link Match} indicating which nodes are involved in
     * the operation.
     * @param match the pattern match
     * @param ctx
     */
    void resolve(Match match, TranslationContext ctx) throws TransformationException;

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

    GraphOperation instantiateAny1ofNEdge(Match match);

    boolean isWildcarded();

    boolean isMultiEdged();
}
