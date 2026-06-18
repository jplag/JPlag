package de.jplag.java.babylon.extractor;

import java.util.Optional;
import java.util.function.UnaryOperator;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link CodeModelExtractor} that applies some transformation to the code model before returning it.
 */
public final class TransformingCodeModelExtractor implements CodeModelExtractor {
    private final CodeModelExtractor delegate;
    private final UnaryOperator<CoreOp.FuncOp> transformation;

    /**
     * Create a new instance.
     * @param delegate the extractor from which the input code model should be sourced
     * @param transformation the transformation to apply before returning the code model may return null to signal that the
     * method should be skipped
     */
    public TransformingCodeModelExtractor(CodeModelExtractor delegate, UnaryOperator<CoreOp.FuncOp> transformation) {
        this.delegate = delegate;
        this.transformation = transformation;
    }

    @Override
    public Optional<CoreOp.FuncOp> toOp(MethodTree methodTree, CompilationUnitTree ast) {
        return delegate.toOp(methodTree, ast).flatMap(op -> Optional.ofNullable(transformation.apply(op)));
    }
}
