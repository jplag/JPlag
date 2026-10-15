package de.jplag.java.babylon.pipeline;

import java.util.List;

import de.jplag.java.babylon.extractor.CodeModelExtractor;
import de.jplag.java.babylon.transformer.TransformationStep;

import com.sun.source.tree.CompilationUnitTree;

/**
 * A lazy prepass executor that always applies each previous step when an extracted model is requested.
 */
public final class LazyPrepassExecutor implements PrepassExecutor {
    @Override
    public CodeModelExtractor prepass(List<TransformationStep<?>> steps, Iterable<? extends CompilationUnitTree> trees,
            TransformationStep.PrepassConstructionContext context) {
        CodeModelExtractor currentExtractor = context.extractor();
        for (TransformationStep<?> step : steps) {
            currentExtractor = prepass(step, trees, new TransformationStep.PrepassConstructionContext(currentExtractor, context.task()));
        }
        return currentExtractor;
    }
}
