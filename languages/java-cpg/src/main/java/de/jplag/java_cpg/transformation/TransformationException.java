package de.jplag.java_cpg.transformation;

/**
 * An {@link Exception} that relates to the Transformation process.
 */
public class TransformationException extends RuntimeException {

    public TransformationException(String msg) {
        super(msg);
    }

    public TransformationException(Exception e) {
        super(e);
    }
}
