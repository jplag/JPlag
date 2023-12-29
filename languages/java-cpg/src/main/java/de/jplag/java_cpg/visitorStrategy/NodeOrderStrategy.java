package de.jplag.java_cpg.visitorStrategy;

import com.google.common.collect.Iterators;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.Statement;
import de.fraunhofer.aisec.cpg.processing.IStrategy;
import de.fraunhofer.aisec.cpg.processing.strategy.Strategy;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;

/**
 * This class defines the order of visitation of the CPG {@link Node}s.
 */
public class NodeOrderStrategy implements IStrategy<Node> {

    public NodeOrderStrategy() {

    }

    @NotNull
    @Override
    public Iterator<Node> getIterator(Node node) {
        if (RecordDeclaration.class.isAssignableFrom(node.getClass())) {
            RecordDeclaration recordDecl = (RecordDeclaration) node;
            return walkRecord(recordDecl);
        } else if (MethodDeclaration.class.isAssignableFrom(node.getClass())) {
            MethodDeclaration methodDecl = (MethodDeclaration) node;
            return walkMethod(methodDecl);
        } else if (Statement.class.isAssignableFrom(node.getClass())) {
            return Strategy.INSTANCE.EOG_FORWARD(node);
        } else {
            return Strategy.INSTANCE.AST_FORWARD(node);
        }
    }

    private static Iterator<Node> walkMethod(MethodDeclaration methodDecl) {
        return Iterators.concat(
            methodDecl.getParameters().iterator(),
            methodDecl.hasBody() ? Strategy.INSTANCE.EOG_FORWARD(methodDecl) : Collections.emptyIterator()
        );
    }

    private static Iterator<Node> walkRecord(RecordDeclaration recordDecl) {
        Iterator<Node> concat = Iterators.concat(
            recordDecl.getFields().iterator(),
            recordDecl.getConstructors().iterator(),
            // TODO: iterate over methods in CallGraph top-down DFS order
            recordDecl.getMethods().iterator(),
            recordDecl.getTemplates().iterator(),
            recordDecl.getRecords().iterator()
        );
        return concat;
    }
}