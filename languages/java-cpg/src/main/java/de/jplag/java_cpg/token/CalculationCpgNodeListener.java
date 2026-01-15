package de.jplag.java_cpg.token;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.jplag.java_cpg.NodeRegistry;
import de.jplag.java_cpg.token.characteristic.CharacteristicVector;
import de.jplag.java_cpg.token.characteristic.CharacteristicVectorDimensionsMapper;

public class CalculationCpgNodeListener extends VisitorExitor<Node> {
    private final Deque<Node> nodeStack = new ArrayDeque<>();
    private final boolean doSemanticAnalysis;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CalculationCpgNodeListener.class);

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
        List<Node> referencedNodes = node.getPrevDFGEdges().stream().map(PropertyEdge::getStart)
                // filter for methods that write to the referenced variable
                .filter(n -> n instanceof AssignExpression || n instanceof VariableDeclaration
                        || (n instanceof UnaryOperator unaryOperator && (unaryOperator.getOperatorCode() != null
                                && (unaryOperator.getOperatorCode().equals("++") || unaryOperator.getOperatorCode().equals("--")))))
                .toList();
        Node mostPriorNode = node.getRefersTo();
        if (mostPriorNode == null) {
            return;
        }
        for (Node priorNode : referencedNodes) {
            if (priorNode.getLocation() == null || mostPriorNode.getLocation() == null || node.getLocation() == null) {
                continue;
            }
            // only keep nodes before the current one
            if (priorNode.getLocation().getRegion().startLine > node.getLocation().getRegion().startLine
                    || (priorNode.getLocation().getRegion().startLine == node.getLocation().getRegion().startLine
                            && (priorNode.getLocation().getRegion().startColumn >= node.getLocation().getRegion().startColumn))) {
                continue;
            }
            if (priorNode.getLocation().getRegion().getEndLine() > mostPriorNode.getLocation().getRegion().getEndLine()
                    || (priorNode.getLocation().getRegion().getEndLine() == mostPriorNode.getLocation().getRegion().getEndLine()
                            && (priorNode.getLocation().getRegion().getEndColumn() > mostPriorNode.getLocation().getRegion().getEndColumn()))) {
                mostPriorNode = priorNode;
            }
        }
        // dont add references to "this"
        if (mostPriorNode.getName().getLocalName().equals("this")) {
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

}
