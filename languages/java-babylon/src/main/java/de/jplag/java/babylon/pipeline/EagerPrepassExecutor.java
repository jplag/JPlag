package de.jplag.java.babylon.pipeline;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.jplag.java.babylon.extractor.CachingCodeModelExtractor;
import de.jplag.java.babylon.extractor.CodeModelExtractor;
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

        for (TransformationStep<?> step : steps) {
            CodeModelExtractor extractor1 = prepass(step, trees, new CachingCodeModelExtractor(null, caches.first));
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
