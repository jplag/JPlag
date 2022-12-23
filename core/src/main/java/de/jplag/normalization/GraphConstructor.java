package de.jplag.normalization;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.SimpleDirectedGraph;

import de.jplag.semantics.Variable;

public class GraphConstructor {
    private SimpleDirectedGraph<TokenGroup, Dependency> graph;
    private int loopCount;
    private Collection<TokenGroup> controlAffected;
    private TokenGroup lastControl;
    private Map<Variable, Collection<TokenGroup>> variableReads;
    private Map<Variable, Collection<TokenGroup>> variableWrites;
    private TokenGroup current;

    public GraphConstructor(List<TokenGroup> tokenGroups) {
        graph = new SimpleDirectedGraph<>(Dependency.class);
        loopCount = 0;
        controlAffected = new LinkedList<>();
        lastControl = null;
        variableReads = new HashMap<>();
        variableWrites = new HashMap<>();
        for (TokenGroup current : tokenGroups) {
            graph.addVertex(current);
            this.current = current;
            processLoops();
            processControl();
            processReads();
            processWrites();
        }
    }

    public SimpleDirectedGraph<TokenGroup, Dependency> get() {
        return graph;
    }

    private void processLoops() {
        if (current.semantics.loopBegin())
            loopCount++;
        if (current.semantics.loopEnd())
            loopCount--;
    }

    private void processControl() {
        if (current.semantics.control()) {
            addCurrentEdges(controlAffected, DependencyType.CONTROL, null);
            controlAffected.clear();
            lastControl = current;
        } else {
            addCurrentEdge(lastControl, DependencyType.CONTROL, null);
        }
        controlAffected.add(current);
    }

    private void processReads() {
        for (Variable r : current.semantics.reads()) {
            addCurrentEdgesVar(DependencyType.DATA, r, variableWrites);
            addVarToMap(r, variableReads);
        }
    }

    private void processWrites() {
        DependencyType writeToReadDependencyType = loopCount > 0 ? DependencyType.DATA : DependencyType.ORDER;
        for (Variable w : current.semantics.writes()) {
            addCurrentEdgesVar(DependencyType.ORDER, w, variableWrites);
            addCurrentEdgesVar(writeToReadDependencyType, w, variableReads);
            addVarToMap(w, variableWrites);
        }
    }

    private void addCurrentEdgesVar(DependencyType type, Variable var, Map<Variable, Collection<TokenGroup>> varMap) {
        addCurrentEdges(varMap.getOrDefault(var, new LinkedList<>()), type, var);
    }

    private void addCurrentEdges(Collection<TokenGroup> starts, DependencyType type, Variable cause) {
        starts.forEach(s -> addCurrentEdge(s, type, cause));
    }

    private void addCurrentEdge(TokenGroup start, DependencyType type, Variable cause) {
        Dependency dependency = graph.getEdge(start, current);
        if (dependency == null) {
            dependency = new Dependency();
            graph.addEdge(start, current, dependency);
        }
        dependency.addItem(type, cause);
    }

    private void addVarToMap(Variable var, Map<Variable, Collection<TokenGroup>> varMap) {
        varMap.putIfAbsent(var, new LinkedList<>());
        varMap.get(var).add(current);
    }
}
