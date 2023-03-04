package de.jplag.normalization;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.SimpleDirectedGraph;

import de.jplag.Token;
import de.jplag.semantics.BlockRelation;
import de.jplag.semantics.Ordering;
import de.jplag.semantics.Variable;

class NormalizationGraphConstructor {
    private SimpleDirectedGraph<TokenLine, Dependency> graph;
    private int loopDepth;
    private Collection<TokenLine> fullOrderingIngoing;
    private TokenLine lastFullOrdering;
    private TokenLine lastPartialOrdering;
    private Map<Variable, Collection<TokenLine>> variableReads;
    private Map<Variable, Collection<TokenLine>> variableWrites;
    private TokenLine current;

    NormalizationGraphConstructor(List<Token> tokens) {
        graph = new SimpleDirectedGraph<>(Dependency.class);
        loopDepth = 0;
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
        processLoops();
        processFullOrdering();
        processPartialOrdering();
        processReads();
        processWrites();
        current.semantics().reads().forEach(r -> addVarToMap(r, variableReads));
        current.semantics().writes().forEach(w -> addVarToMap(w, variableWrites));

    }

    private void processLoops() {
        if (current.semantics().bidirectionalBlockRelation() == BlockRelation.BEGINS_BLOCK)
            loopDepth++;
        if (current.semantics().bidirectionalBlockRelation() == BlockRelation.ENDS_BLOCK)
            loopDepth--;
    }

    private void processFullOrdering() {
        if (current.semantics().ordering() == Ordering.FULL) {
            addCurrentEdges(fullOrderingIngoing, DependencyType.CONTROL, null); // ingoing edges
            fullOrderingIngoing.clear();
            lastFullOrdering = current;
        } else if (lastFullOrdering != null) {
            addCurrentEdge(lastFullOrdering, DependencyType.CONTROL, null); // outgoing edges
        }
        fullOrderingIngoing.add(current);
    }

    private void processPartialOrdering() {
        if (current.semantics().ordering() == Ordering.PARTIAL) {
            if (lastPartialOrdering != null) {
                addCurrentEdge(lastPartialOrdering, DependencyType.CRITICAL, null);
            }
            lastPartialOrdering = current;
        }
    }

    private void processReads() {
        for (Variable r : current.semantics().reads()) {
            addCurrentEdgesVar(DependencyType.DATA, r, variableWrites);
        }
    }

    private void processWrites() {
        DependencyType writeToReadDependencyType = loopDepth > 0 ? DependencyType.DATA_THROUGH_LOOP : DependencyType.ORDER;
        for (Variable w : current.semantics().writes()) {
            addCurrentEdgesVar(DependencyType.ORDER, w, variableWrites);
            addCurrentEdgesVar(writeToReadDependencyType, w, variableReads);
            addVarToMap(w, variableWrites);
        }
    }

    private void addCurrentEdgesVar(DependencyType type, Variable var, Map<Variable, Collection<TokenLine>> varMap) {
        addCurrentEdges(varMap.getOrDefault(var, new LinkedList<>()), type, var);
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

    private void addVarToMap(Variable var, Map<Variable, Collection<TokenLine>> varMap) {
        varMap.putIfAbsent(var, new LinkedList<>());
        varMap.get(var).add(current);
    }
}
