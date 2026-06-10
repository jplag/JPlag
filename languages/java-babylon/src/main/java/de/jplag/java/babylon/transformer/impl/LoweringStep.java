package de.jplag.java.babylon.transformer.impl;

import com.google.auto.service.AutoService;
import de.jplag.java.babylon.transformer.TransformationPipeline;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * A {@link TransformationPipeline.Step} that applies {@link CodeTransformer#LOWERING_TRANSFORMER}.
 */
@AutoService(TransformationPipeline.Step.class)
public class LoweringStep implements TransformationPipeline.Step {
    @Override
    public String getIdentifier() {
        return "lower";
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op) {
        return op.transform(CodeTransformer.LOWERING_TRANSFORMER);
    }
}
