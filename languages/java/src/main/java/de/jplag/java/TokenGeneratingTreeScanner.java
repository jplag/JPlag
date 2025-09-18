package de.jplag.java;

import java.io.File;
import java.util.Set;

import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableAccessType;
import de.jplag.semantics.VariableRegistry;
import de.jplag.semantics.VariableScope;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
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
import com.sun.source.tree.ExportsTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MemberSelectTree;
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

final class TokenGeneratingTreeScanner extends TreeScanner<Void, Void> {
    private final static String ANONYMOUS_VARIABLE_NAME = "";

    private final File file;
    private final Parser parser;
    private final LineMap map;
    private final SourcePositions positions;
    private final CompilationUnitTree ast;

    private final VariableRegistry variableRegistry;

    private static final Set<String> IMMUTABLES = Set.of(
            // from https://medium.com/@bpnorlander/java-understanding-primitive-types-and-wrapper-objects-a6798fb2afe9
            "byte", "short", "int", "long", "float", "double", "boolean", "char", // primitives
            "Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character", "String");

    private static final Set<String> CRITICAL_METHODS = Set.of("System.out.println", "System.out.print");

    public TokenGeneratingTreeScanner(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast) {
        this.file = file;
        this.parser = parser;
        this.map = map;
        this.positions = positions;
        this.ast = ast;
        this.variableRegistry = new VariableRegistry();
    }

    public void addToken(TokenType type, File file, long startLine, long startColumn, long endLine, long endColumn, long length,
            CodeSemantics semantics) {
        parser.add(new Token(type, file, Math.toIntExact(startLine), Math.toIntExact(startColumn), Math.toIntExact(endLine),
                Math.toIntExact(endColumn), Math.toIntExact(length), semantics));
        variableRegistry.updateSemantics(semantics);
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param position is the start position of the token.
     * @param length is the length of the token.
     */
    private void addToken(JavaTokenType tokenType, long position, int length, CodeSemantics semantics) {
        addToken(tokenType, position, position + length, semantics);
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param start is the start position of the token.
     * @param end is the end position of the token for the calculation of the length.
     */
    private void addToken(JavaTokenType tokenType, long start, long end, CodeSemantics semantics) {
        addToken(tokenType, file, map.getLineNumber(start), map.getColumnNumber(start), map.getLineNumber(end), map.getColumnNumber(end), end - start,
                semantics);
    }

    private boolean isMutable(Tree classTree) {
        // classTree is null if `var` keyword is used
        return classTree == null || !IMMUTABLES.contains(classTree.toString());
    }

    @Override
    public Void visitBlock(BlockTree node, Void unused) {
        // kind of weird since in the case of for loops and catches, two scopes are introduced
        // but I'm pretty sure that's how Java does it internally as well
        variableRegistry.enterLocalScope();
        super.visitBlock(node, null);
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitClass(ClassTree node, Void unused) {
        // not super accurate
        variableRegistry.registerVariable(node.getSimpleName().toString(), VariableScope.FILE, true);
        variableRegistry.enterClass();
        for (var member : node.getMembers()) {
            if (member.getKind() == Tree.Kind.VARIABLE) {
                VariableTree variableTree = (VariableTree) member;
                String name = variableTree.getName().toString();
                boolean mutable = isMutable(variableTree.getType());
                variableRegistry.registerVariable(name, VariableScope.CLASS, mutable);
            }
        }

        long start = extractStartPosition(node);
        long end = extractEndPosition(node, start);

        CodeSemantics semantics = CodeSemantics.createControl();
        if (node.getKind() == Tree.Kind.ENUM) {
            addToken(JavaTokenType.J_ENUM_BEGIN, start, 4, semantics);
        } else if (node.getKind() == Tree.Kind.INTERFACE) {
            addToken(JavaTokenType.J_INTERFACE_BEGIN, start, 9, semantics);
        } else if (node.getKind() == Tree.Kind.RECORD) {
            addToken(JavaTokenType.J_RECORD_BEGIN, start, 1, semantics);
        } else if (node.getKind() == Tree.Kind.ANNOTATION_TYPE) {
            long nameLength = node.getSimpleName().length();
            // The start position for the is calculated that way, because the @ is the final element in the modifier list for
            // annotations
            addToken(JavaTokenType.J_ANNO_T_BEGIN, start - 2, start - 2 + 11 + nameLength, semantics);
        } else if (node.getKind() == Tree.Kind.CLASS) {
            addToken(JavaTokenType.J_CLASS_BEGIN, start, 5, semantics);
        }
        super.visitClass(node, null);

        JavaTokenType tokenType = switch (node.getKind()) {
            case ENUM -> JavaTokenType.J_ENUM_END;
            case INTERFACE -> JavaTokenType.J_INTERFACE_END;
            case RECORD -> JavaTokenType.J_RECORD_END;
            case ANNOTATION_TYPE -> JavaTokenType.J_ANNO_T_END;
            case CLASS -> JavaTokenType.J_CLASS_END;
            default -> null;
        };
        if (tokenType != null) {
            semantics = CodeSemantics.createControl();
            addToken(tokenType, end, 1, semantics);
        }
        variableRegistry.exitClass();
        return null;
    }

    private long extractEndPosition(ClassTree node, long start) {
        long end = positions.getEndPosition(ast, node) - 1;
        if (end <= start) { // Java 25 compact source files have implicit classes
            // use end of last member as class end:
            return node.getMembers().stream().mapToLong(it -> positions.getEndPosition(ast, it)).max().orElse(start);
        }
        return end;
    }

    private long extractStartPosition(ClassTree node) {
        boolean hasModifiers = !node.getModifiers().getFlags().isEmpty();
        long endPosition = positions.getEndPosition(ast, node.getModifiers());
        if (hasModifiers && endPosition != -1) { // Java 25 compact source files have implicit (final) classes.
            return endPosition + 1;
        }
        return positions.getStartPosition(ast, node);
    }

    @Override
    public Void visitImport(ImportTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_IMPORT, start, 6, CodeSemantics.createKeep());
        return super.visitImport(node, null);
    }

    @Override
    public Void visitPackage(PackageTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_PACKAGE, start, 7, CodeSemantics.createControl());
        return super.visitPackage(node, null);
    }

    @Override
    public Void visitMethod(MethodTree node, Void unused) {
        variableRegistry.enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_METHOD_BEGIN, start, node.getName().length(), CodeSemantics.createControl());
        super.visitMethod(node, null);
        addToken(JavaTokenType.J_METHOD_END, end, 1, CodeSemantics.createControl());
        variableRegistry.addAllNonLocalVariablesAsReads();
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitSynchronized(SynchronizedTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SYNC_BEGIN, start, 12, CodeSemantics.createControl());
        super.visitSynchronized(node, null);
        addToken(JavaTokenType.J_SYNC_END, end, 1, CodeSemantics.createControl());
        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node.getStatement()) - 1;
        addToken(JavaTokenType.J_LOOP_BEGIN, start, 2, CodeSemantics.createLoopBegin());
        scan(node.getStatement(), null);
        addToken(JavaTokenType.J_LOOP_END, end, 1, CodeSemantics.createLoopEnd());
        scan(node.getCondition(), null);
        return null;
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_LOOP_BEGIN, start, 5, CodeSemantics.createLoopBegin());
        super.visitWhileLoop(node, null);
        addToken(JavaTokenType.J_LOOP_END, end, 1, CodeSemantics.createLoopEnd());
        return null;
    }

    @Override
    public Void visitForLoop(ForLoopTree node, Void unused) {
        variableRegistry.enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_LOOP_BEGIN, start, 3, CodeSemantics.createLoopBegin());
        super.visitForLoop(node, null);
        addToken(JavaTokenType.J_LOOP_END, end, 1, CodeSemantics.createLoopEnd());
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void unused) {
        variableRegistry.enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_LOOP_BEGIN, start, 3, CodeSemantics.createLoopBegin());
        super.visitEnhancedForLoop(node, null);
        addToken(JavaTokenType.J_LOOP_END, end, 1, CodeSemantics.createLoopEnd());
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitSwitch(SwitchTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6, CodeSemantics.createControl());
        super.visitSwitch(node, null);
        addToken(JavaTokenType.J_SWITCH_END, end, 1, CodeSemantics.createControl());
        return null;
    }

    @Override
    public Void visitSwitchExpression(SwitchExpressionTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6, CodeSemantics.createControl());
        super.visitSwitchExpression(node, null);
        addToken(JavaTokenType.J_SWITCH_END, end, 1, CodeSemantics.createControl());
        return null;
    }

    @Override
    public Void visitCase(CaseTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_CASE, start, 4, CodeSemantics.createControl());

        this.scan(node.getLabels(), null);
        if (node.getGuard() != null) {
            addToken(JavaTokenType.J_IF_BEGIN, positions.getStartPosition(ast, node.getGuard()), 0, CodeSemantics.createControl());
        }
        this.scan(node.getGuard(), null);
        if (node.getCaseKind() == CaseTree.CaseKind.RULE) {
            this.scan(node.getBody(), null);
        } else {
            this.scan(node.getStatements(), null);
        }

        if (node.getGuard() != null) {
            addToken(JavaTokenType.J_IF_END, positions.getEndPosition(ast, node), 0, CodeSemantics.createControl());
        }

        return null;
    }

    @Override
    public Void visitTry(TryTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_TRY_BEGIN, start, 3, CodeSemantics.createControl());
        scan(node.getResources(), null);
        scan(node.getBlock(), null);
        long end = positions.getEndPosition(ast, node);
        scan(node.getCatches(), null);
        if (node.getFinallyBlock() != null) {
            start = positions.getStartPosition(ast, node.getFinallyBlock());
            addToken(JavaTokenType.J_FINALLY_BEGIN, start, 3, CodeSemantics.createControl());
            scan(node.getFinallyBlock(), null);
            end = positions.getEndPosition(ast, node.getFinallyBlock());
            addToken(JavaTokenType.J_FINALLY_END, end, 1, CodeSemantics.createControl());
        }
        addToken(JavaTokenType.J_TRY_END, end, 1, CodeSemantics.createControl());
        return null;
    }

    @Override
    public Void visitCatch(CatchTree node, Void unused) {
        variableRegistry.enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_CATCH_BEGIN, start, 5, CodeSemantics.createControl());
        super.visitCatch(node, null);
        addToken(JavaTokenType.J_CATCH_END, end, 1, CodeSemantics.createControl());
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitIf(IfTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);

        addToken(JavaTokenType.J_IF_BEGIN, start, 2, CodeSemantics.createControl());
        scan(node.getCondition(), null);
        scan(node.getThenStatement(), null);
        long end = positions.getEndPosition(ast, node.getThenStatement()) - 1;
        addToken(JavaTokenType.J_IF_END, end, 1, CodeSemantics.createControl());
        boolean isElseOnly = false;
        if (node.getElseStatement() != null) {
            isElseOnly = node.getElseStatement().getKind() != Tree.Kind.IF;
            if (isElseOnly) {
                start = positions.getStartPosition(ast, node.getElseStatement());
                addToken(JavaTokenType.J_IF_BEGIN, start, 4, CodeSemantics.createControl());
            }
        }
        scan(node.getElseStatement(), null);
        if (isElseOnly) {
            end = positions.getEndPosition(ast, node) - 1;
            addToken(JavaTokenType.J_IF_END, end, 1, CodeSemantics.createControl());
        }
        return null;
    }

    @Override
    public Void visitBreak(BreakTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_BREAK, start, 5, CodeSemantics.createControl());
        return super.visitBreak(node, null);
    }

    @Override
    public Void visitContinue(ContinueTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_CONTINUE, start, 8, CodeSemantics.createControl());
        return super.visitContinue(node, null);
    }

    @Override
    public Void visitReturn(ReturnTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_RETURN, start, 6, CodeSemantics.createControl());
        return super.visitReturn(node, null);
    }

    @Override
    public Void visitThrow(ThrowTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_THROW, start, 5, CodeSemantics.createControl());
        return super.visitThrow(node, null);
    }

    @Override
    public Void visitNewClass(NewClassTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node.getIdentifier());
        if (!node.getTypeArguments().isEmpty()) {
            addToken(JavaTokenType.J_GENERIC, start, 3 + node.getIdentifier().toString().length(), new CodeSemantics());
        }
        addToken(JavaTokenType.J_NEWCLASS, start, end, new CodeSemantics());
        super.visitNewClass(node, null);
        return null;
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        // This is odd, but also done like this in Java 1.7
        addToken(JavaTokenType.J_GENERIC, start, 1, new CodeSemantics());
        return super.visitTypeParameter(node, null);
    }

    @Override
    public Void visitNewArray(NewArrayTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = node.getType() == null ? start + 1 : positions.getEndPosition(ast, node.getType());
        addToken(JavaTokenType.J_NEWARRAY, start, end, new CodeSemantics());
        scan(node.getType(), null);
        scan(node.getDimensions(), null);
        boolean hasInit = node.getInitializers() != null && !node.getInitializers().isEmpty();
        if (hasInit) {
            start = positions.getStartPosition(ast, node.getInitializers().get(0));
            addToken(JavaTokenType.J_ARRAY_INIT_BEGIN, start, 1, new CodeSemantics());
        }
        scan(node.getInitializers(), null);
        // super method has annotation processing but we have it disabled anyways
        if (hasInit) {
            end = positions.getEndPosition(ast, node.getInitializers().getLast()) - 1;
            addToken(JavaTokenType.J_ARRAY_INIT_END, end, 1, new CodeSemantics());
        }
        return null;
    }

    @Override
    public Void visitAssignment(AssignmentTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getStartPosition(ast, node.getExpression()) - 1;
        addToken(JavaTokenType.J_ASSIGN, start, end, new CodeSemantics());
        variableRegistry.setNextVariableAccessType(VariableAccessType.WRITE);
        return super.visitAssignment(node, null);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getStartPosition(ast, node.getExpression()) - 1;
        addToken(JavaTokenType.J_ASSIGN, start, end, new CodeSemantics());
        variableRegistry.setNextVariableAccessType(VariableAccessType.READ_WRITE);
        return super.visitCompoundAssignment(node, null);
    }

    @Override
    public Void visitUnary(UnaryTree node, Void unused) {
        if (Set.of(Tree.Kind.PREFIX_INCREMENT, Tree.Kind.POSTFIX_INCREMENT, Tree.Kind.PREFIX_DECREMENT, Tree.Kind.POSTFIX_DECREMENT)
                .contains(node.getKind())) {
            long start = positions.getStartPosition(ast, node);
            addToken(JavaTokenType.J_ASSIGN, start, node.toString().length(), new CodeSemantics());
            variableRegistry.setNextVariableAccessType(VariableAccessType.READ_WRITE);
        }
        return super.visitUnary(node, null);
    }

    @Override
    public Void visitAssert(AssertTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ASSERT, start, 6, CodeSemantics.createControl());
        return super.visitAssert(node, null);
    }

    @Override
    public Void visitVariable(VariableTree node, Void unused) {
        if (!node.getName().contentEquals(ANONYMOUS_VARIABLE_NAME)) {
            long start = positions.getStartPosition(ast, node);
            long end = positions.getEndPosition(ast, node) - 1;
            end -= node.getInitializer() == null ? 0 : node.getInitializer().toString().length();
            String name = node.getName().toString();
            boolean inLocalScope = variableRegistry.inLocalScope();
            // this presents a problem when classes are declared in local scopes, which can happen in ad-hoc implementations
            CodeSemantics semantics;
            if (inLocalScope) {
                boolean mutable = isMutable(node.getType());
                variableRegistry.registerVariable(name, VariableScope.LOCAL, mutable);
                semantics = new CodeSemantics();
            } else {
                semantics = CodeSemantics.createKeep();
            }
            addToken(JavaTokenType.J_VARDEF, start, end, semantics);
            // manually add variable to semantics since identifier isn't visited
            variableRegistry.setNextVariableAccessType(VariableAccessType.WRITE);
            variableRegistry.registerVariableAccess(name, !inLocalScope);
        }
        return super.visitVariable(node, null);
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_COND, start, 1, new CodeSemantics());
        return super.visitConditionalExpression(node, null);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node.getMethodSelect());
        CodeSemantics codeSemantics = CRITICAL_METHODS.contains(node.getMethodSelect().toString()) ? CodeSemantics.createCritical()
                : CodeSemantics.createControl();
        addToken(JavaTokenType.J_APPLY, start, end, codeSemantics);
        variableRegistry.addAllNonLocalVariablesAsReads();
        scan(node.getTypeArguments(), null);
        // differentiate bar() and this.bar() (ignore) from bar.foo() (don't ignore)
        // look at cases foo.bar()++ and foo().bar++
        variableRegistry.setIgnoreNextVariableAccess(true);
        variableRegistry.setMutableWrite(true);
        scan(node.getMethodSelect(), null);  // foo.bar() is a write to foo
        scan(node.getArguments(), null);  // foo(bar) is a write to bar
        variableRegistry.setMutableWrite(false);
        return null;
    }

    @Override
    public Void visitAnnotation(AnnotationTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        String annotationName = node.getAnnotationType().toString();
        addToken(JavaTokenType.J_ANNO, start, annotationName.length() + 1, new CodeSemantics());
        return super.visitAnnotation(node, null);
    }

    @Override
    public Void visitModule(ModuleTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_MODULE_BEGIN, start, 6, CodeSemantics.createControl());
        super.visitModule(node, null);
        addToken(JavaTokenType.J_MODULE_END, end, 1, CodeSemantics.createControl());
        return null;
    }

    @Override
    public Void visitRequires(RequiresTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_REQUIRES, start, 8, CodeSemantics.createControl());
        return super.visitRequires(node, null);
    }

    @Override
    public Void visitProvides(ProvidesTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_PROVIDES, start, 8, CodeSemantics.createControl());
        return super.visitProvides(node, null);
    }

    @Override
    public Void visitExports(ExportsTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_EXPORTS, start, 7, CodeSemantics.createControl());
        return super.visitExports(node, null);
    }

    @Override
    public Void visitYield(YieldTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenType.J_YIELD, start, end, CodeSemantics.createControl());
        return super.visitYield(node, null);
    }

    @Override
    public Void visitDefaultCaseLabel(DefaultCaseLabelTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenType.J_DEFAULT, start, end, CodeSemantics.createControl());
        return super.visitDefaultCaseLabel(node, null);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree node, Void unused) {
        if ("this".equals(node.getExpression().toString())) {
            variableRegistry.registerVariableAccess(node.getIdentifier().toString(), true);
        }
        variableRegistry.setIgnoreNextVariableAccess(false);  // don't ignore the foo in foo.bar()
        return super.visitMemberSelect(node, null);
    }

    @Override
    public Void visitIdentifier(IdentifierTree node, Void unused) {
        variableRegistry.registerVariableAccess(node.toString(), false);
        return super.visitIdentifier(node, null);
    }
}
