package de.jplag.java_cpg.transformation.operations;

import java.util.List;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.NamespaceDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.TemplateDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.AssertStatement;
import de.fraunhofer.aisec.cpg.graph.statements.CatchClause;
import de.fraunhofer.aisec.cpg.graph.statements.DoStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForEachStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForStatement;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.SwitchStatement;
import de.fraunhofer.aisec.cpg.graph.statements.TryStatement;
import de.fraunhofer.aisec.cpg.graph.statements.WhileStatement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.jplag.java_cpg.transformation.Role;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.PatternUtil;

/**
 * Creates a new {@link Node} in the graph. Note: The new {@link Node} needs to be inserted into the graph via other
 * {@link GraphOperation}s.
 * @param sourceGraph the graph
 * @param role the role of the new {@link Node}
 * @param pattern the {@link NodePattern} representing the new {@link Node}
 * @param <N> the new {@link Node}'s type
 */
public record CreateNodeOperation<N extends Node>(GraphPattern sourceGraph, Role role, NodePattern<N> pattern) implements GraphOperation {

    private static final List<Class<? extends Node>> scopedNodeClasses = List.of(Block.class, WhileStatement.class, DoStatement.class,
            AssertStatement.class, ForStatement.class, ForEachStatement.class, SwitchStatement.class, FunctionDeclaration.class, IfStatement.class,
            CatchClause.class, RecordDeclaration.class, TemplateDeclaration.class, TryStatement.class, NamespaceDeclaration.class);

    @Override
    public void resolveAndApply(Match match, TranslationContext ctx) {
        N newNode = PatternUtil.instantiate(pattern);
        match.register(pattern, newNode);

        if (scopedNodeClasses.contains(newNode.getClass())) {
            ctx.getScopeManager().enterScope(newNode);
            ctx.getScopeManager().leaveScope(newNode);
        }
    }

    @Override
    public GraphOperation instantiateWildcard(Match match) {
        throw new TransformationException("Cannot instantiate CreateNodeOperation");
    }

    @Override
    public GraphOperation instantiateAnyOfNEdge(Match match) {
        throw new TransformationException("Cannot instantiate CreateNodeOperation");
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
