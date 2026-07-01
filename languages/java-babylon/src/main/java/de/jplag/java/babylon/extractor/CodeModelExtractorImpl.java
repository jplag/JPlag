package de.jplag.java.babylon.extractor;

import java.util.Optional;

import javax.tools.JavaCompiler;

import com.sun.source.tree.MethodTree;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link CodeModelExtractor} implementation that simply creates a code model from input method or throws if that isn't
 * possible.
 */
public final class CodeModelExtractorImpl implements CodeModelExtractor {
    private final JavaCompiler.CompilationTask task;

    /**
     * Create a new instance.
     * @param task the current compilation task
     */
    public CodeModelExtractorImpl(JavaCompiler.CompilationTask task) {
        this.task = task;
    }

    @Override
    public Optional<CoreOp.FuncOp> toOp(MethodTree methodTree) {
        // if no Op can be constructed, that is an error that should not be construed with deliberate omission
        // of a method
        CoreOp.FuncOp op = Op.ofMethodTree(task, methodTree).orElseThrow(() -> new ExtractionFailedException(methodTree));
        return Optional.of(op);
    }
}
