package de.jplag.semantics;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to assist in generating token semantics. For languages similar in structure to Java/C
 */
public class VariableRegistry {
    private Map<String, Variable> fileVariables;
    private Deque<Map<String, Variable>> classVariables; // map class name to map of variable names to variables
    private Map<String, Deque<Variable>> localVariables; // map local variable name to stack of variables
    private Deque<Set<String>> localVariablesByScope; // stack of local variable names in scope
    private VariableAccessType nextVariableAccessType;
    private boolean ignoreNextVariableAccess;
    private boolean mutableWrite;

    /**
     * Initialize a new variable registry.
     */
    public VariableRegistry() {
        this.fileVariables = new HashMap<>();
        this.classVariables = new LinkedList<>();
        this.localVariables = new HashMap<>();
        this.localVariablesByScope = new LinkedList<>();
        this.nextVariableAccessType = VariableAccessType.READ; // the default
        this.ignoreNextVariableAccess = false;
        this.mutableWrite = false;
    }

    public boolean inLocalScope() {
        return !localVariablesByScope.isEmpty();
    }

    /**
     * Set the next variable acc. This only influences the very next call of registerVariableOperation.
     * @param nextVariableAccessType the new value
     */
    public void setNextVariableAccessType(VariableAccessType nextVariableAccessType) {
        this.nextVariableAccessType = nextVariableAccessType;
    }

    /**
     *
     * @param ignoreNextVariableAccess
     */
    public void setIgnoreNextVariableAccess(boolean ignoreNextVariableAccess) {
        this.ignoreNextVariableAccess = ignoreNextVariableAccess;
    }

    public void setMutableWrite(boolean mutableWrite) {
        this.mutableWrite = mutableWrite;
    }

    public void enterClass() {
        classVariables.addLast(new HashMap<>());
    }

    public void exitClass() {
        classVariables.removeLast();
    }

    public void enterLocalScope() {
        localVariablesByScope.addLast(new HashSet<>());
    }

    public void exitLocalScope() {
        for (String variableName : localVariablesByScope.removeLast()) {
            Deque<Variable> variableStack = localVariables.get(variableName);
            variableStack.removeLast();
            if (variableStack.isEmpty())
                localVariables.remove(variableName);
        }
    }

    public void registerVariable(String variableName, Scope scope, boolean mutable) {
        Variable variable = new Variable(variableName, scope, mutable);
        switch (scope) {
            case FILE -> fileVariables.put(variableName, variable);
            case CLASS -> classVariables.getLast().put(variableName, variable);
            case LOCAL -> {
                localVariables.putIfAbsent(variableName, new LinkedList<>());
                localVariables.get(variableName).addLast(variable);
                localVariablesByScope.getLast().add(variableName);
            }
        }
    }

    public void registerVariableAccess(String variableName, boolean isClassVariable, CodeSemantics semantics) {
        if (ignoreNextVariableAccess) {
            ignoreNextVariableAccess = false;
            return;
        }
        Variable variable = isClassVariable ? getClassVariable(variableName) : getVariable(variableName);
        if (variable != null) {
            if (nextVariableAccessType.isRead)
                semantics.addRead(variable);
            if (nextVariableAccessType.isWrite || (mutableWrite && variable.isMutable()))
                semantics.addWrite(variable);
        } else if (nextVariableAccessType.isWrite || mutableWrite) {
            semantics.markKeep();
            semantics.markFullPositionSignificance();  // since we don't track reads...
        }
        nextVariableAccessType = VariableAccessType.READ;
    }

    public void addAllNonLocalVariablesAsReads(CodeSemantics semantics) {
        Set<Variable> nonLocalVariables = new HashSet<>(fileVariables.values());
        nonLocalVariables.addAll(classVariables.getLast().values());
        for (Variable variable : nonLocalVariables)
            semantics.addRead(variable);
    }

    private Variable getVariable(String variableName) {
        Deque<Variable> variableIdStack = localVariables.get(variableName);
        if (variableIdStack != null)
            return variableIdStack.getLast();  // stack is never empty
        Variable variable = getClassVariable(variableName);
        return variable != null ? variable : fileVariables.get(variableName);
        /* todo track global variables -> hard, how to differentiate SomeClass.staticAttr++ from String.join(...)
        // problem here: all String.joins (for example) are registered as writes to String
        // get global variable, register if it doesn't exist
        variable = globalVariables.get(variableName);
        if (variable != null)
            return variable;
        variable = new Variable(variableName, false, true);
        globalVariables.put(variableName, variable);
        return variable;
         */
    }

    private Variable getClassVariable(String variableName) {
        Map<String, Variable> currentClassVariables = classVariables.peekLast();
        return currentClassVariables != null ? currentClassVariables.get(variableName) : null;
    }
}
