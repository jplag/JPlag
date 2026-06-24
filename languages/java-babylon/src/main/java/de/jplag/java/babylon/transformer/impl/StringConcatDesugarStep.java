package de.jplag.java.babylon.transformer.impl;

import de.jplag.java.babylon.transformer.TransformationPipeline;
import de.jplag.java.babylon.transformer.impl.util.DelegatePipelineStep;

import com.google.auto.service.AutoService;
import jdk.incubator.code.dialect.java.StringConcatTransformer;

/**
 * {@link TransformationPipeline.Step} that desugars string concatenation to {@link StringBuilder} invocations.
 */
@AutoService(TransformationPipeline.Step.class)
public class StringConcatDesugarStep extends DelegatePipelineStep {
    /**
     * Identifier of this pipeline step.
     */
    public static final String IDENTIFIER = "string-concat-desugar";

    /**
     * Create a new instance.
     */
    public StringConcatDesugarStep() {
        super(new StringConcatTransformer(), IDENTIFIER);
    }
}
