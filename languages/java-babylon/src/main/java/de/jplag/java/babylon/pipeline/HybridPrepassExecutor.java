package de.jplag.java.babylon.pipeline;

import java.util.List;

import de.jplag.java.babylon.extractor.CachingCodeModelExtractor;
import de.jplag.java.babylon.extractor.CodeModelExtractor;
import de.jplag.java.babylon.transformer.TransformationStep;

import com.sun.source.tree.CompilationUnitTree;

/**
 * A hybrid prepass executor that is partially lazy but caches at a fixed interval.
 */
public final class HybridPrepassExecutor implements PrepassExecutor {
    private static final int cacheInterval = Integer.parseInt(System.getProperty("jplag.java-babylon.pipeline.hybrid.cache-interval", "10"));

    @Override
    public CodeModelExtractor prepass(List<TransformationStep<?>> steps, Iterable<? extends CompilationUnitTree> trees,
            CodeModelExtractor extractor) {
        CodeModelExtractor currentExtractor = extractor;
        int index = 0;
        for (TransformationStep<?> step : steps) {
            currentExtractor = prepass(step, trees, currentExtractor);
            if (cacheInterval > 0) {
                index = (index + 1) % cacheInterval;
                if (index == 0) {
                    currentExtractor = new CachingCodeModelExtractor(currentExtractor);
                }
            }
        }
        return currentExtractor;
    }
}
