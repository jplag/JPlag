package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;

public final class EdgeUtil {

    private EdgeUtil() {
    }

    public static <N extends Node> RecordDeclaration getRecord(N node) {

        Scope scope = node.getScope();
        while (!(scope.getAstNode() instanceof RecordDeclaration record) ) {
            scope = scope.getParent();
        }
        return record;
    }

}
