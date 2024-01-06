package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class SetOperation<S extends Node, T extends Node> extends GraphOperationImpl<S, T> {
    private static final Logger LOGGER;
    private final NodePattern<? extends T> newChildPattern;

    public SetOperation(NodePattern<? extends S> parentPattern,
                        CpgEdge<S, T> edge,
                        NodePattern<? extends T> newChildPattern) {
        super(parentPattern, edge);
        this.newChildPattern = newChildPattern;
    }

    static {
        LOGGER = LoggerFactory.getLogger(RemoveOperation.class);
    }

    @Override
    public void apply(GraphPattern.Match<?> match) {
        S parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        T newChild = match.get(newChildPattern);
        LOGGER.info("Set %s as AST child of %s".formatted(desc(newChild), desc(parent)));

        assert Objects.isNull(edge.getter().apply(parent));
        edge.setter().accept(parent, newChild);
    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public <S extends Node, T extends Node> GraphOperation instantiateWildcard(GraphPattern.Match.WildcardMatch<S, T> match) {
        if (!(this.parentPattern instanceof WildcardGraphPattern<?>.ParentNodePattern)) {
            return this;
        }

        throw new RuntimeException("Cannot apply SetOperation with WildcardGraphPattern.ParentPattern as parentPattern.");
    }

    @Override
    public GraphOperation instantiateAny1ofNEdge(GraphPattern.Match<?> match) {
        throw new RuntimeException("Cannot apply SetOperation with Any1ofNEdge.");
    }

}
