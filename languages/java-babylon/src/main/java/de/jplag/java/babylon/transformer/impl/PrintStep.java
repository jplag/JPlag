package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.transformer.TransformationStep;

import com.google.auto.service.AutoService;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link TransformationStep} that prints the code model.
 */
@AutoService(TransformationStep.class)
public class PrintStep implements TransformationStep<Void> {
    /**
     * Identifier of this pipeline step.
     */
    public static final String IDENTIFIER = "print";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Void context) {
        IO.println(op.toText());
        return op;
    }
}
