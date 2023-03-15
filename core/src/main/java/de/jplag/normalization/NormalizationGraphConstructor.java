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
    private Collection<TokenLine> fullOrderingIngoing;
    private TokenLine lastFullOrdering;
    private TokenLine lastPartialOrdering;
    private Map<Variable, Collection<TokenLine>> variableReads;
    private Map<Variable, Collection<TokenLine>> variableWrites;
    private Set<TokenLine> inCurrentBidirectionalBlock;
    private TokenLine current;

    NormalizationGraphConstructor(List<Token> tokens) {
        graph = new SimpleDirectedGraph<>(Dependency.class);
        bidirectionalBlockDepth = 0;
        fullOrderingIngoing = new LinkedList<>();
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
        processFullOrdering();
        processPartialOrdering();
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

    private void processFullOrdering() {
        if (current.semantics().isFullOrdering()) {
            for (TokenLine node: fullOrderingIngoing)
                addCurrentEdge(node, DependencyType.ORDERING_FULL, null);
            fullOrderingIngoing.clear();
            lastFullOrdering = current;
        } else if (lastFullOrdering != null) {
            addCurrentEdge(lastFullOrdering, DependencyType.ORDERING_FULL, null);
        }
        fullOrderingIngoing.add(current);
    }

    private void processPartialOrdering() {
        if (current.semantics().isPartialOrdering()) {
            if (lastPartialOrdering != null) {
                addCurrentEdge(lastPartialOrdering, DependencyType.ORDERING_PARTIAL, null);
            }
            lastPartialOrdering = current;
        }
    }

    private void processReads() {
        for (Variable variable : current.semantics().reads()) {
            for (TokenLine node : variableWrites.getOrDefault(variable, Set.of()))
                addCurrentEdge(node, DependencyType.VARIABLE_DATA, variable);
        }
    }

    private void processWrites() {
        for (Variable variable : current.semantics().writes()) {
            for (TokenLine node : variableWrites.getOrDefault(variable, Set.of()))
                addCurrentEdge(node, DependencyType.VARIABLE_ORDER, variable);
            for (TokenLine node : variableReads.getOrDefault(variable, Set.of())) {
                DependencyType dependencyType = inCurrentBidirectionalBlock.contains(node) ? //
                        DependencyType.VARIABLE_REVERSE_DATA : DependencyType.VARIABLE_ORDER;
                addCurrentEdge(node, dependencyType, variable);
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
    private void addCurrentEdge(TokenLine start, DependencyType type, Variable cause) {
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
