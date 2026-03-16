package de.jplag.java_cpg.ai.variables.values;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.jplag.java_cpg.ai.variables.Type;
import de.jplag.java_cpg.ai.variables.values.arrays.IJavaArray;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;

import kotlin.Pair;

/**
 * Interface for all values.
 * @author ujiqk
 * @version 1.0
 */
public interface IValue {

    /**
     * @return the type of this value.
     */
    @NotNull
    Type getType();

    /**
     * Performs a binary operation between this value and another value.
     * @param operator the operator.
     * @param other the other value.
     * @return the result value. VoidValue if the operation does not return a value.
     * @throws UnsupportedOperationException if the operation is not supported between the two value types.
     */
    IValue binaryOperation(@NotNull String operator, @NotNull IValue other);

    /**
     * Performs a unary operation on this value.
     * @param operator the operator.
     * @return the result value. VoidValue if the operation does not return a value.
     * @throws IllegalArgumentException if the operation is not supported for this value type.
     */
    IValue unaryOperation(@NotNull String operator);

    /**
     * Creates and returns a deep copy of this value.
     * @return a deep copy of this value.
     */
    @NotNull
    IValue copy();

    /**
     * Creates and returns a deep copy of this value.
     * @param copiedObjects map of already copied objects to handle cyclic references.
     * @return a deep copy of this value.
     */
    default IValue copy(Map<JavaObject, JavaObject> copiedObjects) {
        return copy(); // default: ignore the map
    }

    /**
     * Merges the information of another instance of the same value into this one. Types should be the same. For example,
     * when a value has different content in different branches of an if statement.
     * @param other other value.
     */
    void merge(@NotNull IValue other);

    /**
     * Merges the information of another instance of the same value into this one. Types should be the same. For example,
     * when a value has different content in different branches of an if statement.
     * @param other other value.
     * @param visited set of already visited JavaObjects to handle cyclic references.
     */
    default void merge(@NotNull IValue other, Set<JavaObject> visited) {
        merge(other); // default: ignore visited set
    }

    /**
     * Delete all information in this value.
     */
    void setToUnknown();

    /**
     * Delete all information in this value.
     * @param visited set of already visited JavaObjects to handle cyclic references.
     */
    default void setToUnknown(Set<IJavaObject> visited) {
        setToUnknown(); // default: ignore visited set
    }

    /**
     * Resets all information about this value except its type. The initial value depends on the specific value type.
     */
    void setInitialValue();

    /**
     * Resets all information about this value except its type. The initial value depends on the specific value type.
     * @param visited set of already visited JavaObjects to handle cyclic references.
     */
    default void setInitialValue(Set<IJavaObject> visited) {
        setInitialValue(); // default: ignore visited set
    }

    /**
     * {@link #setParentObject(IJavaObject)} must be called before to use this method.
     * @return the parent object of this value. Can be null.
     */
    IJavaObject getParentObject();

    /**
     * Sets the parent object of this value. Must be called before some filed accesses.
     * @param parentObject the parent object. Can be null.
     */
    void setParentObject(@Nullable IJavaObject parentObject);

    /**
     * {@link #setArrayPosition(IJavaArray, INumberValue)} must be called before to use this method.
     * @return the position of this value in the array that contains it.
     */
    Pair<IJavaArray, INumberValue> getArrayPosition();

    /**
     * Sets the position of this value in the array that contains it. Necessary to set before array assignments.
     * @param array the array that contains this value.
     * @param index the index of this value in the array.
     */
    void setArrayPosition(IJavaArray array, INumberValue index);

}
