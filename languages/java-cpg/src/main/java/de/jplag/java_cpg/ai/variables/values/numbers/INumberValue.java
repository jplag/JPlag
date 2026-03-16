package de.jplag.java_cpg.ai.variables.values.numbers;

import de.jplag.java_cpg.ai.variables.values.IValue;

/**
 * Interface for number values.
 * @author ujiqk
 * @version 1.0
 */
public interface INumberValue extends IValue {

    /**
     * @return if exact information is available.
     */
    boolean getInformation();

    /**
     * @return the exact value. Only valid if getInformation() returns true.
     */
    double getValue();

}
