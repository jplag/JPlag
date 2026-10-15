package de.jplag.java.babylon.transformer.impl;

import java.util.Set;

import de.jplag.java.babylon.transformer.TransformationStep;

import com.google.auto.service.AutoService;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.SSA;

/**
 * {@link TransformationStep} that performs a SSA transformation.<br>
 * Set the system property {@code babylon.ssa} to {@code braun} or {@code cytron} to pick an implementation.
 */
@AutoService(TransformationStep.class)
public class SsaStep implements TransformationStep<Void> {
    /**
     * Identifier of this pipeline step.
     */
    public static final String IDENTIFIER = "ssa";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<String> getDependencies() {
        return Set.of(LoweringStep.IDENTIFIER);
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Void context) {
        return SSA.transform(op);
    }
}
