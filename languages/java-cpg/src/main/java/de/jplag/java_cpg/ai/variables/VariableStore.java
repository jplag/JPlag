package de.jplag.java_cpg.ai.variables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;

/**
 * Stores variables in different scopes.
 * @author ujiqk
 * @version 1.0
 */
public class VariableStore {

    /**
     * The name used for the "this" object in anonymous classes.
     */
    public static final VariableName ANONYMOUS_THIS_NAME = new VariableName("$this");
    private final ArrayList<Scope> scopes = new ArrayList<>();
    private int currentScopeIndex = 0;
    private VariableName thisObject;
    private List<ChangeRecorder> changeRecorders = new ArrayList<>();

    /**
     * Copy constructor. Performs deep copy down the values.
     * @param variableStore the variable store to copy.
     */
    public VariableStore(@NotNull VariableStore variableStore) {
        this.currentScopeIndex = variableStore.currentScopeIndex;
        for (Scope p : variableStore.scopes) {
            this.scopes.add(new Scope(p));
        }
        thisObject = variableStore.thisObject;
        assert thisObject != null;
        this.changeRecorders = variableStore.changeRecorders; // no deep copy
    }

    /**
     * Default constructor. Initializes with one {@link Scope}.
     */
    public VariableStore() {
        scopes.add(new Scope());
    }

    /**
     * @param variable the variable to add to the current scope.
     */
    public void addVariable(@NotNull Variable variable) {
        scopes.get(currentScopeIndex).addVariable(variable);
    }

    /**
     * @param thisName the name of the {@link JavaObject} this variable store is associated with.
     */
    public void setThisName(@NotNull VariableName thisName) {
        this.thisObject = thisName;
    }

    /**
     * @return the {@link IJavaObject} this variable store is associated with, or null if not set.
     */
    @NotNull
    public IJavaObject getThisObject() {
        assert thisObject != null;
        Variable variable = getVariable(thisObject);
        if (variable == null) {     // does not exist yet -> create it
            assert thisObject == ANONYMOUS_THIS_NAME;
            Variable thisClassVar = new Variable(thisObject, new JavaObject(new Type(Type.TypeEnum.OBJECT)));
            for (ChangeRecorder recorder : changeRecorders) {
                thisClassVar.addChangeRecorder(recorder);
            }
            scopes.getFirst().addVariable(thisClassVar);
            variable = getVariable(thisObject);
            assert variable != null;
        }
        IValue value = variable.getValue();
        assert value instanceof IJavaObject;
        return (JavaObject) value;
    }

    /**
     * @param name the name of the variable to retrieve.
     * @return the variable with the given name or null if it does not exist.
     */
    @Nullable
    public Variable getVariable(@NotNull VariableName name) {
        for (int i = currentScopeIndex; i >= 0; i--) {
            Variable variable = scopes.get(i).getVariable(name);
            if (variable != null) {
                return variable;
            }
        }
        return null;
    }

    /**
     * @param name the name of the variable to retrieve.
     * @return the variable with the given name or null if it does not exist.
     */
    @Nullable
    public Variable getVariable(String name) {
        return getVariable(new VariableName(name));
    }

    /**
     * Creates a new scope on top of the current one.
     */
    public void newScope() {
        scopes.add(new Scope());
        currentScopeIndex++;
    }

    /**
     * Removes the current scope.
     */
    public void removeScope() {
        if (currentScopeIndex > 0) {
            scopes.remove(currentScopeIndex);
            currentScopeIndex--;
        }
        assert currentScopeIndex >= 0;
    }

    /**
     * Merges the information from another variable store into this one. Used for merging variable states after control-flow
     * joins. Merges scopes up to the minimum scope depth of both stores.
     * @param other the other variable store to merge.
     */
    public void merge(@NotNull VariableStore other) {
        // In complex control-flow, the scope depth can differ.
        int targetIndex = Math.min(this.currentScopeIndex, other.currentScopeIndex);
        if (this.currentScopeIndex > targetIndex) {
            // remove scopes from the end until we match the target index
            for (int i = this.currentScopeIndex; i > targetIndex; i--) {
                // scopes are always appended at the end; safe to remove by index
                scopes.remove(i);
            }
            this.currentScopeIndex = targetIndex;
        }
        for (int i = 0; i <= targetIndex; i++) {
            Scope thisScope = this.scopes.get(i);
            Scope otherScope = other.scopes.get(i);
            thisScope.merge(otherScope);
        }
    }

    /**
     * Sets all variables in all scopes to completely unknown.
     */
    public void setEverythingUnknown() {
        for (Scope scope : scopes) {
            scope.setEverythingUnknown();
        }
    }

    /**
     * Starts recording changes to variables in all scopes.
     */
    public void recordChanges() {
        ChangeRecorder changeRecorder = new ChangeRecorder();
        for (Scope scope : scopes) {
            scope.addChangeRecorder(changeRecorder);
        }
        this.changeRecorders.add(changeRecorder);
    }

    /**
     * Stops recording changes to variables in all scopes and returns the set of changed variables. Method assumes that
     * recordChanges() was called before.
     * @return the set of changed variables.
     */
    @NotNull
    public Set<Variable> stopRecordingChanges() {
        this.changeRecorders.removeLast();
        List<ChangeRecorder> recorders = new ArrayList<>();
        for (Scope scope : scopes) {
            recorders.add(scope.removeLastChangeRecorder());
        }
        ChangeRecorder first = recorders.stream().filter(Objects::nonNull).findFirst().orElse(null);
        assert recorders.stream().allMatch(r -> (r == first) || (r == null));
        if (first == null) {
            return new HashSet<>();
        }
        return first.getChangedVariables();
    }

}
