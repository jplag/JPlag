package de.jplag.java.babylon.extractor;

import java.util.Optional;

import com.sun.source.tree.MethodTree;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Encapsulates the logic for creating code models.<br>
 * Subclasses may also apply transformations before returning the model.<br>
 * Deliberately separated to simplify porting, as we expect release versions to not provide this exact API.
 */
public interface CodeModelExtractor {
    /**
     * Converts a method obtained from a {@link com.sun.source.tree.TreeVisitor} into a code model.
     * @param methodTree the method from the visitor
     * @return the code model, if it could be created
     * @throws ExtractionFailedException if an unrecoverable error occurs during extraction
     */
    Optional<CoreOp.FuncOp> toOp(MethodTree methodTree) throws ExtractionFailedException;

    /**
     * Notifies this extractor that internal caches for a method tree can be evicted since it has been cached at a later
     * stage.
     * @param methodTree the method tree for which to evict caches
     */
    default void evictCache(MethodTree methodTree) {
    }
}
