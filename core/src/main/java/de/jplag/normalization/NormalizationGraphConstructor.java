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

class NormalizationGraphConstructor {
    private SimpleDirectedGraph<Statement, Edge> graph;
    private int bidirectionalBlockDepth;
    private Collection<Statement> fullPositionSignificanceIngoing;
    private Statement lastFullPositionSignificance;
    private Statement lastPartialPositionSignificance;
    private Map<Variable, Collection<Statement>> variableReads;
    private Map<Variable, Collection<Statement>> variableWrites;
    private Set<Statement> inCurrentBidirectionalBlock;
    private Statement current;

    NormalizationGraphConstructor(List<Token> tokens) {
        graph = new SimpleDirectedGraph<>(Edge.class);
        bidirectionalBlockDepth = 0;
        fullPositionSignificanceIngoing = new ArrayList<>();
        variableReads = new HashMap<>();
        variableWrites = new HashMap<>();
        inCurrentBidirectionalBlock = new HashSet<>();
        StatementBuilder current = new StatementBuilder(tokens.get(0).getLine());
        for (Token token : tokens) {
            if (token.getLine() != current.lineNumber()) {
                addStatement(current.build());
                current = new StatementBuilder(token.getLine());
            }
            current.addToken(token);
        }
        addStatement(current.build());
    }

    SimpleDirectedGraph<Statement, Edge> get() {
        return graph;
    }

    private void addStatement(Statement statement) {
        graph.addVertex(statement);
        this.current = statement;
        processBidirectionalBlock();
        processFullPositionSignificance();
        processPartialPositionSignificance();
        processReads();
        processWrites();
        for (Variable variable : current.semantics().reads())
            addVariableToMap(variableReads, variable);
        for (Variable variable : current.semantics().writes())
            addVariableToMap(variableWrites, variable);
    }

    private void processBidirectionalBlock() {
        bidirectionalBlockDepth += current.semantics().bidirectionalBlockDepthChange();
        if (bidirectionalBlockDepth > 0)
            inCurrentBidirectionalBlock.add(current);
        else
            inCurrentBidirectionalBlock.clear();
    }

    private void processFullPositionSignificance() {
        if (current.semantics().hasFullPositionSignificance()) {
            for (Statement node : fullPositionSignificanceIngoing)
                addIngoingEdgeToCurrent(node, EdgeType.POSITION_SIGNIFICANCE_FULL, null);
            fullPositionSignificanceIngoing.clear();
            lastFullPositionSignificance = current;
        } else if (lastFullPositionSignificance != null) {
            addIngoingEdgeToCurrent(lastFullPositionSignificance, EdgeType.POSITION_SIGNIFICANCE_FULL, null);
        }
        fullPositionSignificanceIngoing.add(current);
    }

    private void processPartialPositionSignificance() {
        if (current.semantics().hasPartialPositionSignificance()) {
            if (lastPartialPositionSignificance != null) {
                addIngoingEdgeToCurrent(lastPartialPositionSignificance, EdgeType.POSITION_SIGNIFICANCE_PARTIAL, null);
            }
            lastPartialPositionSignificance = current;
        }
    }

    private void processReads() {
        for (Variable variable : current.semantics().reads()) {
            for (Statement node : variableWrites.getOrDefault(variable, Set.of()))
                addIngoingEdgeToCurrent(node, EdgeType.VARIABLE_FLOW, variable);
        }
    }

    private void processWrites() {
        for (Variable variable : current.semantics().writes()) {
            for (Statement node : variableWrites.getOrDefault(variable, Set.of()))
                addIngoingEdgeToCurrent(node, EdgeType.VARIABLE_ORDER, variable);
            for (Statement node : variableReads.getOrDefault(variable, Set.of())) {
                EdgeType edgeType = inCurrentBidirectionalBlock.contains(node) ? //
                        EdgeType.VARIABLE_REVERSE_FLOW : EdgeType.VARIABLE_ORDER;
                addIngoingEdgeToCurrent(node, edgeType, variable);
            }
        }
    }

    /**
     * Adds an ingoing edge to the current node.
     * @param start the start of the edge
     * @param type the type of the edge
     * @param cause the variable that caused the edge, may be null
     */
    private void addIngoingEdgeToCurrent(Statement start, EdgeType type, Variable cause) {
        Edge edge = graph.getEdge(start, current);
        if (edge == null) {
            edge = new Edge();
            graph.addEdge(start, current, edge);
        }
        edge.addItem(type, cause);
    }

    private void addVariableToMap(Map<Variable, Collection<Statement>> variableMap, Variable variable) {
        variableMap.putIfAbsent(variable, new ArrayList<>());
        variableMap.get(variable).add(current);
    }
}
