package de.jplag.java;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DefaultCaseLabelTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExportsTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModuleTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.ProvidesTree;
import com.sun.source.tree.RequiresTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchExpressionTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.YieldTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;

final class TokenGeneratingTreeScanner extends TreeScanner<Object, Object> {
    private final String filename;
    private final Parser parser;
    private final LineMap map;
    private final SourcePositions positions;
    private final CompilationUnitTree ast;

    public TokenGeneratingTreeScanner(String filename, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast) {
        this.filename = filename;
        this.parser = parser;
        this.map = map;
        this.positions = positions;
        this.ast = ast;
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type from {@link JavaTokenConstants}.
     * @param position is the start position of the token.
     * @param length is the length of the token.
     */
    private void addToken(int tokenType, long position, int length) {
        parser.add(tokenType, filename, map.getLineNumber(position), map.getColumnNumber(position), length);
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type from {@link JavaTokenConstants}.
     * @param start is the start position of the token.
     * @param end is the end position of the token for the calculation of the length.
     */
    private void addToken(int tokenType, long start, long end) {
        parser.add(tokenType, filename, map.getLineNumber(start), map.getColumnNumber(start), (end - start));
    }

    @Override
    public Object visitBlock(BlockTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_INIT_BEGIN, start, 1);
        Object result = super.visitBlock(node, p);
        addToken(JavaTokenConstants.J_INIT_END, end, 1);
        return result;
    }

    @Override
    public Object visitClass(ClassTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;

        if (node.getKind() == Tree.Kind.ENUM) {
            addToken(JavaTokenConstants.J_ENUM_BEGIN, start, 4);
        } else if (node.getKind() == Tree.Kind.INTERFACE) {
            addToken(JavaTokenConstants.J_INTERFACE_BEGIN, start, 9);
        } else if (node.getKind() == Tree.Kind.RECORD) {
            addToken(JavaTokenConstants.J_RECORD_BEGIN, start, 1);
        } else if (node.getKind() == Tree.Kind.ANNOTATION_TYPE) {
            addToken(JavaTokenConstants.J_ANNO_T_BEGIN, start, 10);
        } else if (node.getKind() == Tree.Kind.CLASS) {
            addToken(JavaTokenConstants.J_CLASS_BEGIN, start, 5);
        }
        Object result = super.visitClass(node, p);
        if (node.getKind() == Tree.Kind.ENUM) {
            addToken(JavaTokenConstants.J_ENUM_END, end, 1);
        } else if (node.getKind() == Tree.Kind.INTERFACE) {
            addToken(JavaTokenConstants.J_INTERFACE_END, end, 1);
        } else if (node.getKind() == Tree.Kind.RECORD) {
            addToken(JavaTokenConstants.J_RECORD_END, end, 1);
        } else if (node.getKind() == Tree.Kind.ANNOTATION_TYPE) {
            addToken(JavaTokenConstants.J_ANNO_T_END, end, 1);
        } else if (node.getKind() == Tree.Kind.CLASS) {
            addToken(JavaTokenConstants.J_CLASS_END, end, 1);
        }
        return result;
    }

    @Override
    public Object visitImport(ImportTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_IMPORT, start, 6);
        return super.visitImport(node, p);
    }

    @Override
    public Object visitPackage(PackageTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_PACKAGE, start, 7);
        return super.visitPackage(node, p);
    }

    @Override
    public Object visitMethod(MethodTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_METHOD_BEGIN, start, node.getName().length());
        Object result = super.visitMethod(node, p);
        addToken(JavaTokenConstants.J_METHOD_END, end, 1);
        return result;
    }

    @Override
    public Object visitSynchronized(SynchronizedTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_SYNC_BEGIN, start, 12);
        Object result = super.visitSynchronized(node, p);
        addToken(JavaTokenConstants.J_SYNC_END, end, 1);
        return result;
    }

    @Override
    public Object visitDoWhileLoop(DoWhileLoopTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_DO_BEGIN, start, 2);
        Object result = super.visitDoWhileLoop(node, p);
        addToken(JavaTokenConstants.J_DO_END, end, 1);
        return result;
    }

    @Override
    public Object visitWhileLoop(WhileLoopTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_WHILE_BEGIN, start, 5);
        Object result = super.visitWhileLoop(node, p);
        addToken(JavaTokenConstants.J_WHILE_END, end, 1);
        return result;
    }

    @Override
    public Object visitForLoop(ForLoopTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_FOR_BEGIN, start, 3);
        Object result = super.visitForLoop(node, p);
        addToken(JavaTokenConstants.J_FOR_END, end, 1);
        return result;
    }

    @Override
    public Object visitEnhancedForLoop(EnhancedForLoopTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_FOR_BEGIN, start, 3);
        Object result = super.visitEnhancedForLoop(node, p);
        addToken(JavaTokenConstants.J_FOR_END, end, 1);
        return result;
    }

    @Override
    public Object visitSwitch(SwitchTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_SWITCH_BEGIN, start, 6);
        Object result = super.visitSwitch(node, p);
        addToken(JavaTokenConstants.J_SWITCH_END, end, 1);
        return result;
    }

    @Override
    public Object visitSwitchExpression(SwitchExpressionTree node, Object parameterValue) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_SWITCH_BEGIN, start, 6);
        Object result = super.visitSwitchExpression(node, parameterValue);
        addToken(JavaTokenConstants.J_SWITCH_END, end, 1);
        return result;
    }

    @Override
    public Object visitCase(CaseTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_CASE, start, 4);
        return super.visitCase(node, p);
    }

    @Override
    public Object visitTry(TryTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        if (node.getResources().isEmpty())
            addToken(JavaTokenConstants.J_TRY_BEGIN, start, 3);
        else
            addToken(JavaTokenConstants.J_TRY_WITH_RESOURCE, start, 3);
        if (node.getFinallyBlock() != null)
            addToken(JavaTokenConstants.J_FINALLY, start, 3);
        return super.visitTry(node, p);
    }

    @Override
    public Object visitCatch(CatchTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_CATCH_BEGIN, start, 5);
        Object result = super.visitCatch(node, p);
        addToken(JavaTokenConstants.J_CATCH_END, end, 1);
        return result;
    }

    @Override
    public Object visitIf(IfTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_IF_BEGIN, start, 2);
        node.getCondition().accept(this, p);
        node.getThenStatement().accept(this, p);
        if (node.getElseStatement() != null) {
            start = positions.getStartPosition(ast, node.getElseStatement());
            addToken(JavaTokenConstants.J_ELSE, start, 4);
            node.getElseStatement().accept(this, p);
        }
        addToken(JavaTokenConstants.J_IF_END, end, 1);
        return null;
    }

    @Override
    public Object visitBreak(BreakTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_BREAK, start, 5);
        return super.visitBreak(node, p);
    }

    @Override
    public Object visitContinue(ContinueTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_CONTINUE, start, 8);
        return super.visitContinue(node, p);
    }

    @Override
    public Object visitReturn(ReturnTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_RETURN, start, 6);
        return super.visitReturn(node, p);
    }

    @Override
    public Object visitThrow(ThrowTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_THROW, start, 5);
        return super.visitThrow(node, p);
    }

    @Override
    public Object visitNewClass(NewClassTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        if (node.getTypeArguments().size() > 0) {
            addToken(JavaTokenConstants.J_GENERIC, start, 3 + node.getIdentifier().toString().length());
        }
        addToken(JavaTokenConstants.J_NEWCLASS, start, 3);
        return super.visitNewClass(node, p);
    }

    @Override
    public Object visitTypeParameter(TypeParameterTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        // This is odd, but also done like this in Java17
        addToken(JavaTokenConstants.J_GENERIC, start, 1);
        return super.visitTypeParameter(node, p);
    }

    @Override
    public Object visitNewArray(NewArrayTree node, Object arg1) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_NEWARRAY, start, 3);
        if (node.getInitializers() != null && !node.getInitializers().isEmpty()) {
            start = positions.getStartPosition(ast, node.getInitializers().get(0));
            addToken(JavaTokenConstants.J_ARRAY_INIT_BEGIN, start, 1);
            addToken(JavaTokenConstants.J_ARRAY_INIT_END, end, 1);
        }
        return super.visitNewArray(node, arg1);
    }

    @Override
    public Object visitAssignment(AssignmentTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_ASSIGN, start, 1);
        return super.visitAssignment(node, p);
    }

    @Override
    public Object visitAssert(AssertTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_ASSERT, start, 6);
        return super.visitAssert(node, p);
    }

    @Override
    public Object visitVariable(VariableTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_VARDEF, start, node.toString().length());
        return super.visitVariable(node, p);
    }

    @Override
    public Object visitConditionalExpression(ConditionalExpressionTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_COND, start, 1);
        return super.visitConditionalExpression(node, p);
    }

    @Override
    public Object visitMethodInvocation(MethodInvocationTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_APPLY, start, positions.getEndPosition(ast, node.getMethodSelect()) - start);
        return super.visitMethodInvocation(node, p);
    }

    @Override
    public Object visitAnnotation(AnnotationTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_ANNO, start, 1);
        return super.visitAnnotation(node, p);
    }

    @Override
    public Object visitModule(ModuleTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenConstants.J_MODULE_BEGIN, start, 6);
        Object result = super.visitModule(node, p);
        addToken(JavaTokenConstants.J_MODULE_END, end, 1);
        return result;
    }

    @Override
    public Object visitRequires(RequiresTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_REQUIRES, start, 8);
        return super.visitRequires(node, p);
    }

    @Override
    public Object visitProvides(ProvidesTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_PROVIDES, start, 8);
        return super.visitProvides(node, p);
    }

    @Override
    public Object visitExports(ExportsTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenConstants.J_EXPORTS, start, 7);
        return super.visitExports(node, p);
    }

    @Override
    public Object visitErroneous(ErroneousTree node, Object p) {
        parser.increaseErrors();
        return super.visitErroneous(node, p);
    }

    @Override
    public Object visitYield(YieldTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenConstants.J_YIELD, start, end);
        return super.visitYield(node, p);
    }

    @Override
    public Object visitDefaultCaseLabel(DefaultCaseLabelTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenConstants.J_DEFAULT, start, end);
        return super.visitDefaultCaseLabel(node, p);
    }
}
