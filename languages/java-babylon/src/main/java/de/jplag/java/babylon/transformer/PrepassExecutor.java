package de.jplag.java.babylon.transformer;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.jplag.java.babylon.extractor.CachingCodeModelExtractor;
import de.jplag.java.babylon.extractor.CodeModelExtractor;
import de.jplag.java.babylon.extractor.TransformingCodeModelExtractor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Strategy for executing a transformation pipelines prepasses.<br>
 * Pluggable to allow the performance of approaches to be evaluated.
 */
public sealed interface PrepassExecutor {
    /**
     * Performs a prepass to obtain the context required for tokenization.
     * @param steps the steps comprising the pipeline that is to be executed
     * @param trees the input trees on which the prepass should be run
     * @param extractor the extractor to use for obtaining code models
     * @return the prepass visitor
     */
    CodeModelExtractor prepass(List<TransformationPipeline.Step<?>> steps, Iterable<? extends CompilationUnitTree> trees,
            CodeModelExtractor extractor);

    /**
     * Resolves the default pipeline executor of the current process.
     * @return the pipeline executor
     * @throws IllegalArgumentException if the pipeline executor property is not well-formed
     */
    static PrepassExecutor getDefault() {
        return switch (System.getProperty("jplag.java-babylon.pipeline.executor", "eager")) {
            case "eager" -> new Eager();
            case "lazy" -> new Lazy();
            case "hybrid" -> new Hybrid();
            default -> throw new IllegalArgumentException("Selected pipeline executor does not exist. Pick one of [eager, lazy, hybrid]");
        };
    }

    private static <T> CodeModelExtractor prepass(Iterable<? extends CompilationUnitTree> trees, TransformationPipeline.Step<T> step,
            CodeModelExtractor currentExtractor) {
        Prepass<T> prepass = step.beginPrepass(new TransformationPipeline.PrepassConstructionContext(currentExtractor));
        T context;
        if (prepass != null) {
            for (CompilationUnitTree tree : trees) {
                tree.accept(prepass, tree);
            }
            context = prepass.finalizeContext();
        } else {
            context = null;
        }
        return new TransformingCodeModelExtractor(currentExtractor, op -> step.apply(op, context));
    }

    /**
     * A lazy prepass executor that always applies each previous step when an extracted model is requested.
     */
    final class Lazy implements PrepassExecutor {
        @Override
        public CodeModelExtractor prepass(List<TransformationPipeline.Step<?>> steps, Iterable<? extends CompilationUnitTree> trees,
                CodeModelExtractor extractor) {
            CodeModelExtractor currentExtractor = extractor;
            for (TransformationPipeline.Step<?> step : steps) {
                currentExtractor = PrepassExecutor.prepass(trees, step, currentExtractor);
            }
            return currentExtractor;
        }
    }

    /**
     * An eager prepass executor that fully applies a step before moving on to the next prepass.
     */
    final class Eager implements PrepassExecutor {
        @Override
        public CodeModelExtractor prepass(List<TransformationPipeline.Step<?>> steps, Iterable<? extends CompilationUnitTree> trees,
                CodeModelExtractor extractor) {
            SwappablePair<Map<MethodTree, Optional<CoreOp.FuncOp>>> caches = new SwappablePair<>(new IdentityHashMap<>(), new IdentityHashMap<>());
            Map<MethodTree, CompilationUnitTree> parents = new IdentityHashMap<>();
            for (CompilationUnitTree tree : trees) {
                tree.accept(new TreeScanner<>() {
                    @Override
                    public Object visitMethod(MethodTree node, Object o) {
                        var op = extractor.toOp(node, tree);
                        caches.first.put(node, op);
                        caches.second.put(node, op);
                        parents.put(node, tree);
                        return super.visitMethod(node, o);
                    }
                }, null);
            }

            for (TransformationPipeline.Step<?> step : steps) {
                CodeModelExtractor extractor1 = PrepassExecutor.prepass(trees, step, new CachingCodeModelExtractor(null, caches.first));
                caches.second.replaceAll((key, _) -> extractor1.toOp(key, parents.get(key)));
                caches.swap();
            }
            return new CachingCodeModelExtractor(null, caches.first);
        }

        private static class SwappablePair<T> {
            private T first;
            private T second;

            public SwappablePair(T first, T second) {
                this.first = first;
                this.second = second;
            }

            void swap() {
                T tmp = first;
                first = second;
                second = tmp;
            }
        }
    }

    /**
     * A hybrid prepass executor that is partially lazy but caches at a fixed interval.
     */
    final class Hybrid implements PrepassExecutor {
        private static final int cacheInterval = Integer.parseInt(System.getProperty("jplag.java-babylon.pipeline.hybrid.cache-interval", "10"));

        @Override
        public CodeModelExtractor prepass(List<TransformationPipeline.Step<?>> steps, Iterable<? extends CompilationUnitTree> trees,
                CodeModelExtractor extractor) {
            CodeModelExtractor currentExtractor = extractor;
            int index = 0;
            for (TransformationPipeline.Step<?> step : steps) {
                currentExtractor = PrepassExecutor.prepass(trees, step, currentExtractor);
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
}
