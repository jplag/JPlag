package de.jplag.java_cpg.ai.variables.values.string;

import org.jetbrains.annotations.Nullable;

import de.jplag.java_cpg.ai.variables.values.IJavaObject;

/**
 * Strings are objects with added functionality.
 * @author ujiqk
 * @version 1.0
 */
public interface IStringValue extends IJavaObject {

    /**
     * @return true if the string value has definite information (i.e., a known value), false otherwise.
     */
    boolean getInformation();

    /**
     * @return if known, the string value.
     */
    @Nullable
    String getValue();

}
