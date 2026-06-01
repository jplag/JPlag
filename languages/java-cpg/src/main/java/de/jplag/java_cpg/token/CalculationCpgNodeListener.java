package de.jplag.java_cpg.token;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.AssignExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Reference;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.jplag.java_cpg.NodeRegistry;
import de.jplag.java_cpg.token.characteristic.CharacteristicVector;
import de.jplag.java_cpg.token.characteristic.CharacteristicVectorDimensionsMapper;

/**
 * This class is a listener for the calculation of characteristic vectors for CPG nodes. It is used in the
 * {@link de.jplag.java_cpg.passes.VectorCalculationPass}. It calculates the characteristic vector for each node and
 * stores it in the {@link NodeRegistry}.
 */
public class CalculationCpgNodeListener extends VisitorExitor<Node> {
    private final Deque<Node> nodeStack = new ArrayDeque<>();
    private final boolean doSemanticAnalysis;

    /**
     * Creates a new CalculationCpgNodeListener.
     * @param doSemanticAnalysis whether to perform semantic analysis to add characteristic vectors of referenced nodes to
     * the reference nodes.
     */
    public CalculationCpgNodeListener(boolean doSemanticAnalysis) {
        super();
        this.doSemanticAnalysis = doSemanticAnalysis;
    }

    @Override
    public void visit(@NotNull Node node) {
        nodeStack.push(node);
        if (NodeRegistry.INSTANCE.getNodeData(node) == null) {
            NodeRegistry.INSTANCE.registerNodeData(node, new CharacteristicVector());
        }
    }

    @Override
    public void exit(@NotNull Node node) {
        if (doSemanticAnalysis && node instanceof Reference reference) {
            exitReferenceNode(reference);
        }
        exitAnyNode(node);
    }

    private void exitReferenceNode(Reference node) {
        if (node.getRefersTo() instanceof MethodDeclaration) {
            return;
        }
        if (node.getLocation() == null) {
            return;
        }
        List<Node> referencedNodes = node.getPrevDFGEdges().stream().map(PropertyEdge::getStart)
                // filter for methods that write to the referenced variable
                .filter(n -> n instanceof AssignExpression || n instanceof VariableDeclaration
                        || (n instanceof UnaryOperator unaryOperator && (unaryOperator.getOperatorCode() != null
                                && (unaryOperator.getOperatorCode().equals("++") || unaryOperator.getOperatorCode().equals("--")))))
                .toList();
        Node mostPriorNode = findMostPriorNode(node, referencedNodes);
        if (mostPriorNode == null) {
            return;
        }
        CharacteristicVector referencedCharacteristicVector = NodeRegistry.INSTANCE.getNodeData(mostPriorNode);
        if (referencedCharacteristicVector == null) {
            return;
        }
        CharacteristicVector currentCharacteristicVector = NodeRegistry.INSTANCE.getNodeData(node);
        currentCharacteristicVector.addVector(referencedCharacteristicVector);
        NodeRegistry.INSTANCE.registerNodeData(node, currentCharacteristicVector);
    }

    private Node findMostPriorNode(Reference node, List<Node> referencedNodes) {
        Node mostPriorNode = null;
        if (isValidCandidate(node, node.getRefersTo())) {
            mostPriorNode = node.getRefersTo();
        }
        for (Node priorNode : referencedNodes) {
            if (!isValidCandidate(node, priorNode)) {
                continue;
            }
            if (mostPriorNode == null || (priorNode.getLocation().getRegion().getEndLine() > mostPriorNode.getLocation().getRegion().getEndLine()
                    || (priorNode.getLocation().getRegion().getEndLine() == mostPriorNode.getLocation().getRegion().getEndLine()
                            && (priorNode.getLocation().getRegion().getEndColumn() > mostPriorNode.getLocation().getRegion().getEndColumn())))) {
                mostPriorNode = priorNode;
            }
        }
        return mostPriorNode;
    }

    private void exitAnyNode(Node node) {
        if (!nodeStack.pop().equals(node)) {
            throw new IllegalStateException("Node stack is inconsistent!");
        }

        CharacteristicVector currentCharacteristicVector = NodeRegistry.INSTANCE.getNodeData(node);
        if (CharacteristicVectorDimensionsMapper.mapNodeType(node) != null) {
            currentCharacteristicVector.incrementDimension(CharacteristicVectorDimensionsMapper.mapNodeType(node));
            NodeRegistry.INSTANCE.registerNodeData(node, currentCharacteristicVector);
        }
        if (!nodeStack.isEmpty()) {
            CharacteristicVector parentCharacteristicVector = NodeRegistry.INSTANCE.getNodeData(nodeStack.getFirst());
            parentCharacteristicVector.addVector(currentCharacteristicVector);
            NodeRegistry.INSTANCE.registerNodeData(nodeStack.getFirst(), parentCharacteristicVector);
        }
    }

    private boolean isValidCandidate(Node node, Node candidate) {
        if (candidate == null || candidate.getLocation() == null || candidate instanceof RecordDeclaration) {
            return false;
        }
        // only keep nodes before the current one
        if (candidate.getLocation().getRegion().startLine > node.getLocation().getRegion().startLine
                || (candidate.getLocation().getRegion().startLine == node.getLocation().getRegion().startLine
                        && (candidate.getLocation().getRegion().startColumn >= node.getLocation().getRegion().startColumn))) {
            return false;
        }
        return Objects.equals(candidate.getLocation().getArtifactLocation().toString(), node.getLocation().getArtifactLocation().toString())
                && !candidate.getName().getLocalName().equals("this");
    }

}
