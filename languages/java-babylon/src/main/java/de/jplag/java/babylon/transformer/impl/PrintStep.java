package de.jplag.java.babylon.transformer.impl;

import com.google.auto.service.AutoService;
import de.jplag.java.babylon.transformer.TransformationPipeline;
import jdk.incubator.code.dialect.core.CoreOp;

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
