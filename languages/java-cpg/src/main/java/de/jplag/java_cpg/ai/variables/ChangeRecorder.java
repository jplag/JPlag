package de.jplag.java_cpg.ai.variables;

import java.util.Set;

/**
 * Recorder for changes in variables. Can be added to {@link VariableStore}s, {@link Scope}s and {@link Variable}s to
 * track when they are changed.
 * @author ujiqk
 * @version 1.0
 */
public class ChangeRecorder {

    private final Set<Variable> changedVariables = new java.util.HashSet<>();

    /**
     * Creates a new ChangeRecorder.
     */
    public ChangeRecorder() {
        // empty
    }

    /**
     * Called by a {@link Variable} when it is changed.
     * @param variable the calling variable.
     */
    public void recordChange(Variable variable) {
        changedVariables.add(variable);
    }

    /**
     * @return the set of changed variables.
     */
    public Set<Variable> getChangedVariables() {
        return changedVariables;
    }

}
