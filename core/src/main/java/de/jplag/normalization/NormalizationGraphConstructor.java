package de.jplag.normalization;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import de.jplag.Token;
import de.jplag.semantics.Variable;

class NormalizationGraphConstructor {
    private SimpleDirectedGraph<Statement, Edge> graph;
    private int bidirectionalBlockDepth;
    private Collection<Statement> fullPositionalSignificanceIngoing;
    private Statement lastFullPositionalSignificance;
    private Statement lastPartialPositionalSignificance;
    private Map<Variable, Collection<Statement>> variableReads;
    private Map<Variable, Collection<Statement>> variableWrites;
    private Set<Statement> inCurrentBidirectionalBlock;
    private Statement current;

    NormalizationGraphConstructor(List<Token> tokens) {
        graph = new SimpleDirectedGraph<>(Edge.class);
        bidirectionalBlockDepth = 0;
        fullPositionalSignificanceIngoing = new LinkedList<>();
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
        processFullPositionalSignificance();
        processPartialPositionalSignificance();
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

    private void processFullPositionalSignificance() {
        if (current.semantics().hasFullPositionSignificance()) {
            for (Statement node: fullPositionalSignificanceIngoing)
                addIngoingEdgeToCurrent(node, EdgeType.POSITION_SIGNIFICANCE_FULL, null);
            fullPositionalSignificanceIngoing.clear();
            lastFullPositionalSignificance = current;
        } else if (lastFullPositionalSignificance != null) {
            addIngoingEdgeToCurrent(lastFullPositionalSignificance, EdgeType.POSITION_SIGNIFICANCE_FULL, null);
        }
        fullPositionalSignificanceIngoing.add(current);
    }

    private void processPartialPositionalSignificance() {
        if (current.semantics().hasPartialPositionSignificance()) {
            if (lastPartialPositionalSignificance != null) {
                addIngoingEdgeToCurrent(lastPartialPositionalSignificance, EdgeType.POSITION_SIGNIFICANCE_PARTIAL, null);
            }
            lastPartialPositionalSignificance = current;
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
            addVariableToMap(variableWrites, variable);
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
        variableMap.putIfAbsent(variable, new LinkedList<>());
        variableMap.get(variable).add(current);
    }
}
