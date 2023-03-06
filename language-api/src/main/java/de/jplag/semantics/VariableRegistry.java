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
    private Map<String, Variable> globalVariables; // map global variable name to variable
    private Deque<Map<String, Variable>> memberVariables; // map member variable name to stack of variables
    private Map<String, Deque<Variable>> localVariables; // map local variable name to stack of variables
    private Deque<Set<String>> localVariablesByScope; // stack of local variable names in scope
    private NextOperation nextOperation;
    private boolean ignoreNextOperation;
    private boolean mutableWrite;

    public VariableRegistry() {
        this.globalVariables = new HashMap<>();
        this.memberVariables = new LinkedList<>();
        this.localVariables = new HashMap<>();
        this.localVariablesByScope = new LinkedList<>();
        this.nextOperation = NextOperation.READ; // the default
        this.ignoreNextOperation = false;
        this.mutableWrite = false;
    }

    public void setNextOperation(NextOperation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public void setIgnoreNextOperation(boolean ignoreNextOperation) {
        this.ignoreNextOperation = ignoreNextOperation;
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
        // get local variable if exists
        Deque<Variable> variableIdStack = localVariables.get(variableName);
        if (variableIdStack != null)
            return variableIdStack.getLast();
        // get member variable if exists
        Variable variable = getMemberVariable(variableName);
        if (variable != null)
            return variable;
        // if (nextOperation.isWrite) System.err.println(variableName); <- can uncover bugs
        // get global variable, register if it doesn't exist
        variable = globalVariables.get(variableName);
        if (variable != null)
            return variable;
        variable = new Variable(variableName, false, true);
        globalVariables.put(variableName, variable);
        return variable;
    }

    public void registerMemberVariable(String variableName, boolean mutable) {
        Variable variable = new Variable(variableName, true, mutable);
        memberVariables.getLast().put(variableName, variable);
    }

    public void registerLocalVariable(String variableName, boolean mutable) {
        Variable variable = new Variable(variableName, false, mutable);
        localVariables.putIfAbsent(variableName, new LinkedList<>());
        localVariables.get(variableName).addLast(variable);
        localVariablesByScope.getLast().add(variableName);
    }

    public void addAllNonLocalVariablesAsReads(CodeSemantics semantics) {
        Set<Variable> nonLocalVariables = new HashSet<>(globalVariables.values());
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

    public void registerVariableOperation(String variableName, boolean isOwnMember, CodeSemantics semantics) {
        if (ignoreNextOperation) {
            ignoreNextOperation = false;
            return;
        }
        Variable variable = isOwnMember ? getMemberVariable(variableName) : getVariable(variableName);
        if (nextOperation.isRead)
            semantics.addRead(variable);
        if (nextOperation.isWrite || (mutableWrite && variable.isMutable()))
            semantics.addWrite(variable);
        nextOperation = NextOperation.READ;
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
