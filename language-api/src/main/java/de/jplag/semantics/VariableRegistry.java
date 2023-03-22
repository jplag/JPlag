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
    private Map<String, Variable> unscopedVariables;
    private Deque<Map<String, Variable>> memberVariables; // map member variable name to stack of variables
    private Map<String, Deque<Variable>> localVariables; // map local variable name to stack of variables
    private Deque<Set<String>> localVariablesByScope; // stack of local variable names in scope
    private VariableAccessType nextVariableAccessType;
    private boolean ignoreNextVariableAccess;
    private boolean mutableWrite;

    /**
     * Initialize a new variable registry.
     */
    public VariableRegistry() {
        this.unscopedVariables = new HashMap<>();
        this.memberVariables = new LinkedList<>();
        this.localVariables = new HashMap<>();
        this.localVariablesByScope = new LinkedList<>();
        this.nextVariableAccessType = VariableAccessType.READ; // the default
        this.ignoreNextVariableAccess = false;
        this.mutableWrite = false;
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

    public boolean inLocalScope() {
        return !localVariablesByScope.isEmpty();
    }

    private Variable getMemberVariable(String variableName) {
        Map<String, Variable> currentMemberVariables = memberVariables.peek();
        return currentMemberVariables != null ? memberVariables.getLast().get(variableName) : null;
    }

    private Variable getVariable(String variableName) {
        Deque<Variable> variableIdStack = localVariables.get(variableName);
        if (variableIdStack != null)
            return variableIdStack.getLast();
        Variable variable = getMemberVariable(variableName);
        return variable != null ? variable : unscopedVariables.get(variableName);
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

    public void registerVariable(String variableName, Scope scope, boolean mutable) {
        Variable variable = new Variable(variableName, scope, mutable);
        switch (scope) {
            case FILE -> unscopedVariables.put(variableName, variable);
            case CLASS -> memberVariables.getLast().put(variableName, variable);
            case LOCAL -> {
                localVariables.putIfAbsent(variableName, new LinkedList<>());
                localVariables.get(variableName).addLast(variable);
                localVariablesByScope.getLast().add(variableName);
            }
        }
    }

    public void addAllNonLocalVariablesAsReads(CodeSemantics semantics) {
        Set<Variable> nonLocalVariables = new HashSet<>();
        for (Map<String, Variable> classMemberVariables: memberVariables)
            nonLocalVariables.addAll(classMemberVariables.values());
        for (Variable variable : nonLocalVariables)
            semantics.addRead(variable);
    }

    public void enterClass() {
        memberVariables.addLast(new HashMap<>());
    }

    public void exitClass() {
        memberVariables.removeLast();
    }

    public void registerVariableAccess(String variableName, boolean isOwnMember, CodeSemantics semantics) {
        if (ignoreNextVariableAccess) {
            ignoreNextVariableAccess = false;
            return;
        }
        Variable variable = isOwnMember ? getMemberVariable(variableName) : getVariable(variableName);
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
}
