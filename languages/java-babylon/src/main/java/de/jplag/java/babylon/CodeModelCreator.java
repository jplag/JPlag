package de.jplag.java.babylon;

import java.util.Optional;

import javax.tools.JavaCompiler;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Encapsulates the logic for creating code models.<br>
 * Deliberately separated to simplify porting, as we expect release versions to not provide this exact API.
 */
public class CodeModelCreator {
    private final JavaCompiler.CompilationTask task;
    private CompilationUnitTree ast;

    /**
     * Create a new instance.
     * @param task the current compilation task
     * @param ast the active {@link CompilationUnitTree}. May be swapped out later via {@link #setAst}
     */
    public CodeModelCreator(JavaCompiler.CompilationTask task, CompilationUnitTree ast) {
        this.task = task;
        this.ast = ast;
    }

    /**
     * Swap out the active {@link CompilationUnitTree}.<br>
     * Required to support retaining a single object for a whole prepass, drastically simplifying that API.
     * @param ast the new AST
     */
    public void setAst(CompilationUnitTree ast) {
        this.ast = ast;
    }

    /**
     * Converts a method obtained from a {@link com.sun.source.tree.TreeVisitor} into a code model.
     * @param methodTree the method from the visitor
     * @return the code model, if it could be created
     */
    public Optional<CoreOp.FuncOp> toFunc(MethodTree methodTree) {
        return Op.ofMethodTree(task, ast, methodTree);
    }
}
