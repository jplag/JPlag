package de.jplag.semantics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Helper class to assist in generating token semantics. For languages similar in structure to Java/C
 */
public class VariableRegistry {
    private Map<String, Variable> memberVariables; // map member variable name to variable
    private Map<String, Stack<Variable>> localVariables; // map local variable name to variable
    private Stack<Set<String>> localVariablesByScope; // stack of local variable names in scope
    private NextOperation nextOperation;
    private boolean ignoreNextOperation;
    private boolean mutableWrite;

    public VariableRegistry() {
        this.memberVariables = new HashMap<>();
        this.localVariables = new HashMap<>();
        this.localVariablesByScope = new Stack<>();
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
        return memberVariables.get(variableName);
    }

    private Variable getVariable(String variableName) {
        Stack<Variable> variableIdStack = localVariables.get(variableName);
        if (variableIdStack != null) {
            return variableIdStack.peek();
        }
        return getMemberVariable(variableName);
    }

    public Variable registerMemberVariable(String variableName, boolean mutable) {
        Variable variable = new Variable(variableName, true, mutable);
        memberVariables.put(variableName, variable);
        return variable;
    }

    public Variable registerLocalVariable(String variableName, boolean mutable) {
        Variable variable = new Variable(variableName, false, mutable);
        localVariables.putIfAbsent(variableName, new Stack<>());
        localVariables.get(variableName).push(variable);
        localVariablesByScope.peek().add(variableName);
        return variable;
    }

    public void addAllMemberVariablesAsReads(TokenSemantics semantics) {
        for (Variable memberVar : memberVariables.values()) {
            semantics.addRead(memberVar);
        }
    }

    public void clearMemberVariables() {
        memberVariables.clear();
    }

    public void registerVariableOperation(String variableName, boolean isOwnMember, TokenSemantics semantics) {
        if (!ignoreNextOperation) {
            Variable variable = isOwnMember ? getMemberVariable(variableName) : getVariable(variableName);
            if (variable != null) {
                if (nextOperation.isRead) {
                    semantics.addRead(variable);
                }
                if (nextOperation.isWrite || (nextOperation.isRead && mutableWrite && variable.isMutable())) {
                    semantics.addWrite(variable);
                }
            }
            nextOperation = NextOperation.READ;
        }
        ignoreNextOperation = false;
    }

    public void enterLocalScope() {
        localVariablesByScope.add(new HashSet<>());
    }

    public void exitLocalScope() {
        for (String variableName : localVariablesByScope.pop()) {
            Stack<Variable> variableStack = localVariables.get(variableName);
            variableStack.pop();
            if (variableStack.isEmpty()) {
                localVariables.remove(variableName);
            }
        }
    }
}
