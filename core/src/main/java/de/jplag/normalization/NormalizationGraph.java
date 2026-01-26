package de.jplag.normalization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import de.jplag.Token;
import de.jplag.semantics.Variable;

/**
 * Token normalization graph, which is a directed graph based on nodes of type {@link Statement} and edges of type
 * {@link MultipleEdge}. This class class inherits from {@link SimpleDirectedGraph} to provide a data structure for the
 * token sequence normalization.
 */
public class NormalizationGraph extends SimpleDirectedGraph<Statement, MultipleEdge> {

    private static final long serialVersionUID = -8407465274643809647L; // generated

    private int bidirectionalBlockDepth;
    private final transient Collection<Statement> fullPositionSignificanceIncoming;
    private transient Statement lastFullPositionSignificance;
    private transient Statement lastPartialPositionSignificance;
    private final transient Map<Variable, Collection<Statement>> variableReads;
    private final transient Map<Variable, Collection<Statement>> variableWrites;
    private final transient Set<Statement> inCurrentBidirectionalBlock;
    private transient Statement current;

    /**
     * Creates a new normalization graph.
     * @param tokens is the token sequence from which the graph is built.
     */
    public NormalizationGraph(List<Token> tokens) {
        super(MultipleEdge.class);
        bidirectionalBlockDepth = 0;
        fullPositionSignificanceIncoming = new ArrayList<>();
        variableReads = new HashMap<>();
        variableWrites = new HashMap<>();
        inCurrentBidirectionalBlock = new HashSet<>();
        StatementBuilder builderForCurrent = new StatementBuilder(tokens.get(0).getStartLine());
        for (Token token : tokens) {
            if (token.getStartLine() != builderForCurrent.lineNumber()) {
                addStatement(builderForCurrent.build());
                builderForCurrent = new StatementBuilder(token.getStartLine());
            }
            builderForCurrent.addToken(token);
        }
        addStatement(builderForCurrent.build());
    }

    private void addStatement(Statement statement) {
        addVertex(statement);
        this.current = statement;
        processBidirectionalBlock();
        processFullPositionSignificance();
        processPartialPositionSignificance();
        processReads();
        processWrites();
        for (Variable variable : current.semantics().reads()) {
            addVariableToMap(variableReads, variable);
        }
        for (Variable variable : current.semantics().writes()) {
            addVariableToMap(variableWrites, variable);
        }
    }

    private void processBidirectionalBlock() {
        bidirectionalBlockDepth += current.semantics().bidirectionalBlockDepthChange();
        if (bidirectionalBlockDepth > 0) {
            inCurrentBidirectionalBlock.add(current);
        } else {
            inCurrentBidirectionalBlock.clear();
        }
    }

    private void processFullPositionSignificance() {
        if (current.semantics().hasFullPositionSignificance()) {
            for (Statement node : fullPositionSignificanceIncoming) {
                addIncomingEdgeToCurrent(node, EdgeType.POSITION_SIGNIFICANCE_FULL, null);
            }
            fullPositionSignificanceIncoming.clear();
            lastFullPositionSignificance = current;
        } else if (lastFullPositionSignificance != null) {
            addIncomingEdgeToCurrent(lastFullPositionSignificance, EdgeType.POSITION_SIGNIFICANCE_FULL, null);
        }
        fullPositionSignificanceIncoming.add(current);
    }

    private void processPartialPositionSignificance() {
        if (current.semantics().hasPartialPositionSignificance()) {
            if (lastPartialPositionSignificance != null) {
                addIncomingEdgeToCurrent(lastPartialPositionSignificance, EdgeType.POSITION_SIGNIFICANCE_PARTIAL, null);
            }
            lastPartialPositionSignificance = current;
        }
    }

    private void processReads() {
        for (Variable variable : current.semantics().reads()) {
            for (Statement node : variableWrites.getOrDefault(variable, Set.of())) {
                addIncomingEdgeToCurrent(node, EdgeType.VARIABLE_FLOW, variable);
            }
        }
    }

    private void processWrites() {
        for (Variable variable : current.semantics().writes()) {
            for (Statement node : variableWrites.getOrDefault(variable, Set.of())) {
                addIncomingEdgeToCurrent(node, EdgeType.VARIABLE_ORDER, variable);
            }
            for (Statement node : variableReads.getOrDefault(variable, Set.of())) {
                EdgeType edgeType = inCurrentBidirectionalBlock.contains(node) ? EdgeType.VARIABLE_REVERSE_FLOW : EdgeType.VARIABLE_ORDER;
                addIncomingEdgeToCurrent(node, edgeType, variable);
            }
        }
    }

    /**
     * Adds an incoming edge to the current node.
     * @param start the start of the edge
     * @param type the type of the edge
     * @param cause the variable that caused the edge, may be null
     */
    private void addIncomingEdgeToCurrent(Statement start, EdgeType type, Variable cause) {
        MultipleEdge multipleEdge = getEdge(start, current);
        if (multipleEdge == null) {
            multipleEdge = new MultipleEdge();
            addEdge(start, current, multipleEdge);
        }
        multipleEdge.addEdge(type, cause);
    }

    private void addVariableToMap(Map<Variable, Collection<Statement>> variableMap, Variable variable) {
        variableMap.putIfAbsent(variable, new ArrayList<>());
        variableMap.get(variable).add(current);
    }

}
