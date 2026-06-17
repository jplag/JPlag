package de.jplag.java.babylon.transformer.impl;

import java.util.Set;

import de.jplag.java.babylon.transformer.TransformationPipeline;

import com.google.auto.service.AutoService;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.SSA;

/**
 * {@link TransformationPipeline.Step} that performs a SSA transformation using SSABraun.
 */
@AutoService(TransformationPipeline.Step.class)
public class SsaStep implements TransformationPipeline.Step<Void> {
    @Override
    public String getIdentifier() {
        return "ssa";
    }

    @Override
    public Set<String> getDependencies() {
        return Set.of(LoweringStep.IDENTIFIER);
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Void context) {
        System.setProperty("babylon.ssa", "braun"); // used internally. There is apparently no cleaner way to do this currently.
        return SSA.transform(op);
    }
}
