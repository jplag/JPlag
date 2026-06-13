package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.transformer.TransformationPipeline;

import com.google.auto.service.AutoService;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.SSA;

/**
 * {@link TransformationPipeline.Step} that performs a SSA transformation using SSACytron.
 */
@AutoService(TransformationPipeline.Step.class)
public class SsaCytronStep implements TransformationPipeline.Step {
    @Override
    public String getIdentifier() {
        return "ssa-cytron";
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op) {
        System.setProperty("babylon.ssa", "cytron"); // used internally. There is apparently no cleaner way to do this currently.
        return SSA.transform(op);
    }
}
