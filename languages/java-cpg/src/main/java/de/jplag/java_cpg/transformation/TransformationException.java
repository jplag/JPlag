package de.jplag.java_cpg.transformation;

import java.io.Serial;

/**
 * An {@link Exception} that relates to the Transformation process.
 */
public class TransformationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6642475708013551738L;

    /**
     * Creates a new {@link TransformationException}.
     * @param message the exception message
     */
    public TransformationException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link TransformationException} from the given Exception.
     * @param e the exception
     */
    public TransformationException(Exception e) {
        super(e);
    }
}
