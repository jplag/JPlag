package de.jplag.java_cpg.ai;

/**
 * This exception is thrown when a Java language feature is used that is not supported by the analysis.
 * @author ujiqk
 * @version 1.0
 */
public class JavaLanguageFeatureNotSupportedException extends RuntimeException {

    /**
     * Constructs a new JavaLanguageFeatureNotSupportedException with the specified detail message.
     * @param message the detail message.
     */
    public JavaLanguageFeatureNotSupportedException(String message) {
        super(message);
    }

}
