package de.jplag.java.babylon.extractor;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link CodeModelExtractor} that caches resulting code models.<br>
 * Not thread safe.
 */
public final class CachingCodeModelExtractor implements CodeModelExtractor {
    private final CodeModelExtractor delegate;
    private final Map<MethodTree, Optional<CoreOp.FuncOp>> cache;

    /**
     * Create a new instance.
     * @param delegate the extractor from which the input code model should be sourced method should be skipped
     */
    public CachingCodeModelExtractor(CodeModelExtractor delegate) {
        this(delegate, new IdentityHashMap<>());
    }

    /**
     * Create a new instance.
     * @param delegate the extractor from which the input code model should be sourced method should be skipped
     * @param cache the preinitialized cache. Should be an {@link IdentityHashMap}.
     */
    public CachingCodeModelExtractor(CodeModelExtractor delegate, Map<MethodTree, Optional<CoreOp.FuncOp>> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public Optional<CoreOp.FuncOp> toOp(MethodTree methodTree, CompilationUnitTree ast) {
        Optional<CoreOp.FuncOp> result = cache.get(methodTree);
        if (result != null) {
            return result;
        }
        result = delegate.toOp(methodTree, ast);
        cache.put(methodTree, result);
        delegate.evictCache(methodTree, ast);
        return result;
    }

    @Override
    public void evictCache(MethodTree methodTree, CompilationUnitTree ast) {
        if (delegate != null)
            delegate.evictCache(methodTree, ast);
        cache.remove(methodTree);
    }
}
