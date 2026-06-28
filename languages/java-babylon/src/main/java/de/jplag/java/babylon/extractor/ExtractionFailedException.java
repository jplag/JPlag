package de.jplag.java.babylon.extractor;

import com.sun.source.tree.MethodTree;

/**
 * {@link RuntimeException} that signals that a {@link jdk.incubator.code.dialect.core.CoreOp.FuncOp} could not be extracted.
 */
public class ExtractionFailedException extends RuntimeException {
    /**
     * The source of the attempted extraction.
     */
    public final MethodTree source;

    /**
     * Create a new instance.
     * @param source the source of the attempted extraction
     */
    public ExtractionFailedException(MethodTree source) {
        super("Can't resolve body of method: " + source.getName());
        this.source = source;
    }

    /**
     * Create a new instance.
     * @param source the exception that caused this
     */
    public ExtractionFailedException(ExtractionFailedException source) {
        super(source);
        this.source = source.source;
    }
}
