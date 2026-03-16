package de.jplag.java_cpg.ai.variables.values.arrays;

import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;

/**
 * Also needs to extend JavaObject because Java arrays are objects.
 * @author ujiqk
 */
public interface IJavaArray extends IJavaObject {

    /**
     * Access an array element at the given index.
     * @param index The index to access.
     * @return The value at the given index.
     */
    IValue arrayAccess(INumberValue index);

    /**
     * Assign a value to an array element at the given index.
     * @param index The index to assign to.
     * @param value The value to assign.
     */
    void arrayAssign(INumberValue index, IValue value);

}
