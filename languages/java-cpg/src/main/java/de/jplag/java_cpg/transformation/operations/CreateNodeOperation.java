package de.jplag.java_cpg.transformation.operations;

import java.util.List;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.NamespaceDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.TemplateDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.jplag.java_cpg.transformation.matching.pattern.*;

/**
 * Creates a new {@link Node} in the graph. Note: The new {@link Node} needs to be inserted into the graph via other
 * {@link GraphOperation}s.
 * @param sourceGraph the graph
 * @param roleName the role name of the new {@link Node}
 * @param pattern the {@link NodePattern} representing the new {@link Node}
 * @param <N> the new {@link Node}'s type
 */
public record CreateNodeOperation<N extends Node>(GraphPattern sourceGraph, String roleName, NodePattern<N> pattern) implements GraphOperation {

    private static final List<Class<? extends Node>> scopedNodeClasses = List.of(Block.class, WhileStatement.class, DoStatement.class,
            AssertStatement.class, ForStatement.class, ForEachStatement.class, SwitchStatement.class, FunctionDeclaration.class, IfStatement.class,
            CatchClause.class, RecordDeclaration.class, TemplateDeclaration.class, TryStatement.class, NamespaceDeclaration.class);

    @Override
    public void resolve(Match match, TranslationContext ctx) {
        N newNode = PatternUtil.instantiate(pattern);
        match.register(pattern, newNode);

        if (scopedNodeClasses.contains(newNode.getClass())) {
            ctx.getScopeManager().enterScope(newNode);
            ctx.getScopeManager().leaveScope(newNode);
        }
    }

    @Override
    public NodePattern<?> getTarget() {
        return null;
    }

    @Override
    public GraphOperation instantiateWildcard(Match match) {
        throw new RuntimeException("Cannot instantiate CreateNodeOperation");
    }

    @Override
    public GraphOperation instantiateAny1ofNEdge(Match match) {
        throw new RuntimeException("Cannot instantiate CreateNodeOperation");
    }

    @Override
    public boolean isWildcarded() {
        return false;
    }

    @Override
    public boolean isMultiEdged() {
        return false;
    }
}
