package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;

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
import com.sun.source.tree.ExpressionTree;
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

import javax.lang.model.element.Name;

final class TokenGeneratingTreeScanner extends TreeScanner<Void, Void> {
    private final File file;
    private final Parser parser;
    private final LineMap map;
    private final SourcePositions positions;
    private final CompilationUnitTree ast;

    private List<ParsingException> parsingExceptions = new ArrayList<>();

    private int variableCount;
    private Map<Name, String> memberVariables; // map member variable name to id
    private Map<Name, Stack<String>> localVariables; // map local variable name to id
    private Map<String, Name> variableNames; // map variable id to name for debugging purposes, inverse of two maps above
    private Map<String, Boolean> variableIsMutable; // map variable id to whether it is immutable
    private Stack<Set<Name>> scopeVariables; // stack of local variable names in scope
    private NextOperation nextOperation;
    private boolean mutableWrite;

    private static final Set<String> IMMUTABLES = Set.of(
        // from https://medium.com/@bpnorlander/java-understanding-primitive-types-and-wrapper-objects-a6798fb2afe9
        "byte", "short", "int", "long", "float", "double", "boolean", "char",
        "Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character",
        "String"
    );

    enum NextOperation {
        NONE,
        READ,
        WRITE,
        READ_WRITE
    }

    public TokenGeneratingTreeScanner(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast) {
        this.file = file;
        this.parser = parser;
        this.map = map;
        this.positions = positions;
        this.ast = ast;
        this.variableCount = 0;
        this.memberVariables = new HashMap<>();
        this.localVariables = new HashMap<>();
        this.variableNames = new HashMap<>();
        this.variableIsMutable = new HashMap<>();
        this.scopeVariables = new Stack<>();
        this.nextOperation = NextOperation.READ; // the default
        this.mutableWrite = false;
    }

    public List<ParsingException> getParsingExceptions() {
        return parsingExceptions;
    }

    public void addToken(TokenType type, File file, long line, long column, long length) {
        parser.add(new Token(type, file, (int) line, (int) column, (int) length));
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param position is the start position of the token.
     * @param length is the length of the token.
     */
    private void addToken(JavaTokenType tokenType, long position, int length) {
        addToken(tokenType, file, map.getLineNumber(position), map.getColumnNumber(position), length);
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param start is the start position of the token.
     * @param end is the end position of the token for the calculation of the length.
     */
    private void addToken(JavaTokenType tokenType, long start, long end) {
        addToken(tokenType, file, map.getLineNumber(start), map.getColumnNumber(start), (end - start));
    }

    private String variableId() {
        return Integer.toString(variableCount++);
    }

    private String getMemberVariableId(Name variableName) {
        return memberVariables.getOrDefault(variableName, null);
    }

    private String getVariableId(Name variableName) {
        Stack<String> variableIdStack = localVariables.getOrDefault(variableName, null);
        if (variableIdStack != null) {
            return variableIdStack.peek();
        }
        return getMemberVariableId(variableName);
    }

    private boolean isVariable(ExpressionTree expressionTree) {
        return expressionTree.getKind() == Tree.Kind.IDENTIFIER
                || (expressionTree.getKind() == Tree.Kind.MEMBER_SELECT && isOwnMemberSelect((MemberSelectTree) expressionTree));
    }

    private boolean isOwnMemberSelect(MemberSelectTree memberSelect) {
        return memberSelect.getExpression().toString().equals("this");
    }

    private String formatVariable(String variableId) {
        return variableNames.get(variableId) + " [" + variableId + "]";
    }

    private boolean isMutable(Tree classTree) {
        return classTree != null && !IMMUTABLES.contains(classTree);
    }

    private void registerVariable(String variableId) {
        if (variableId != null) {
            if (Set.of(NextOperation.WRITE, NextOperation.READ_WRITE).contains(nextOperation)
                || mutableWrite && variableIsMutable.get(variableId)) {
                System.out.println("write " + formatVariable(variableId));
            }
            if (Set.of(NextOperation.READ, NextOperation.READ_WRITE).contains(nextOperation)) {
                System.out.println("read " + formatVariable(variableId));
            }
        }
        nextOperation = NextOperation.READ;
    }

    public void enterLocalScope() {
        scopeVariables.add(new HashSet<>());
    }

    public void exitLocalScope() {
        for (Name variableName : scopeVariables.pop()) {
            Stack<String> variableIdStack = localVariables.get(variableName);
            variableIdStack.pop();
            if (variableIdStack.isEmpty())
                localVariables.remove(variableName);
        }
    }

    @Override
    public Void visitBlock(BlockTree node, Void unused) {
        // classes are an obvious exception since members are treated differently
        Set<Tree.Kind> classKinds = Set.of(Tree.Kind.ENUM, Tree.Kind.INTERFACE, Tree.Kind.RECORD, Tree.Kind.ANNOTATION_TYPE, Tree.Kind.CLASS);
        boolean isClass = classKinds.contains(node.getKind());
        // for loops are also an exception since a scope can be induced without a block visit (without brackets)
        boolean isForLoop = Set.of(Tree.Kind.FOR_LOOP, Tree.Kind.ENHANCED_FOR_LOOP).contains(node.getKind());
        // methods and catches are also an exception since variables can be declared before the block begins
        if (!(isClass || isForLoop || Set.of(Tree.Kind.METHOD, Tree.Kind.CATCH).contains(node.getKind()))) {
            enterLocalScope();
        }
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_INIT_BEGIN, start, 1);
        super.visitBlock(node, unused);
        addToken(JavaTokenType.J_INIT_END, end, 1);
        if (!(isClass || isForLoop)) {
            exitLocalScope();
        }
        return null;
    }

    @Override
    public Void visitClass(ClassTree node, Void unused) {
        for (var member : node.getMembers()) {
            if (member.getKind() == Tree.Kind.VARIABLE) {
                VariableTree variable = (VariableTree) member;
                Name variableName = variable.getName();
                String variableId = variableId();
                // System.out.println("new member " + formatVariable(variableId));
                memberVariables.put(variableName, variableId);
                variableNames.put(variableId, variableName);
                variableIsMutable.put(variableId, isMutable(variable.getType()));
            }
        }
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
        super.visitClass(node, unused);
        JavaTokenType tokenType = switch (node.getKind()) {
            case ENUM -> JavaTokenType.J_ENUM_END;
            case INTERFACE -> JavaTokenType.J_INTERFACE_END;
            case RECORD -> JavaTokenType.J_RECORD_END;
            case ANNOTATION_TYPE -> JavaTokenType.J_ANNO_T_END;
            case CLASS -> JavaTokenType.J_CLASS_END;
            default -> null;
        };
        if (tokenType != null) {
            addToken(tokenType, end, 1);
        }
        memberVariables.clear();
        return null;
    }

    @Override
    public Void visitImport(ImportTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_IMPORT, start, 6);
        super.visitImport(node, unused);
        return null;
    }

    @Override
    public Void visitPackage(PackageTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_PACKAGE, start, 7);
        super.visitPackage(node, unused);
        return null;
    }

    @Override
    public Void visitMethod(MethodTree node, Void unused) {
        enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_METHOD_BEGIN, start, node.getName().length());
        super.visitMethod(node, unused);
        addToken(JavaTokenType.J_METHOD_END, end, 1);
        return null;
    }

    @Override
    public Void visitSynchronized(SynchronizedTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SYNC_BEGIN, start, 12);
        super.visitSynchronized(node, unused);
        addToken(JavaTokenType.J_SYNC_END, end, 1);
        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_DO_BEGIN, start, 2);
        super.visitDoWhileLoop(node, unused);
        addToken(JavaTokenType.J_DO_END, end, 1);
        return null;
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_WHILE_BEGIN, start, 5);
        super.visitWhileLoop(node, unused);
        addToken(JavaTokenType.J_WHILE_END, end, 1);
        return null;
    }

    @Override
    public Void visitForLoop(ForLoopTree node, Void unused) {
        enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_FOR_BEGIN, start, 3);
        super.visitForLoop(node, unused);
        addToken(JavaTokenType.J_FOR_END, end, 1);
        exitLocalScope();
        return null;
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void unused) {
        enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_FOR_BEGIN, start, 3);
        super.visitEnhancedForLoop(node, unused);
        addToken(JavaTokenType.J_FOR_END, end, 1);
        exitLocalScope();
        return null;
    }

    @Override
    public Void visitSwitch(SwitchTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6);
        super.visitSwitch(node, unused);
        addToken(JavaTokenType.J_SWITCH_END, end, 1);
        return null;
    }

    @Override
    public Void visitSwitchExpression(SwitchExpressionTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6);
        super.visitSwitchExpression(node, unused);
        addToken(JavaTokenType.J_SWITCH_END, end, 1);
        return null;
    }

    @Override
    public Void visitCase(CaseTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_CASE, start, 4);
        super.visitCase(node, unused);
        return null;
    }

    @Override
    public Void visitTry(TryTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        if (node.getResources().isEmpty())
            addToken(JavaTokenType.J_TRY_BEGIN, start, 3);
        else
            addToken(JavaTokenType.J_TRY_WITH_RESOURCE, start, 3);
        if (node.getFinallyBlock() != null)
            addToken(JavaTokenType.J_FINALLY, start, 3);
        super.visitTry(node, unused);
        return null;
    }

    @Override
    public Void visitCatch(CatchTree node, Void unused) {
        enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_CATCH_BEGIN, start, 5);
        super.visitCatch(node, unused);
        addToken(JavaTokenType.J_CATCH_END, end, 1);
        return null;
    }

    @Override
    public Void visitIf(IfTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_IF_BEGIN, start, 2);
        scan(node.getCondition(), unused);
        scan(node.getThenStatement(), unused);
        if (node.getElseStatement() != null) {
            start = positions.getStartPosition(ast, node.getElseStatement());
            addToken(JavaTokenType.J_ELSE, start, 4);
            scan(node.getElseStatement(), unused);
        }
        addToken(JavaTokenType.J_IF_END, end, 1);
        return null;
    }

    @Override
    public Void visitBreak(BreakTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_BREAK, start, 5);
        super.visitBreak(node, unused);
        return null;
    }

    @Override
    public Void visitContinue(ContinueTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_CONTINUE, start, 8);
        super.visitContinue(node, unused);
        return null;
    }

    @Override
    public Void visitReturn(ReturnTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_RETURN, start, 6);
        super.visitReturn(node, unused);
        return null;
    }

    @Override
    public Void visitThrow(ThrowTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_THROW, start, 5);
        super.visitThrow(node, unused);
        return null;
    }

    @Override
    public Void visitNewClass(NewClassTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        if (node.getTypeArguments().size() > 0) {
            addToken(JavaTokenType.J_GENERIC, start, 3 + node.getIdentifier().toString().length());
        }
        addToken(JavaTokenType.J_NEWCLASS, start, 3);
        super.visitNewClass(node, unused);
        return null;
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        // This is odd, but also done like this in Java17
        addToken(JavaTokenType.J_GENERIC, start, 1);
        super.visitTypeParameter(node, unused);
        return null;
    }

    @Override
    public Void visitNewArray(NewArrayTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_NEWARRAY, start, 3);
        if (node.getInitializers() != null && !node.getInitializers().isEmpty()) {
            start = positions.getStartPosition(ast, node.getInitializers().get(0));
            addToken(JavaTokenType.J_ARRAY_INIT_BEGIN, start, 1);
            addToken(JavaTokenType.J_ARRAY_INIT_END, end, 1);
        }
        super.visitNewArray(node, unused);
        return null;
    }

    @Override
    public Void visitAssignment(AssignmentTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ASSIGN, start, 1);
        nextOperation = NextOperation.WRITE;
        super.visitAssignment(node, unused);
        return null;
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ASSIGN, start, 1);
        nextOperation = NextOperation.READ_WRITE;
        super.visitCompoundAssignment(node, unused);
        return null;
    }

    @Override
    public Void visitUnary(UnaryTree node, Void unused) {
        if (Set.of(Tree.Kind.PREFIX_INCREMENT, Tree.Kind.POSTFIX_INCREMENT, Tree.Kind.PREFIX_DECREMENT, Tree.Kind.POSTFIX_DECREMENT)
                .contains(node.getKind())) {
            long start = positions.getStartPosition(ast, node);
            addToken(JavaTokenType.J_ASSIGN, start, 1);
            nextOperation = NextOperation.READ_WRITE;
        }
        super.visitUnary(node, unused);
        return null;
    }

    @Override
    public Void visitAssert(AssertTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ASSERT, start, 6);
        super.visitAssert(node, unused);
        return null;
    }

    @Override
    public Void visitVariable(VariableTree node, Void unused) {
        nextOperation = NextOperation.WRITE;
        if (!scopeVariables.isEmpty()) { // local scope
            Name variableName = node.getName();
            String variableId = variableId();
            // System.out.println("new local " + formatVariable(variableId));
            localVariables.putIfAbsent(variableName, new Stack<>());
            localVariables.get(variableName).push(variableId);
            variableNames.put(variableId, variableName);
            scopeVariables.peek().add(variableName);
            variableIsMutable.put(variableId, isMutable(node.getType()));
            registerVariable(variableId); // somewhat special case, identifier isn't visited
        } else {
            registerVariable(getMemberVariableId(node.getName()));
        }
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_VARDEF, start, node.toString().length());
        super.visitVariable(node, unused);
        return null;
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_COND, start, 1);
        super.visitConditionalExpression(node, unused);
        return null;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_APPLY, start, positions.getEndPosition(ast, node.getMethodSelect()) - start);
        // from super method, would need to be changed if return value were to be used
        scan(node.getTypeArguments(), unused);
        // to differentiate bar() and this.bar() (ignore) from bar.foo() (don't ignore), different namespace for variables and methods
        if (isVariable(node.getMethodSelect())) {
            nextOperation = NextOperation.NONE;
        }
        mutableWrite = true;  // when mentioned here, mutable variables can be written to
        scan(node.getMethodSelect(), unused);
        scan(node.getArguments(), unused);
        mutableWrite = false;
        return null;
    }

    @Override
    public Void visitAnnotation(AnnotationTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_ANNO, start, 1);
        super.visitAnnotation(node, unused);
        return null;
    }

    @Override
    public Void visitModule(ModuleTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        addToken(JavaTokenType.J_MODULE_BEGIN, start, 6);
        super.visitModule(node, unused);
        addToken(JavaTokenType.J_MODULE_END, end, 1);
        return null;
    }

    @Override
    public Void visitRequires(RequiresTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_REQUIRES, start, 8);
        super.visitRequires(node, unused);
        return null;
    }

    @Override
    public Void visitProvides(ProvidesTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_PROVIDES, start, 8);
        super.visitProvides(node, unused);
        return null;
    }

    @Override
    public Void visitExports(ExportsTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        addToken(JavaTokenType.J_EXPORTS, start, 7);
        super.visitExports(node, unused);
        return null;
    }

    @Override
    public Void visitErroneous(ErroneousTree node, Void unused) {
        parsingExceptions.add(new ParsingException(file, "error while visiting %s".formatted(node)));
        super.visitErroneous(node, unused);
        return null;
    }

    @Override
    public Void visitYield(YieldTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenType.J_YIELD, start, end);
        super.visitYield(node, unused);
        return null;
    }

    @Override
    public Void visitDefaultCaseLabel(DefaultCaseLabelTree node, Void unused) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        addToken(JavaTokenType.J_DEFAULT, start, end);
        super.visitDefaultCaseLabel(node, unused);
        return null;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree node, Void unused) {
        if (isOwnMemberSelect(node)) {
            registerVariable(getMemberVariableId(node.getIdentifier()));
        }
        super.visitMemberSelect(node, unused);
        return null;
    }

    @Override
    public Void visitIdentifier(IdentifierTree node, Void unused) {
        registerVariable(getVariableId(node.getName()));
        super.visitIdentifier(node, unused);
        return null;
    }
}
