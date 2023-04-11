package de.jplag.semantics;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Registry of variables to assist in generating token semantics.
 */
public class VariableRegistry {
    // private CodeSemantics semantics;
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

    /**
     * @return If we are currently in a local scope.
     */
    public boolean inLocalScope() {
        return !localVariablesByScope.isEmpty();
    }

    /**
     * Set the type of the next variable access. This only influences the very next call of registerVariableOperation.
     * @param nextVariableAccessType The type of the next variable access.
     */
    public void setNextVariableAccessType(VariableAccessType nextVariableAccessType) {
        this.nextVariableAccessType = nextVariableAccessType;
    }

    /**
     * Set whether the next variable access is ignored. This only influences the very next call of
     * registerVariableOperation.
     * @param ignoreNextVariableAccess Whether the next variable access is ignored.
     */
    public void setIgnoreNextVariableAccess(boolean ignoreNextVariableAccess) {
        this.ignoreNextVariableAccess = ignoreNextVariableAccess;
    }

    /**
     * Set whether accesses to mutable variables are writes from this point on.
     * @param mutableWrite Whether accesses to mutable variables are writes from this point on.
     */
    public void setMutableWrite(boolean mutableWrite) {
        this.mutableWrite = mutableWrite;
    }

    /**
     * Enter a class.
     */
    public void enterClass() {
        classVariables.push(new HashMap<>());
    }

    /**
     * Exit a class. This causes all variables bound to the current class to no longer be visible.
     */
    public void exitClass() {
        classVariables.pop();
    }

    /**
     * Enter a local scope.
     */
    public void enterLocalScope() {
        localVariablesByScope.push(new HashSet<>());
    }

    /**
     * Exit a local scope. This causes all variables bound to the current local scope to no longer be visible.
     */
    public void exitLocalScope() {
        for (String variableName : localVariablesByScope.pop()) {
            Deque<Variable> variableStack = localVariables.get(variableName);
            variableStack.pop();
            if (variableStack.isEmpty())
                localVariables.remove(variableName);
        }
    }

    /**
     * Register a variable.
     * @param variableName The variable's name.
     * @param scope The variable's scope.
     * @param mutable Whether the variable is mutable.
     */
    public void registerVariable(String variableName, VariableScope scope, boolean mutable) {
        Variable variable = new Variable(variableName, scope, mutable);
        switch (scope) {
            case FILE -> fileVariables.put(variableName, variable);
            case CLASS -> classVariables.getFirst().put(variableName, variable);
            case LOCAL -> {
                localVariables.putIfAbsent(variableName, new LinkedList<>());
                localVariables.get(variableName).push(variable);
                localVariablesByScope.getFirst().add(variableName);
            }
        }
    }

    /**
     * Register a variable access, more precisely: Add a variable access to a CodeSemantics instance. The type of the access
     * can be set with setNextVariableAccessType. By default, its type is read.
     * @param variableName The variable's name.
     * @param isClassVariable Whether the variable is a class variable. This is true if a variable is qualified with the
     * "this" keyword in Java, for example.
     * @param semantics The CodeSemantics instance the variable access is added to.
     */
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

    /**
     * Add all non-local visible variables as reads to the CodeSemantics instance.
     * @param semantics The CodeSemantics instance.
     */
    public void addAllNonLocalVariablesAsReads(CodeSemantics semantics) {
        Set<Variable> nonLocalVariables = new HashSet<>(fileVariables.values());
        nonLocalVariables.addAll(classVariables.getFirst().values());
        for (Variable variable : nonLocalVariables)
            semantics.addRead(variable);
    }

    private Variable getVariable(String variableName) {
        Deque<Variable> variableIdStack = localVariables.get(variableName);
        if (variableIdStack != null)
            return variableIdStack.getFirst();  // stack is never empty
        Variable variable = getClassVariable(variableName);
        return variable != null ? variable : fileVariables.get(variableName);
        /*
         * todo track global variables -> hard, how to differentiate SomeClass.staticAttr++ from String.join(...) // problem
         * here: all String.joins (for example) are registered as writes to String // get global variable, register if it
         * doesn't exist variable = globalVariables.get(variableName); if (variable != null) return variable; variable = new
         * Variable(variableName, false, true); globalVariables.put(variableName, variable); return variable;
         */
    }

    private Variable getClassVariable(String variableName) {
        Map<String, Variable> currentClassVariables = classVariables.peek();
        return currentClassVariables != null ? currentClassVariables.get(variableName) : null;
    }
}
