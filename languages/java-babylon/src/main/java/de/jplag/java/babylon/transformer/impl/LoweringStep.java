package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.transformer.TransformationStep;

import com.google.auto.service.AutoService;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * A {@link TransformationStep} that applies {@link CodeTransformer#LOWERING_TRANSFORMER}.
 */
@AutoService(TransformationStep.class)
public class LoweringStep implements TransformationStep<Void> {
    /**
     * Identifier of this pipeline step.
     */
    public static final String IDENTIFIER = "lower";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Void context) {
        return op.transform(CodeTransformer.LOWERING_TRANSFORMER);
    }
}
