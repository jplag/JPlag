package de.jplag.java_cpg.transformation;

/**
 * An {@link Exception} that relates to the Transformation process.
 */
public class TransformationException extends RuntimeException {
    /**
     * Creates a new {@link TransformationException} with the given message.
     * @param msg the message
     */
    public TransformationException(String msg) {
        super(msg);
    }

    /**
     * Creates a new {@link TransformationException} with the given cause.
     * @param e the cause
     */
    public TransformationException(Exception e) {
        super(e);
    }
}
