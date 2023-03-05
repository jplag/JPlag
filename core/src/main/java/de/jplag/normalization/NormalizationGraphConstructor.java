package de.jplag.normalization;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private TokenLine current;

    NormalizationGraphConstructor(List<Token> tokens) {
        graph = new SimpleDirectedGraph<>(Dependency.class);
        bidirectionalBlockDepth = 0;
        fullOrderingIngoing = new LinkedList<>();
        variableReads = new HashMap<>();
        variableWrites = new HashMap<>();
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
        bidirectionalBlockDepth += tokenLine.semantics().bidirectionalBlockDepthChange();
        processFullOrdering();
        processPartialOrdering();
        processReads();
        processWrites();
        for (Variable variable: current.semantics().reads())
            addVariableToMap(variableReads, variable);
        for (Variable variable: current.semantics().writes())
            addVariableToMap(variableWrites, variable);
    }

    private void processFullOrdering() {
        if (current.semantics().isFullOrdering()) {
            addCurrentEdges(fullOrderingIngoing, DependencyType.ORDERING_FULL, null); // ingoing edges
            fullOrderingIngoing.clear();
            lastFullOrdering = current;
        } else if (lastFullOrdering != null) {
            addCurrentEdge(lastFullOrdering, DependencyType.ORDERING_FULL, null); // outgoing edges
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
            addCurrentEdgesByVariable(variableWrites, variable, DependencyType.VARIABLE_DATA);
        }
    }

    private void processWrites() {
        DependencyType readToWriteDependencyType = bidirectionalBlockDepth > 0 ? DependencyType.VARIABLE_REVERSE_DATA : DependencyType.VARIABLE_ORDER;
        for (Variable variable : current.semantics().writes()) {
            addCurrentEdgesByVariable(variableWrites, variable, DependencyType.VARIABLE_ORDER);
            addCurrentEdgesByVariable(variableReads, variable, readToWriteDependencyType);
            addVariableToMap(variableWrites, variable);
        }
    }

    private void addCurrentEdgesByVariable(Map<Variable, Collection<TokenLine>> variableMap, Variable variable, DependencyType type) {
        addCurrentEdges(variableMap.getOrDefault(variable, new LinkedList<>()), type, variable);
    }

    private void addCurrentEdges(Collection<TokenLine> starts, DependencyType type, Variable cause) {
        starts.forEach(s -> addCurrentEdge(s, type, cause));
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
