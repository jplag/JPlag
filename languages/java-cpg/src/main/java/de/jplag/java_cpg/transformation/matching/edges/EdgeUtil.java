package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * This class provides auxiliary methods to build edges.
 */
public final class EdgeUtil {

    private EdgeUtil() {
    }

    public static <N extends Node> RecordDeclaration getRecord(N node) {

        Scope scope = node.getScope();
        while (!(scope.getAstNode() instanceof RecordDeclaration record)) {
            scope = scope.getParent();
        }
        return record;
    }

    @NotNull
    static String getLocalName(Node n) {
        return n.getName().getLocalName();
    }

}
