package de.jplag.java.babylon.transformer.impl;

import java.lang.invoke.MethodHandles;

import de.jplag.java.babylon.transformer.TransformationPipeline;

import com.google.auto.service.AutoService;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.ConstantExpressionTransformer;

/**
 * {@link TransformationPipeline.Step} that precomputes constant values.
 */
@AutoService(TransformationPipeline.Step.class)
public class ConstantPropagationStep implements TransformationPipeline.Step<Void> {
    /**
     * Identifier of this pipeline step.
     */
    public static final String IDENTIFIER = "constant-propagation";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Void unused) {
        return (CoreOp.FuncOp) ConstantExpressionTransformer.transform(MethodHandles.publicLookup(), op);
    }
}
