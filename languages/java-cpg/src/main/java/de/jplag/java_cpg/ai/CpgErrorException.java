package de.jplag.java_cpg.ai;

/**
 * This exception is thrown when we encounter a malformed CPG Graph.
 * @author ujiqk
 * @version 1.0
 */
public class CpgErrorException extends RuntimeException {

    /**
     * Constructs a new CpgErrorException with the specified detail message.
     * @param message the detail message.
     */
    public CpgErrorException(String message) {
        super(message);
    }

}
