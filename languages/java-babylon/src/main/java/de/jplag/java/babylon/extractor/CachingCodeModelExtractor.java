package de.jplag.java.babylon.extractor;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import com.sun.source.tree.MethodTree;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link CodeModelExtractor} that caches resulting code models.<br>
 * Not thread safe.
 */
public final class CachingCodeModelExtractor implements CodeModelExtractor {
    private final CodeModelExtractor delegate;
    private final Map<MethodTree, Optional<CoreOp.FuncOp>> cache;
    private final Map<MethodTree, ExtractionFailedException> extractionFailedCache;

    /**
     * Create a new instance.
     * @param delegate the extractor from which the input code model should be sourced method should be skipped
     */
    public CachingCodeModelExtractor(CodeModelExtractor delegate) {
        this(delegate, new IdentityHashMap<>(), new IdentityHashMap<>());
    }

    /**
     * Create a new instance.
     * @param delegate the extractor from which the input code model should be sourced method should be skipped
     * @param cache the preinitialized cache. Should be an {@link IdentityHashMap}.
     * @param extractionFailedCache the preinitialized cache of methods for which extraction failed. Should be a
     * {@link IdentityHashMap}.
     */
    public CachingCodeModelExtractor(CodeModelExtractor delegate, Map<MethodTree, Optional<CoreOp.FuncOp>> cache,
            Map<MethodTree, ExtractionFailedException> extractionFailedCache) {
        this.delegate = delegate;
        this.cache = cache;
        this.extractionFailedCache = extractionFailedCache;
    }

    @Override
    public Optional<CoreOp.FuncOp> toOp(MethodTree methodTree) {
        Optional<CoreOp.FuncOp> result = cache.get(methodTree);
        if (result != null) {
            return result;
        }
        ExtractionFailedException exception = extractionFailedCache.get(methodTree);
        if (exception != null) {
            throw new ExtractionFailedException(exception);
        }
        try {
            result = delegate.toOp(methodTree);
        } catch (ExtractionFailedException e) {
            extractionFailedCache.put(methodTree, e);
            throw e;
        }
        cache.put(methodTree, result);
        delegate.evictCache(methodTree);
        return result;
    }

    @Override
    public void evictCache(MethodTree methodTree) {
        if (delegate != null)
            delegate.evictCache(methodTree);
        cache.remove(methodTree);
    }
}
