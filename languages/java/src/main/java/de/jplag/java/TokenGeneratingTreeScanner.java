package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.NextOperation;
import de.jplag.semantics.VariableRegistry;

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
import com.sun.source.tree.ErroneousTree;
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

final class TokenGeneratingTreeScanner extends TreeScanner<Void, CodeSemantics> {
    private final File file;
    private final Parser parser;
    private final LineMap map;
    private final SourcePositions positions;
    private final CompilationUnitTree ast;

    private List<ParsingException> parsingExceptions = new ArrayList<>();

    private VariableRegistry variableRegistry;

    private static final Set<String> IMMUTABLES = Set.of(
            // from https://medium.com/@bpnorlander/java-understanding-primitive-types-and-wrapper-objects-a6798fb2afe9
            "byte", "short", "int", "long", "float", "double", "boolean", "char", // primitives
            "Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character", "String");

    public TokenGeneratingTreeScanner(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast) {
        this.file = file;
        this.parser = parser;
        this.map = map;
        this.positions = positions;
        this.ast = ast;
        this.variableRegistry = new VariableRegistry();
    }

    public List<ParsingException> getParsingExceptions() {
        return parsingExceptions;
    }

    public void addToken(TokenType type, File file, long line, long column, long length, CodeSemantics semantics) {
        parser.add(new Token(type, file, (int) line, (int) column, (int) length, semantics));
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param position is the start position of the token.
     * @param length is the length of the token.
     */
    private void addToken(JavaTokenType tokenType, long position, int length, CodeSemantics semantics) {
        addToken(tokenType, file, map.getLineNumber(position), map.getColumnNumber(position), length, semantics);
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param start is the start position of the token.
     * @param end is the end position of the token for the calculation of the length.
     */
    private void addToken(JavaTokenType tokenType, long start, long end, CodeSemantics semantics) {
        addToken(tokenType, file, map.getLineNumber(start), map.getColumnNumber(start), (end - start), semantics);
    }

    private boolean isOwnMemberSelect(MemberSelectTree memberSelect) {
        return memberSelect.toString().equals("this");
    }

    private boolean isMutable(Tree classTree) {
        // classTree is null if `var` keyword is used
        return classTree == null || !IMMUTABLES.contains(classTree.toString());
    }

    @Override
    public Void visitBlock(BlockTree node, CodeSemantics semantics) {
        // kind of weird since in the case of for loops and catches, two scopes are introduced
        // but I'm pretty sure that's how Java does it internally as well
        variableRegistry.enterLocalScope();
        super.visitBlock(node, null);
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitClass(ClassTree node, CodeSemantics semantics) {
        for (var member : node.getMembers()) {
            if (member.getKind() == Tree.Kind.VARIABLE) {
                VariableTree variableTree = (VariableTree) member;
                String name = variableTree.getName().toString();
                boolean mutable = isMutable(variableTree.getType());
                variableRegistry.registerMemberVariable(name, mutable);
            }
        }

        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createControl();
        if (node.getKind() == Tree.Kind.ENUM) {
            addToken(JavaTokenType.J_ENUM_BEGIN, start, 4, semantics);
        } else if (node.getKind() == Tree.Kind.INTERFACE) {
            addToken(JavaTokenType.J_INTERFACE_BEGIN, start, 9, semantics);
        } else if (node.getKind() == Tree.Kind.RECORD) {
            addToken(JavaTokenType.J_RECORD_BEGIN, start, 1, semantics);
        } else if (node.getKind() == Tree.Kind.ANNOTATION_TYPE) {
            addToken(JavaTokenType.J_ANNO_T_BEGIN, start, 10, semantics);
        } else if (node.getKind() == Tree.Kind.CLASS) {
            addToken(JavaTokenType.J_CLASS_BEGIN, start, 5, semantics);
        }
        scan(node.getModifiers(), semantics);
        scan(node.getTypeParameters(), semantics);
        scan(node.getExtendsClause(), semantics);
        scan(node.getImplementsClause(), semantics);
        scan(node.getPermitsClause(), semantics);
        scan(node.getMembers(), null);

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
        variableRegistry.clearMemberVariables();
        return null;
    }

    @Override
    public Void visitImport(ImportTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createKeep();
        addToken(JavaTokenType.J_IMPORT, start, 6, semantics);
        super.visitImport(node, semantics);
        return null;
    }

    @Override
    public Void visitPackage(PackageTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_PACKAGE, start, 7, semantics);
        super.visitPackage(node, semantics);
        return null;
    }

    @Override
    public Void visitMethod(MethodTree node, CodeSemantics semantics) {
        variableRegistry.enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_METHOD_BEGIN, start, node.getName().length(), semantics);
        scan(node.getModifiers(), semantics);
        scan(node.getReturnType(), semantics);
        scan(node.getTypeParameters(), semantics);
        scan(node.getParameters(), semantics);
        scan(node.getReceiverParameter(), semantics);
        scan(node.getThrows(), semantics);
        scan(node.getBody(), null);
        semantics = CodeSemantics.createControl();
        variableRegistry.addAllMemberVariablesAsReads(semantics);
        addToken(JavaTokenType.J_METHOD_END, end, 1, semantics);
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitSynchronized(SynchronizedTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_SYNC_BEGIN, start, 12, semantics);
        super.visitSynchronized(node, semantics);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_SYNC_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createLoopBegin();
        addToken(JavaTokenType.J_DO_BEGIN, start, 2, semantics);
        scan(node.getStatement(), null);
        semantics = CodeSemantics.createLoopEnd();
        addToken(JavaTokenType.J_DO_END, end, 1, semantics);
        scan(node.getCondition(), semantics);
        return null;
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createLoopBegin();
        addToken(JavaTokenType.J_WHILE_BEGIN, start, 5, semantics);
        scan(node.getCondition(), semantics);
        scan(node.getStatement(), null);
        semantics = CodeSemantics.createLoopEnd();
        addToken(JavaTokenType.J_WHILE_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitForLoop(ForLoopTree node, CodeSemantics semantics) {
        variableRegistry.enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createLoopBegin();
        addToken(JavaTokenType.J_FOR_BEGIN, start, 3, semantics);
        scan(node.getInitializer(), semantics);
        scan(node.getCondition(), semantics);
        scan(node.getUpdate(), semantics);
        scan(node.getStatement(), null);
        semantics = CodeSemantics.createLoopEnd();
        addToken(JavaTokenType.J_FOR_END, end, 1, semantics);
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree node, CodeSemantics semantics) {
        variableRegistry.enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createLoopBegin();
        addToken(JavaTokenType.J_FOR_BEGIN, start, 3, semantics);
        scan(node.getVariable(), semantics);
        scan(node.getExpression(), semantics);
        scan(node.getStatement(), null);
        semantics = CodeSemantics.createLoopEnd();
        addToken(JavaTokenType.J_FOR_END, end, 1, semantics);
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitSwitch(SwitchTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6, semantics);
        scan(node.getExpression(), semantics);
        scan(node.getCases(), null);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_SWITCH_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitSwitchExpression(SwitchExpressionTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6, semantics);
        scan(node.getExpression(), semantics);
        scan(node.getCases(), null);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_SWITCH_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitCase(CaseTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_CASE, start, 4, semantics);
        scan(node.getExpressions(), semantics);
        if (node.getCaseKind() == CaseTree.CaseKind.RULE) {
            scan(node.getBody(), semantics); // case -> result, in switch expression
        } else {
            scan(node.getStatements(), null); // in normal switch
        }
        return null;
    }

    @Override
    public Void visitTry(TryTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_TRY_BEGIN, start, 3, semantics);
        scan(node.getResources(), semantics);
        scan(node.getBlock(), null);
        long end = positions.getEndPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_TRY_END, end, 1, semantics);
        scan(node.getCatches(), null);
        if (node.getFinallyBlock() != null) {
            start = positions.getStartPosition(ast, node.getFinallyBlock());
            semantics = CodeSemantics.createControl();
            addToken(JavaTokenType.J_FINALLY_BEGIN, start, 3, semantics);
            scan(node.getFinallyBlock(), null);
            end = positions.getEndPosition(ast, node.getFinallyBlock());
            semantics = CodeSemantics.createControl();
            addToken(JavaTokenType.J_FINALLY_END, end, 1, semantics);
        }
        return null; // return value isn't used
    }

    @Override
    public Void visitCatch(CatchTree node, CodeSemantics semantics) {
        variableRegistry.enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_CATCH_BEGIN, start, 5, semantics);
        super.visitCatch(node, null); // can leave this since catch parameter is variable declaration and thus always generates a token
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_CATCH_END, end, 1, semantics);
        variableRegistry.exitLocalScope();
        return null;
    }

    @Override
    public Void visitIf(IfTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_IF_BEGIN, start, 2, semantics);
        scan(node.getCondition(), semantics);
        scan(node.getThenStatement(), null);
        if (node.getElseStatement() != null) {
            start = positions.getStartPosition(ast, node.getElseStatement());
            semantics = CodeSemantics.createControl();
            addToken(JavaTokenType.J_ELSE, start, 4, semantics);
        }
        scan(node.getElseStatement(), null);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_IF_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitBreak(BreakTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_BREAK, start, 5, semantics);
        super.visitBreak(node, semantics);
        return null;
    }

    @Override
    public Void visitContinue(ContinueTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_CONTINUE, start, 8, semantics);
        super.visitContinue(node, semantics);
        return null;
    }

    @Override
    public Void visitReturn(ReturnTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_RETURN, start, 6, semantics);
        super.visitReturn(node, semantics);
        return null;
    }

    @Override
    public Void visitThrow(ThrowTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_THROW, start, 5, semantics);
        super.visitThrow(node, semantics);
        return null;
    }

    @Override
    public Void visitNewClass(NewClassTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        if (node.getTypeArguments().size() > 0) {
            semantics = new CodeSemantics();
            addToken(JavaTokenType.J_GENERIC, start, 3 + node.getIdentifier().toString().length(), semantics);
        }
        semantics = new CodeSemantics();
        addToken(JavaTokenType.J_NEWCLASS, start, 3, semantics);
        super.visitNewClass(node, semantics);
        return null;
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        // This is odd, but also done like this in Java 1.7
        semantics = new CodeSemantics();
        addToken(JavaTokenType.J_GENERIC, start, 1, semantics);
        super.visitTypeParameter(node, semantics);
        return null;
    }

    @Override
    public Void visitNewArray(NewArrayTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new CodeSemantics();
        addToken(JavaTokenType.J_NEWARRAY, start, 3, semantics);
        scan(node.getType(), semantics);
        scan(node.getDimensions(), semantics);
        boolean hasInit = node.getInitializers() != null && !node.getInitializers().isEmpty();
        if (hasInit) {
            start = positions.getStartPosition(ast, node.getInitializers().get(0));
            semantics = new CodeSemantics();
            addToken(JavaTokenType.J_ARRAY_INIT_BEGIN, start, 1, semantics);
        }
        scan(node.getInitializers(), semantics);
        // super method has annotation processing but we have it disabled anyways
        if (hasInit) {
            semantics = new CodeSemantics();
            addToken(JavaTokenType.J_ARRAY_INIT_END, end, 1, semantics);
        }
        return null;
    }

    @Override
    public Void visitAssignment(AssignmentTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        // todo may need to be keep when non-registered (global) variables are involved, not sure how to check
        semantics = new CodeSemantics();
        addToken(JavaTokenType.J_ASSIGN, start, 1, semantics);
        variableRegistry.setNextOperation(NextOperation.WRITE);
        super.visitAssignment(node, semantics);
        // if (this.assignedVariableWasRegistered) makeSemanticsCritical(semantics)
        return null;
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new CodeSemantics();
        addToken(JavaTokenType.J_ASSIGN, start, 1, semantics);
        variableRegistry.setNextOperation(NextOperation.READ_WRITE);
        super.visitCompoundAssignment(node, semantics);
        return null;
    }

    @Override
    public Void visitUnary(UnaryTree node, CodeSemantics semantics) {
        semantics = new CodeSemantics();
        if (Set.of(Tree.Kind.PREFIX_INCREMENT, Tree.Kind.POSTFIX_INCREMENT, Tree.Kind.PREFIX_DECREMENT, Tree.Kind.POSTFIX_DECREMENT)
                .contains(node.getKind())) {
            long start = positions.getStartPosition(ast, node);
            addToken(JavaTokenType.J_ASSIGN, start, 1, semantics);
            variableRegistry.setNextOperation(NextOperation.READ_WRITE);
        }
        super.visitUnary(node, semantics);
        return null;
    }

    @Override
    public Void visitAssert(AssertTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_ASSERT, start, 6, semantics);
        super.visitAssert(node, semantics);
        return null;
    }

    @Override
    public Void visitVariable(VariableTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        String name = node.getName().toString();
        boolean inLocalScope = variableRegistry.inLocalScope();
        if (inLocalScope) {
            boolean mutable = isMutable(node.getType());
            variableRegistry.registerLocalVariable(name, mutable);
            semantics = new CodeSemantics();
        } else {
            semantics = CodeSemantics.createKeep();
        }
        variableRegistry.setNextOperation(NextOperation.WRITE);
        // manually add variable to semantics since identifier isn't visited
        variableRegistry.registerVariableOperation(name, !inLocalScope, semantics);
        addToken(JavaTokenType.J_VARDEF, start, node.toString().length(), semantics);
        super.visitVariable(node, semantics);
        return null;
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new CodeSemantics();
        addToken(JavaTokenType.J_COND, start, 1, semantics);
        super.visitConditionalExpression(node, semantics);
        return null;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        variableRegistry.addAllMemberVariablesAsReads(semantics);
        addToken(JavaTokenType.J_APPLY, start, positions.getEndPosition(ast, node.getMethodSelect()) - start, semantics);
        scan(node.getTypeArguments(), semantics);
        // differentiate bar() and this.bar() (ignore) from bar.foo() (don't ignore)
        // look at cases foo.bar()++ and foo().bar++
        variableRegistry.setIgnoreNextOperation(true);
        variableRegistry.setMutableWrite(true);
        scan(node.getMethodSelect(), semantics);  // foo.bar() is a write to foo
        scan(node.getArguments(), semantics);  // foo(bar) is a write to bar
        variableRegistry.setMutableWrite(false);
        return null;
    }

    @Override
    public Void visitAnnotation(AnnotationTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new CodeSemantics();
        addToken(JavaTokenType.J_ANNO, start, 1, semantics);
        super.visitAnnotation(node, semantics);
        return null;
    }

    @Override
    public Void visitModule(ModuleTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_MODULE_BEGIN, start, 6, semantics);
        super.visitModule(node, null);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_MODULE_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitRequires(RequiresTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_REQUIRES, start, 8, semantics);
        super.visitRequires(node, semantics);
        return null;
    }

    @Override
    public Void visitProvides(ProvidesTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_PROVIDES, start, 8, semantics);
        super.visitProvides(node, semantics);
        return null;
    }

    @Override
    public Void visitExports(ExportsTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_EXPORTS, start, 7, semantics);
        super.visitExports(node, semantics);
        return null;
    }

    @Override
    public Void visitErroneous(ErroneousTree node, CodeSemantics semantics) {
        parsingExceptions.add(new ParsingException(file, "error while visiting %s".formatted(node)));
        super.visitErroneous(node, semantics);
        return null;
    }

    @Override
    public Void visitYield(YieldTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_YIELD, start, end, semantics);
        super.visitYield(node, semantics);
        return null;
    }

    @Override
    public Void visitDefaultCaseLabel(DefaultCaseLabelTree node, CodeSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        semantics = CodeSemantics.createControl();
        addToken(JavaTokenType.J_DEFAULT, start, end, semantics);
        super.visitDefaultCaseLabel(node, semantics);
        return null;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree node, CodeSemantics semantics) {
        if (isOwnMemberSelect(node)) {
            variableRegistry.registerVariableOperation(node.getIdentifier().toString(), true, semantics);
        }
        variableRegistry.setIgnoreNextOperation(false);  // don't ignore the foo in foo.bar()
        super.visitMemberSelect(node, semantics);
        return null;
    }

    @Override
    public Void visitIdentifier(IdentifierTree node, CodeSemantics semantics) {
        variableRegistry.registerVariableOperation(node.toString(), false, semantics);
        super.visitIdentifier(node, semantics);
        return null;
    }
}
