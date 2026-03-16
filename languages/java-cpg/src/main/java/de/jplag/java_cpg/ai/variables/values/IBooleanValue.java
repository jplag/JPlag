package de.jplag.java_cpg.ai.variables.values;

/**
 * Boolean value representations.
 * @author ujiqk
 * @version 1.0
 */
public interface IBooleanValue {

    /**
     * @return if the boolean value is known.
     */
    boolean getInformation();

    /**
     * @return the boolean value if it is known.
     */
    boolean getValue();

}
