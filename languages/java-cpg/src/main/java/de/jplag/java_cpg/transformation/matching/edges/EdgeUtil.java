package de.jplag.java_cpg.transformation.matching.edges;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;

/**
 * This class provides auxiliary methods to build edges.
 */
public final class EdgeUtil {

    private EdgeUtil() {
    }

    /**
     * Gets the {@link RecordDeclaration} that a {@link Node} is located in.
     * @param node a node
     * @return the record declaration
     */
    public static RecordDeclaration getRecord(Node node) {

        Scope scope = node.getScope();
        while (!Objects.isNull(scope)) {
            if (scope.getAstNode() instanceof RecordDeclaration record) {
                return record;
            }
            scope = scope.getParent();
        }
        return null;
    }

    @NotNull
    static String getLocalName(Node n) {
        return n.getName().getLocalName();
    }

}
