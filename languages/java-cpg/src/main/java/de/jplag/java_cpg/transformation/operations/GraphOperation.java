package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern.Match.WildcardMatch;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern.ParentNodePattern;

/**
 *  A {@link GraphOperation} is an arbitrary modification on a Graph and the basic unit of a {@link GraphTransformation}.
 */
public interface GraphOperation {
    /**
     * Applies the {@link GraphOperation} on the graph represented by a {@link GraphPattern.Match} indicating which nodes are involved in the operation.
     * @param match
     */
    void apply(GraphPattern.Match<?> match) throws TransformationException;

    /**
     * Gets the {@link NodePattern} representing the {@link Node} where the operation is intended to be applied.
     * @return the target
     */
    NodePattern<?> getTarget();

    /**
     * If the target nodes of this {@link GraphOperation} is a {@link ParentNodePattern}, then this method creates a concrete {@link GraphOperation} with the given {@link Node} and {@link IEdge} from the {@link WildcardMatch}.
     * @param match The {@link WildcardMatch} containing the concrete {@link Node} and {@link PropertyEdge} for the wildcard
     * @return The instantiated {@link GraphOperation}
     * @param <S> The parent {@link Node} type
     * @param <T> The child {@link Node} type
     */
    <S extends Node, T extends Node> GraphOperation instantiate(WildcardMatch<S, T> match);
}
