package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.PatternUtil;

/**
 * Creates a new {@link Node} in the graph.
 * Note: The new {@link Node} needs to be inserted into the graph via other {@link GraphOperation}s.
 *
 * @param sourceGraph the graph
 * @param roleName    the role name of the new {@link Node}
 * @param pattern     the {@link NodePattern} representing the new {@link Node}
 * @param <N>         the new {@link Node}'s type
 */
public record CreateNodeOperation<N extends Node>(GraphPattern<?> sourceGraph, String roleName,
                                                  NodePattern<N> pattern) implements GraphOperation {

    @Override
    public void apply(GraphPattern.Match<?> match) {
        match.register(pattern, PatternUtil.instantiate(pattern));
    }

    @Override
    public NodePattern<?> getTarget() {
        return null;
    }

    @Override
    public <S extends Node, T extends Node> GraphOperation instantiate(GraphPattern.Match.WildcardMatch<S, T> match) {
        return this;
    }
}
