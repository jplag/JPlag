package de.jplag.java.babylon.extractor;

import java.util.Optional;

import com.sun.source.tree.CompilationUnitTree;
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
     * @param ast the compilation unit in which the method is contained
     * @return the code model, if it could be created
     */
    Optional<CoreOp.FuncOp> toOp(MethodTree methodTree, CompilationUnitTree ast);
}
