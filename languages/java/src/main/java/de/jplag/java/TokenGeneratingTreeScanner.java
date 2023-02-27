package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.ParsingException;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
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
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.YieldTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;

final class TokenGeneratingTreeScanner extends TreeScanner<Object, Object> {
    private final File file;
    private final Parser parser;
    private final LineMap map;
    private final SourcePositions positions;
    private final CompilationUnitTree ast;

    private List<ParsingException> parsingExceptions = new ArrayList<>();

    public TokenGeneratingTreeScanner(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast) {
        this.file = file;
        this.parser = parser;
        this.map = map;
        this.positions = positions;
        this.ast = ast;
    }

    public List<ParsingException> getParsingExceptions() {
        return parsingExceptions;
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param position is the start position of the token.
     * @param length is the length of the token.
     */
    private void addToken(JavaTokenType tokenType, long position, int length) {
        parser.add(tokenType, file, map.getLineNumber(position), map.getColumnNumber(position), length);
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param start is the start position of the token.
     * @param end is the end position of the token for the calculation of the length.
     */
    private void addToken(JavaTokenType tokenType, long start, long end) {
        parser.add(tokenType, file, map.getLineNumber(start), map.getColumnNumber(start), (end - start));
    }

    @Override
    public Object visitClass(ClassTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;

        if (node.getKind() == Tree.Kind.ENUM) {
            addToken(JavaTokenType.J_ENUM_BEGIN, start, 4);
        } else if (node.getKind() == Tree.Kind.INTERFACE) {
            addToken(JavaTokenType.J_INTERFACE_BEGIN, start, 9);
        } else if (node.getKind() == Tree.Kind.RECORD) {
            addToken(JavaTokenType.J_RECORD_BEGIN, start, 1);
        } else if (node.getKind() == Tree.Kind.ANNOTATION_TYPE) {
            addToken(JavaTokenType.J_ANNO_T_BEGIN, start, 10);
        } else if (node.getKind() == Tree.Kind.CLASS) {
            addToken(JavaTokenType.J_CLASS_BEGIN, start, 5);
        }
        Object result = super.visitClass(node, p);
        if (node.getKind() == Tree.Kind.ENUM) {
            addToken(JavaTokenType.J_ENUM_END, end, 1);
        } else if (node.getKind() == Tree.Kind.INTERFACE) {
            addToken(JavaTokenType.J_INTERFACE_END, end, 1);
        } else if (node.getKind() == Tree.Kind.RECORD) {
            addToken(JavaTokenType.J_RECORD_END, end, 1);
        } else if (node.getKind() == Tree.Kind.ANNOTATION_TYPE) {
            addToken(JavaTokenType.J_ANNO_T_END, end, 1);
        } else if (node.getKind() == Tree.Kind.CLASS) {
            addToken(JavaTokenType.J_CLASS_END, end, 1);
        }
        return result;
    }

    @Override
    public Object visitImport(ImportTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_IMPORT, start, 6);
        return super.visitImport(node, p);
    }

    @Override
    public Object visitPackage(PackageTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_PACKAGE, start, 7);
        return super.visitPackage(node, p);
    }

    @Override
    public Object visitMethod(MethodTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_METHOD_BEGIN, start, node.getName().length());
        Object result = super.visitMethod(node, p);
        addToken(JavaTokenType.J_METHOD_END, end, 1);
        return result;
    }

    @Override
    public Object visitSynchronized(SynchronizedTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SYNC_BEGIN, start, 12);
        Object result = super.visitSynchronized(node, p);
        addToken(JavaTokenType.J_SYNC_END, end, 1);
        return result;
    }

    @Override
    public Object visitDoWhileLoop(DoWhileLoopTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_DO_BEGIN, start, 2);
        Object result = super.visitDoWhileLoop(node, p);
        addToken(JavaTokenType.J_DO_END, end, 1);
        return result;
    }

    @Override
    public Object visitWhileLoop(WhileLoopTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_WHILE_BEGIN, start, 5);
        Object result = super.visitWhileLoop(node, p);
        addToken(JavaTokenType.J_WHILE_END, end, 1);
        return result;
    }

    @Override
    public Object visitForLoop(ForLoopTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_FOR_BEGIN, start, 3);
        Object result = super.visitForLoop(node, p);
        addToken(JavaTokenType.J_FOR_END, end, 1);
        return result;
    }

    @Override
    public Object visitEnhancedForLoop(EnhancedForLoopTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_FOR_BEGIN, start, 3);
        Object result = super.visitEnhancedForLoop(node, p);
        addToken(JavaTokenType.J_FOR_END, end, 1);
        return result;
    }

    @Override
    public Object visitSwitch(SwitchTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6);
        Object result = super.visitSwitch(node, p);
        addToken(JavaTokenType.J_SWITCH_END, end, 1);
        return result;
    }

    @Override
    public Object visitSwitchExpression(SwitchExpressionTree node, Object parameterValue) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6);
        Object result = super.visitSwitchExpression(node, parameterValue);
        addToken(JavaTokenType.J_SWITCH_END, end, 1);
        return result;
    }

    @Override
    public Object visitCase(CaseTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_CASE, start, 4);
        return super.visitCase(node, p);
    }

    @Override
    public Object visitTry(TryTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_TRY_BEGIN, start, 3);
        scan(node.getResources(), p);
        scan(node.getBlock(), p);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenType.J_TRY_END, end, 1);
        scan(node.getCatches(), p);
        if (node.getFinallyBlock() != null) {
            start = positions.getStartPosition(ast, node.getFinallyBlock());
            addToken(JavaTokenType.J_FINALLY_BEGIN, start, 3);
            scan(node.getFinallyBlock(), p);
            end = positions.getEndPosition(ast, node.getFinallyBlock());
            addToken(JavaTokenType.J_FINALLY_END, end, 1);
        }
        return null; // return value isn't used
    }

    @Override
    public Object visitCatch(CatchTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_CATCH_BEGIN, start, 5);
        Object result = super.visitCatch(node, p);
        addToken(JavaTokenType.J_CATCH_END, end, 1);
        return result;
    }

    @Override
    public Object visitIf(IfTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_IF_BEGIN, start, 2);
        scan(node.getCondition(), p);
        scan(node.getThenStatement(), p);
        if (node.getElseStatement() != null) {
            start = positions.getStartPosition(ast, node.getElseStatement());
            addToken(JavaTokenType.J_ELSE, start, 4);
        }
        scan(node.getElseStatement(), p);
        addToken(JavaTokenType.J_IF_END, end, 1);
        return null;
    }

    @Override
    public Object visitBreak(BreakTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_BREAK, start, 5);
        return super.visitBreak(node, p);
    }

    @Override
    public Object visitContinue(ContinueTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_CONTINUE, start, 8);
        return super.visitContinue(node, p);
    }

    @Override
    public Object visitReturn(ReturnTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_RETURN, start, 6);
        return super.visitReturn(node, p);
    }

    @Override
    public Object visitThrow(ThrowTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_THROW, start, 5);
        return super.visitThrow(node, p);
    }

    @Override
    public Object visitNewClass(NewClassTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        if (node.getTypeArguments().size() > 0) {
            addToken(JavaTokenType.J_GENERIC, start, 3 + node.getIdentifier().toString().length());
        }
        addToken(JavaTokenType.J_NEWCLASS, start, 3);
        return super.visitNewClass(node, p);
    }

    @Override
    public Object visitTypeParameter(TypeParameterTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        // This is odd, but also done like this in Java 1.7
        addToken(JavaTokenType.J_GENERIC, start, 1);
        return super.visitTypeParameter(node, p);
    }

    @Override
    public Object visitNewArray(NewArrayTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_NEWARRAY, start, 3);
        scan(node.getType(), p);
        scan(node.getDimensions(), p);
        boolean hasInit = node.getInitializers() != null && !node.getInitializers().isEmpty();
        if (hasInit) {
            start = positions.getStartPosition(ast, node.getInitializers().get(0));
            addToken(JavaTokenType.J_ARRAY_INIT_BEGIN, start, 1);
        }
        scan(node.getInitializers(), p);
        // super method has annotation processing but we have it disabled anyways
        if (hasInit) {
            addToken(JavaTokenType.J_ARRAY_INIT_END, end, 1);
        }
        return null; // return value isn't used
    }

    @Override
    public Object visitAssignment(AssignmentTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ASSIGN, start, 1);
        return super.visitAssignment(node, p);
    }

    @Override
    public Object visitCompoundAssignment(CompoundAssignmentTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ASSIGN, start, 1);
        return super.visitCompoundAssignment(node, p);
    }

    @Override
    public Object visitUnary(UnaryTree node, Object p) {
        if (Set.of(Tree.Kind.PREFIX_INCREMENT, Tree.Kind.POSTFIX_INCREMENT, Tree.Kind.PREFIX_DECREMENT, Tree.Kind.POSTFIX_DECREMENT)
                .contains(node.getKind())) {
            long start = positions.getStartPosition(ast, node);
            addToken(JavaTokenType.J_ASSIGN, start, 1);
        }
        return super.visitUnary(node, p);
    }

    @Override
    public Object visitAssert(AssertTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ASSERT, start, 6);
        return super.visitAssert(node, p);
    }

    @Override
    public Object visitVariable(VariableTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_VARDEF, start, node.toString().length());

        return super.visitVariable(node, p);
    }

    @Override
    public Object visitConditionalExpression(ConditionalExpressionTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_COND, start, 1);
        return super.visitConditionalExpression(node, p);
    }

    @Override
    public Object visitMethodInvocation(MethodInvocationTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_APPLY, start, positions.getEndPosition(ast, node.getMethodSelect()) - start);
        return super.visitMethodInvocation(node, p);
    }

    @Override
    public Object visitAnnotation(AnnotationTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ANNO, start, 1);
        return super.visitAnnotation(node, p);
    }

    @Override
    public Object visitModule(ModuleTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_MODULE_BEGIN, start, 6);
        Object result = super.visitModule(node, p);
        addToken(JavaTokenType.J_MODULE_END, end, 1);
        return result;
    }

    @Override
    public Object visitRequires(RequiresTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_REQUIRES, start, 8);
        return super.visitRequires(node, p);
    }

    @Override
    public Object visitProvides(ProvidesTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_PROVIDES, start, 8);
        return super.visitProvides(node, p);
    }

    @Override
    public Object visitExports(ExportsTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_EXPORTS, start, 7);
        return super.visitExports(node, p);
    }

    @Override
    public Object visitErroneous(ErroneousTree node, Object p) {
        parsingExceptions.add(new ParsingException(file, "error while visiting %s".formatted(node)));
        return super.visitErroneous(node, p);
    }

    @Override
    public Object visitYield(YieldTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenType.J_YIELD, start, end);
        return super.visitYield(node, p);
    }

    @Override
    public Object visitDefaultCaseLabel(DefaultCaseLabelTree node, Object p) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenType.J_DEFAULT, start, end);
        return super.visitDefaultCaseLabel(node, p);
    }
}
