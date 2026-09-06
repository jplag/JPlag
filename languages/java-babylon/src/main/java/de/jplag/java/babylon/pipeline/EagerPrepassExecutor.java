package de.jplag.java.babylon.pipeline;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.jplag.java.babylon.extractor.CachingCodeModelExtractor;
import de.jplag.java.babylon.extractor.CodeModelExtractor;
import de.jplag.java.babylon.extractor.ExtractionFailedException;
import de.jplag.java.babylon.transformer.TransformationStep;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * An eager prepass executor that fully applies a step before moving on to the next prepass.
 */
public final class EagerPrepassExecutor implements PrepassExecutor {
    @Override
    public CodeModelExtractor prepass(List<TransformationStep<?>> steps, Iterable<? extends CompilationUnitTree> trees,
            TransformationStep.PrepassConstructionContext context) {
        SwappablePair<Cache> caches = new SwappablePair<>(new Cache(), new Cache());
        Map<MethodTree, CompilationUnitTree> parents = new IdentityHashMap<>();
        for (CompilationUnitTree tree : trees) {
            tree.accept(new TreeScanner<>() {
                @Override
                public Object visitMethod(MethodTree node, Object o) {
                    Optional<CoreOp.FuncOp> op;
                    try {
                        op = context.extractor().toOp(node);
                    } catch (ExtractionFailedException e) {
                        caches.first.failed.put(node, e);
                        caches.second.failed.put(node, e);
                        return super.visitMethod(node, o);
                    }
                    caches.first.success.put(node, op);
                    caches.second.success.put(node, op);
                    parents.put(node, tree);
                    return super.visitMethod(node, o);
                }
            }, null);
        }

        for (TransformationStep<?> step : steps) {
            CodeModelExtractor previousExtractor = new CachingCodeModelExtractor(null, caches.first.success, caches.first.failed);
            CodeModelExtractor extractor1 = prepass(step, trees,
                    new TransformationStep.PrepassConstructionContext(previousExtractor, context.task()));
            for (MethodTree key : caches.first.success.keySet()) {
                try {
                    caches.second.success.put(key, extractor1.toOp(key));
                } catch (ExtractionFailedException e) {
                    caches.second.success.remove(key);
                    caches.second.failed.put(key, e);
                }
            }
            caches.swap();
        }
        return new CachingCodeModelExtractor(null, caches.first.success, caches.first.failed);
    }

    private record Cache(Map<MethodTree, Optional<CoreOp.FuncOp>> success, Map<MethodTree, ExtractionFailedException> failed) {
        public Cache() {
            this(new IdentityHashMap<>(), new IdentityHashMap<>());
        }
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
