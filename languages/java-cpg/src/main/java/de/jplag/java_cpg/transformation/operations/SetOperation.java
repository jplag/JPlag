package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class SetOperation<S extends Node, T extends Node> extends GraphOperationImpl<S, T> {
    private static final Logger logger;
    private final NodePattern<? extends T> newChildPattern;
    private final boolean disconnectEog;

    public SetOperation(NodePattern<? extends S> parentPattern,
                        CpgEdge<S, T> edge,
                        NodePattern<? extends T> newChildPattern, boolean disconnectEog) {
        super(parentPattern, edge);
        this.newChildPattern = newChildPattern;
        this.disconnectEog = disconnectEog;
    }

    static {
        logger = LoggerFactory.getLogger(SetOperation.class);
    }

    @Override
    public void resolve(Match match, TranslationContext ctx) {
        S parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        T newChild = match.get(newChildPattern);
        logger.debug("Set %s as AST child of %s".formatted(desc(newChild), desc(parent)));

        assert Objects.isNull(edge.getter().apply(parent));
        edge.setter().accept(parent, newChild);

        Scope parentScope = Objects.requireNonNullElse(ctx.getScopeManager().lookupScope(parent), parent.getScope());
        newChild.setScope(parentScope);

        if (disconnectEog) {
            logger.warn("disconnectEog in SetOperation – not yet implemented");
        }
    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public GraphOperation instantiateWildcard(Match match) {
        if (!(this.parentPattern instanceof WildcardGraphPattern.ParentNodePattern<?>)) {
            return this;
        }

        throw new RuntimeException("Cannot apply SetOperation with WildcardGraphPattern.ParentPattern as parentPattern.");
    }

    @Override
    public GraphOperation instantiateAny1ofNEdge(Match match) {
        throw new RuntimeException("Cannot apply SetOperation with Any1ofNEdge.");
    }

}
