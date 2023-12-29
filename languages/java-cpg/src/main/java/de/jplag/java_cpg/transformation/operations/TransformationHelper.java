package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;

import java.util.List;

public final class TransformationHelper {

    private TransformationHelper() {
        /* should not be instantiated */
    }

    /**
     * Gets the {@link SubgraphWalker.Border} of the given node's sub-AST that links to outer nodes via EOG edges.
     * @param astRoot the root of the sub-AST
     * @return the EOG {@link SubgraphWalker.Border} of the AST
     */
    static SubgraphWalker.Border getEogBorders(Node astRoot) {
        if (astRoot.getNextEOG().isEmpty() && astRoot.getPrevEOG().isEmpty()) {
            // Isolated node: a case that the API method does not account for
            SubgraphWalker.Border border = new SubgraphWalker.Border();
            border.setEntries(List.of(astRoot));
            border.setExits(List.of(astRoot));
            return border;
        }
        return SubgraphWalker.INSTANCE.getEOGPathEdges(astRoot);
    }

    /**
     * Checks if the given {@link Node} {@code maybeChild} is contained in the sub-AST with root {@code astRoot}.
     * @param astRoot the root of the sub-AST
     * @param maybeChild the node to check
     * @return true if {@code maybeChild} is contained in the sub-AST rooted at {@code astRoot}
     */
    public static boolean isAstChild(Node astRoot, Node maybeChild) {
        return SubgraphWalker.INSTANCE.flattenAST(astRoot).contains(maybeChild);
    }

}
