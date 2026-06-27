package de.jplag.java.babylon.transformer.impl.util;

import de.jplag.java.babylon.transformer.SimpleTransformation;
import de.jplag.java.babylon.transformer.TransformationStep;

import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link TransformationStep} that delegates transformation to a separate {@link CodeTransformer} implementation.<br>
 * Intended to be overridden.
 */
public class DelegatePipelineStep implements TransformationStep<Void> {
    private final CodeTransformer delegate;
    private final String identifier;

    /**
     * Create a new instance.
     * @param delegate the transformer to use
     * @param identifier the identifier of this step
     */
    public DelegatePipelineStep(CodeTransformer delegate, String identifier) {
        this.delegate = delegate;
        this.identifier = identifier;
    }

    /**
     * Create a new instance, reusing the identifier of the delegate.
     * @param delegate the transformer to use
     */
    public DelegatePipelineStep(SimpleTransformation delegate) {
        this.delegate = delegate;
        this.identifier = delegate.getIdentifier();
    }

    @Override
    public final String getIdentifier() {
        return identifier;
    }

    @Override
    public final CoreOp.FuncOp apply(CoreOp.FuncOp op, Void unused) {
        return op.transform(delegate);
    }
}
