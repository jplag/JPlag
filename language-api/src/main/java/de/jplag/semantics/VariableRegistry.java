package de.jplag.semantics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Variable helper class to assist generating token semantics. For languages similar in structure to Java/C
 */
public class VariableRegistry {
    private Map<String, Variable> memberVariables; // map member variable name to variable
    private Map<String, Stack<Variable>> localVariables; // map local variable name to variable
    private Stack<Set<String>> localVariablesByScope; // stack of local variable names in scope
    private Map<Variable, Boolean> isMutable; // map variable to whether it is mutable
    private boolean mutableWrite;
    private NextOperation nextOperation;

    public VariableRegistry() {
        this.memberVariables = new HashMap<>();
        this.localVariables = new HashMap<>();
        this.localVariablesByScope = new Stack<>();
        this.isMutable = new HashMap<>();
        this.mutableWrite = false;
        this.nextOperation = NextOperation.READ; // the default
    }

    public void mutableWrite() {
        mutableWrite = true;
    }

    public void noMutableWrite() {
        mutableWrite = false;
    }

    public void setNextOperation(NextOperation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public boolean inLocalScope() {
        return !localVariablesByScope.isEmpty();
    }

    public Variable getMemberVariable(String variableName) {
        return memberVariables.get(variableName);
    }

    public Variable getVariable(String variableName) {
        Stack<Variable> variableIdStack = localVariables.get(variableName);
        if (variableIdStack != null) {
            return variableIdStack.peek();
        }
        return getMemberVariable(variableName);
    }

    public Variable registerMemberVariable(String name, boolean mutable) {
        Variable variable = new Variable(name);
        memberVariables.put(variable.name(), variable);
        this.isMutable.put(variable, mutable);
        return variable;
    }

    public Variable registerLocalVariable(String name, boolean mutable) {
        Variable variable = new Variable(name);
        localVariables.putIfAbsent(variable.name(), new Stack<>());
        localVariables.get(variable.name()).push(variable);
        localVariablesByScope.peek().add(variable.name());
        this.isMutable.put(variable, mutable);
        return variable;
    }

    public void addAllMemberVariablesAsReads(TokenSemantics semantics) {
        for (Variable mv : memberVariables.values()) {
            semantics.addRead(mv);
        }
    }

    public void clearMemberVariables() {
        memberVariables.clear();
    }

    public void registerVariableOperation(Variable variable, TokenSemantics semantics) {
        if (variable != null) {
            if (Set.of(NextOperation.READ, NextOperation.READ_WRITE).contains(nextOperation)) {
                semantics.addRead(variable);
            }
            if (Set.of(NextOperation.WRITE, NextOperation.READ_WRITE).contains(nextOperation) || (mutableWrite && isMutable.get(variable))) {
                semantics.addWrite(variable);
            }
        }
        nextOperation = NextOperation.READ;
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
