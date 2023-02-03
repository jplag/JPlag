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
    private int loopDepth;
    private Collection<TokenLine> controlAffected;
    private TokenLine lastControl;
    private TokenLine lastCritical;
    private Map<Variable, Collection<TokenLine>> variableReads;
    private Map<Variable, Collection<TokenLine>> variableWrites;
    private TokenLine current;

    NormalizationGraphConstructor(List<Token> tokens) {
        graph = new SimpleDirectedGraph<>(Dependency.class);
        loopDepth = 0;
        controlAffected = new LinkedList<>();
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

    private void addTokenLine(TokenLine tokenLine) {
        graph.addVertex(tokenLine);
        this.current = tokenLine;

        processLoops();
        processControl();
        processCritical();
        processReads();
        processWrites();
        current.semantics().reads().forEach(r -> addVarToMap(r, variableReads));
        current.semantics().writes().forEach(w -> addVarToMap(w, variableWrites));

    }

    SimpleDirectedGraph<TokenLine, Dependency> get() {
        return graph;
    }

    private void processLoops() {
        if (current.semantics().loopBegin())
            loopDepth++;
        if (current.semantics().loopEnd())
            loopDepth--;
    }

    private void processControl() {
        if (current.semantics().control()) {
            addCurrentEdges(controlAffected, DependencyType.CONTROL, null); // edges to control lines
            controlAffected.clear();
            lastControl = current;
        } else if (lastControl != null) {
            addCurrentEdge(lastControl, DependencyType.CONTROL, null); // edge from control lines
        }
        controlAffected.add(current);
    }

    private void processCritical() {
        if (current.semantics().critical()) {
            if (lastCritical != null) {
                addCurrentEdge(lastCritical, DependencyType.CRITICAL, null);
            }
            lastCritical = current;
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
