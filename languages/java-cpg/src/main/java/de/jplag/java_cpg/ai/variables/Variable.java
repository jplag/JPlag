package de.jplag.java_cpg.ai.variables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.NullValue;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.arrays.IJavaArray;
import de.jplag.java_cpg.ai.variables.values.string.IStringValue;

/**
 * A variable is a named value.
 * @author ujiqk
 * @version 1.0
 */
public class Variable {

    private final VariableName name;
    private IValue value;
    private List<ChangeRecorder> changeRecorders = new ArrayList<>();

    /**
     * A variable is a named value.
     * @param name the name of this variable.
     * @param value the value of this variable.
     */
    public Variable(@NotNull VariableName name, @NotNull IValue value) {
        this.name = name;
        this.value = value;
    }

    /**
     * A variable is a named value.
     * @param string the name of this variable.
     * @param value the value of this variable.
     */
    public Variable(@NotNull String string, @NotNull IValue value) {
        this.name = new VariableName(string);
        this.value = value;
    }

    /**
     * A variable is a named value.
     * @param name the name of this variable.
     * @param type the type of this variable; the initial value will be created based on the type.
     */
    public Variable(@NotNull VariableName name, @NotNull Type type) {
        this.name = name;
        this.value = Value.valueFactory(type);
    }

    /**
     * Copy constructor.
     * @param variable the variable to deep copy.
     */
    public Variable(@NotNull Variable variable) {
        this.name = variable.name;
        this.value = variable.value.copy();
        this.changeRecorders = variable.changeRecorders;    // no deep copy
    }

    /**
     * Copy constructor with copied objects map.
     * @param variable the variable to deep copy.
     * @param copiedObjects map of already copied JavaObjects to handle circular references.
     */
    public Variable(@NotNull Variable variable, Map<JavaObject, JavaObject> copiedObjects) {
        this.name = variable.name;
        this.value = variable.value.copy(copiedObjects);
        this.changeRecorders = variable.changeRecorders;
    }

    /**
     * @return The name of this variable in the program.
     */
    public VariableName getName() {
        return name;
    }

    /**
     * Triggers change recording.
     * @return The value of this variable.
     */
    public IValue getValue() {
        // trigger change recording because returned value could be modified outside.
        // ToDo: only trigger when actual changes happen
        for (ChangeRecorder changeRecorder : changeRecorders) {
            changeRecorder.recordChange(this);
        }
        return value;
    }

    /**
     * Set the value of this variable. Triggers change recording.
     * @param value the new value for this variable.
     */
    public void setValue(IValue value) {
        assert value != null;
        this.value = value;
        for (ChangeRecorder changeRecorder : changeRecorders) {
            changeRecorder.recordChange(this);
        }
    }

    /**
     * Merge content from another variable into this variable. The provided variable must have the same name as this
     * variable. The actual merge behavior is delegated to the underlying {@link Value} implementation.
     * @param other the variable whose content will be merged into this one; must have the same name.
     */
    public void merge(@NotNull Variable other) {
        merge(other, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    /**
     * Merge content from another variable into this variable. The provided variable must have the same name as this
     * variable. The actual merge behavior is delegated to the underlying {@link Value} implementation.
     * @param other the variable whose content will be merged into this one; must have the same name.
     * @param visited set of already visited JavaObjects to handle cyclic references.
     */
    public void merge(@NotNull Variable other, Set<JavaObject> visited) {
        assert this.changeRecorders.equals(other.changeRecorders);
        assert other.name.equals(this.name);
        if (this.value instanceof NullValue && !(other.value instanceof NullValue)) {
            if (other.value instanceof IStringValue) {
                this.value = Value.valueFactory(new Type(Type.TypeEnum.STRING));
            } else if (other.value instanceof IJavaArray) {
                this.value = Value.valueFactory(new Type(Type.TypeEnum.ARRAY));
            } else if (other.value instanceof IJavaObject) {
                this.value = Value.valueFactory(new Type(Type.TypeEnum.OBJECT));
            }
        }
        this.value.merge(other.value, visited);
    }

    /**
     * Delete all content information in this variable.
     */
    public void setToUnknown() {
        setToUnknown(Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    /**
     * Delete all content information in this variable.
     * @param visited set of already visited JavaObjects to handle cyclic references.
     */
    public void setToUnknown(Set<IJavaObject> visited) {
        if (getValue() instanceof NullValue) {
            setValue(Value.valueFactory(new Type(Type.TypeEnum.OBJECT)));
        } else {
            value.setToUnknown(visited);
        }
        for (ChangeRecorder changeRecorder : changeRecorders) {
            changeRecorder.recordChange(this);
        }
    }

    /**
     * Reset all information in this variable expect type and name. Initial values depend on the type.
     */
    public void setInitialValue() {
        setInitialValue(Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    /**
     * Reset all information in this variable expect type and name. Initial values depend on the type.
     * @param visited set of already visited JavaObjects to handle cyclic references.
     */
    public void setInitialValue(Set<IJavaObject> visited) {
        value.setInitialValue(visited);
        for (ChangeRecorder changeRecorder : changeRecorders) {
            changeRecorder.recordChange(this);
        }
    }

    /**
     * @param changeRecorder the change recorder to add. It will be notified on value changes.
     */
    public void addChangeRecorder(ChangeRecorder changeRecorder) {
        this.changeRecorders.add(changeRecorder);
    }

    /**
     * @return the last added change recorder. It is removed from this variable.
     */
    public ChangeRecorder removeLastChangeRecorder() {
        assert !this.changeRecorders.isEmpty();
        return this.changeRecorders.removeLast();
    }

}
