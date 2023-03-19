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
    private SimpleDirectedGraph<TokenLine, Dependency> graph;
    private int bidirectionalBlockDepth;
    private Collection<TokenLine> fullPositionalSignificanceIngoing;
    private TokenLine lastFullPositionalSignificance;
    private TokenLine lastPartialPositionalSignificance;
    private Map<Variable, Collection<TokenLine>> variableReads;
    private Map<Variable, Collection<TokenLine>> variableWrites;
    private Set<TokenLine> inCurrentBidirectionalBlock;
    private TokenLine current;

    NormalizationGraphConstructor(List<Token> tokens) {
        graph = new SimpleDirectedGraph<>(Dependency.class);
        bidirectionalBlockDepth = 0;
        fullPositionalSignificanceIngoing = new LinkedList<>();
        variableReads = new HashMap<>();
        variableWrites = new HashMap<>();
        inCurrentBidirectionalBlock = new HashSet<>();
        TokenLineBuilder currentLine = new TokenLineBuilder(tokens.get(0).getLine());
        for (Token token : tokens) {
            if (token.getLine() != currentLine.lineNumber()) {
                addTokenLine(currentLine.build());
                currentLine = new TokenLineBuilder(token.getLine());
            }
            currentLine.addToken(token);
        }
        addTokenLine(currentLine.build());
    }

    SimpleDirectedGraph<TokenLine, Dependency> get() {
        return graph;
    }

    private void addTokenLine(TokenLine tokenLine) {
        graph.addVertex(tokenLine);
        this.current = tokenLine;
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
            for (TokenLine node: fullPositionalSignificanceIngoing)
                addIngoingEdgeToCurrent(node, DependencyType.POSITION_SIGNIFICANCE_FULL, null);
            fullPositionalSignificanceIngoing.clear();
            lastFullPositionalSignificance = current;
        } else if (lastFullPositionalSignificance != null) {
            addIngoingEdgeToCurrent(lastFullPositionalSignificance, DependencyType.POSITION_SIGNIFICANCE_FULL, null);
        }
        fullPositionalSignificanceIngoing.add(current);
    }

    private void processPartialPositionalSignificance() {
        if (current.semantics().hasPartialPositionSignificance()) {
            if (lastPartialPositionalSignificance != null) {
                addIngoingEdgeToCurrent(lastPartialPositionalSignificance, DependencyType.POSITION_SIGNIFICANCE_PARTIAL, null);
            }
            lastPartialPositionalSignificance = current;
        }
    }

    private void processReads() {
        for (Variable variable : current.semantics().reads()) {
            for (TokenLine node : variableWrites.getOrDefault(variable, Set.of()))
                addIngoingEdgeToCurrent(node, DependencyType.VARIABLE_FLOW, variable);
        }
    }

    private void processWrites() {
        for (Variable variable : current.semantics().writes()) {
            for (TokenLine node : variableWrites.getOrDefault(variable, Set.of()))
                addIngoingEdgeToCurrent(node, DependencyType.VARIABLE_ORDER, variable);
            for (TokenLine node : variableReads.getOrDefault(variable, Set.of())) {
                DependencyType dependencyType = inCurrentBidirectionalBlock.contains(node) ? //
                        DependencyType.VARIABLE_REVERSE_FLOW : DependencyType.VARIABLE_ORDER;
                addIngoingEdgeToCurrent(node, dependencyType, variable);
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
    private void addIngoingEdgeToCurrent(TokenLine start, DependencyType type, Variable cause) {
        Dependency dependency = graph.getEdge(start, current);
        if (dependency == null) {
            dependency = new Dependency();
            graph.addEdge(start, current, dependency);
        }
        dependency.addItem(type, cause);
    }

    private void addVariableToMap(Map<Variable, Collection<TokenLine>> variableMap, Variable variable) {
        variableMap.putIfAbsent(variable, new LinkedList<>());
        variableMap.get(variable).add(current);
    }
}
