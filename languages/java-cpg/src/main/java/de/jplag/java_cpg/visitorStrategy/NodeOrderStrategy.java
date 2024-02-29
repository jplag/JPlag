package de.jplag.java_cpg.visitorStrategy;

import com.google.common.collect.Iterators;
import de.fraunhofer.aisec.cpg.graph.Component;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.NamespaceDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.scopes.BlockScope;
import de.fraunhofer.aisec.cpg.graph.scopes.LoopScope;
import de.fraunhofer.aisec.cpg.graph.scopes.TryScope;
import de.fraunhofer.aisec.cpg.graph.scopes.ValueDeclarationScope;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.WhileStatement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.processing.IStrategy;
import de.fraunhofer.aisec.cpg.processing.strategy.Strategy;
import de.jplag.java_cpg.transformation.operations.TransformationHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class defines the order of visitation of the CPG {@link Node}s.
 */
public class NodeOrderStrategy implements IStrategy<Node> {

    public NodeOrderStrategy() {

    }

    private static Iterator<Node> walkMethod(MethodDeclaration methodDecl) {
        if (!methodDecl.hasBody()) {
            return Iterators.concat(methodDecl.getParameters().iterator());
        }
        return Iterators.concat(
            methodDecl.getParameters().iterator(),
            List.of(methodDecl.getBody()).iterator()
        );
    }

    private static Iterator<Node> walkRecord(RecordDeclaration recordDecl) {
        return Iterators.concat(
            recordDecl.getFields().iterator(),
            recordDecl.getConstructors().iterator(),
            // TODO: iterate over methods in CallGraph top-down DFS order
            recordDecl.getMethods().iterator(),
            recordDecl.getTemplates().iterator(),
            recordDecl.getRecords().iterator()
        );
    }

    @Override
    public @NotNull Iterator<Node> getIterator(Node node) {
        if (node instanceof Component c) {
            return walkComponent(c);
        } else if (node instanceof TranslationUnitDeclaration tu) {
            return Iterators.concat(tu.getDeclarations().iterator());
        } else if (node instanceof NamespaceDeclaration nsd) {
            return Iterators.concat(nsd.getDeclarations().iterator());
        } else if (node instanceof RecordDeclaration recordDecl) {
            return walkRecord(recordDecl);
        } else if (node instanceof MethodDeclaration methodDecl) {
            return walkMethod(methodDecl);
        } else if (node instanceof WhileStatement whileStatement) {
            return walkWhileStatement(whileStatement);
        } else if (node instanceof IfStatement ifStatement) {
            return walkIfStatement(ifStatement);
        } else if (node instanceof Block block) {
            if (block.getStatements().isEmpty()) return Collections.emptyIterator();
            Node entry = TransformationHelper.getEogBorders(block.getStatements().get(0)).getEntries().get(0);
            return List.of(entry).iterator();
        } else if (node.getScope() instanceof BlockScope || node.getScope() instanceof LoopScope || node.getScope() instanceof TryScope || node.getScope() instanceof ValueDeclarationScope) {
            return Strategy.INSTANCE.EOG_FORWARD(node);
        }
        else {
            return Strategy.INSTANCE.AST_FORWARD(node);
        }
    }

    private Iterator<Node> walkWhileStatement(WhileStatement whileStatement) {
        List<PropertyEdge<Node>> nextEOGEdges = whileStatement.getNextEOGEdges();
        Map<Boolean, Node> collect = nextEOGEdges.stream().collect(Collectors.toMap(e -> (boolean) e.getProperty(Properties.BRANCH), PropertyEdge::getEnd));
        // first walk into while block, then to the next statement
        return
            Stream.of(whileStatement.getCondition(), collect.get(true), collect.get(false))
                .filter(Objects::nonNull).iterator();

    }

    private Iterator<Node> walkIfStatement(IfStatement ifStatement) {
        return Stream.<Node>of(ifStatement.getCondition(), ifStatement.getThenStatement(), ifStatement.getElseStatement())
            .filter(Objects::nonNull).iterator();
    }

    private Iterator<Node> walkComponent(Component c) {
        ArrayList<TranslationUnitDeclaration> filesContainingMainClasses = c.getTranslationUnits().stream()
            .filter(tu -> tu.getNamespaces().stream().anyMatch(
                nsp -> nsp.getDeclarations().stream().anyMatch(
                    decl -> decl instanceof RecordDeclaration record
                        && record.getMethods().stream().anyMatch(m -> m.isStatic() && m.getName().getLocalName().equals("main"))

                )
            ))
            .collect(Collectors.toCollection(ArrayList::new));

        List<TranslationUnitDeclaration> otherFiles = new ArrayList<>(c.getTranslationUnits());
        otherFiles.removeAll(filesContainingMainClasses);
        return Iterators.concat(filesContainingMainClasses.iterator(), otherFiles.iterator());

    }
}