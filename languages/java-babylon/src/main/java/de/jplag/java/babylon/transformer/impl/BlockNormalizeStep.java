package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.transformer.TransformationStep;

import com.google.auto.service.AutoService;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.NormalizeBlocksTransformer;

/**
 * {@link TransformationStep} that merges redundant blocks and removes unused block parameters.
 */
@AutoService(TransformationStep.class)
public class BlockNormalizeStep implements TransformationStep<Void> {
    /**
     * Identifier of this pipeline step.
     */
    public static final String IDENTIFIER = "block-normalize";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Void unused) {
        return NormalizeBlocksTransformer.transform(op);
    }
}
