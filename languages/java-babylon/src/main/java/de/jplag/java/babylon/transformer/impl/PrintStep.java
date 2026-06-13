package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.transformer.TransformationPipeline;

import com.google.auto.service.AutoService;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link TransformationPipeline.Step} that prints the code model.
 */
@AutoService(TransformationPipeline.Step.class)
public class PrintStep implements TransformationPipeline.Step {
    @Override
    public String getIdentifier() {
        return "print";
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op) {
        IO.println(op.toText());
        return op;
    }
}
