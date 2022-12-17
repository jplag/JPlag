package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

import javax.lang.model.element.Name;

import de.jplag.ParsingException;
import de.jplag.TokenType;
import de.jplag.semantics.SemanticToken;
import de.jplag.semantics.TokenSemantics;
import de.jplag.semantics.TokenSemanticsBuilder;

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

final class TokenGeneratingTreeScanner extends TreeScanner<Void, TokenSemantics> {
    private final File file;
    private final Parser parser;
    private final LineMap map;
    private final SourcePositions positions;
    private final CompilationUnitTree ast;

    private List<ParsingException> parsingExceptions = new ArrayList<>();

    private int variableCount;
    private Map<Name, String> memberVariableIds; // map member variable name to id
    private Map<Name, Stack<String>> localVariableIdMap; // map local variable name to id
    private Set<String> localVariables;
    private Map<String, Name> variableNameMap; // map variable id to name for debugging purposes, inverse of two maps above
    private Map<String, Boolean> variableIsMutable; // map variable id to whether it is immutable
    private Stack<Set<Name>> scopeVariables; // stack of local variable names in scope
    private NextOperation nextOperation;
    private boolean mutableWrite;

    private static final Set<String> IMMUTABLES = Set.of(
            // from https://medium.com/@bpnorlander/java-understanding-primitive-types-and-wrapper-objects-a6798fb2afe9
            "byte", "short", "int", "long", "float", "double", "boolean", "char", "Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean",
            "Character", "String");

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
        this.memberVariableIds = new HashMap<>();
        this.localVariableIdMap = new HashMap<>();
        this.variableNameMap = new HashMap<>();
        this.localVariables = new HashSet<>();
        this.variableIsMutable = new HashMap<>();
        this.scopeVariables = new Stack<>();
        this.nextOperation = NextOperation.READ; // the default
        this.mutableWrite = false;
    }

    public List<ParsingException> getParsingExceptions() {
        return parsingExceptions;
    }

    public void addToken(TokenType type, File file, long line, long column, long length, TokenSemantics semantics) {
        parser.add(new SemanticToken(type, file, (int) line, (int) column, (int) length, semantics));
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param position is the start position of the token.
     * @param length is the length of the token.
     */
    private void addToken(JavaTokenType tokenType, long position, int length, TokenSemantics semantics) {
        addToken(tokenType, file, map.getLineNumber(position), map.getColumnNumber(position), length, semantics);
    }

    /**
     * Convenience method that adds a specific token.
     * @param tokenType is the type of the token.
     * @param start is the start position of the token.
     * @param end is the end position of the token for the calculation of the length.
     */
    private void addToken(JavaTokenType tokenType, long start, long end, TokenSemantics semantics) {
        addToken(tokenType, file, map.getLineNumber(start), map.getColumnNumber(start), (end - start), semantics);
    }

    private String variableId() {
        return Integer.toString(variableCount++);
    }

    private String getMemberVariableId(Name variableName) {
        return memberVariableIds.getOrDefault(variableName, null);
    }

    private String getVariableId(Name variableName) {
        Stack<String> variableIdStack = localVariableIdMap.getOrDefault(variableName, null);
        if (variableIdStack != null) {
            return variableIdStack.peek();
        }
        return getMemberVariableId(variableName);
    }

    private boolean isVariable(ExpressionTree expressionTree) {
        return expressionTree.getKind() == Tree.Kind.IDENTIFIER
                || (expressionTree.getKind() == Tree.Kind.MEMBER_SELECT && isOwnMemberSelect((MemberSelectTree) expressionTree));
    }

    private boolean isNotExistingLocalVariable(ExpressionTree expressionTree) {
        return !(expressionTree.getKind() == Tree.Kind.IDENTIFIER && localVariables.contains(((IdentifierTree) expressionTree).getName().toString()));
    }

    private boolean isOwnMemberSelect(MemberSelectTree memberSelect) {
        return memberSelect.getExpression().toString().equals("this");
    }

    private String formatVariable(String variableId) {
        return variableNameMap.get(variableId) + " [" + variableId + "]";
    }

    private boolean isMutable(Tree classTree) {
        return classTree != null && !IMMUTABLES.contains(classTree);
    }

    private void registerVariable(String variableId, TokenSemantics semantics) {
        if (variableId != null) {
            if (Set.of(NextOperation.WRITE, NextOperation.READ_WRITE).contains(nextOperation) || mutableWrite && variableIsMutable.get(variableId)) {
                // System.out.println("write " + formatVariable(variableId));
                semantics.addWrite(variableId);
            }
            if (Set.of(NextOperation.READ, NextOperation.READ_WRITE).contains(nextOperation)) {
                // System.out.println("read " + formatVariable(variableId));
                semantics.addRead(variableId); // todo change order it's read/write not write/read
            }
        }
        nextOperation = NextOperation.READ;
    }

    public void enterLocalScope() {
        scopeVariables.add(new HashSet<>());
    }

    public void exitLocalScope() {
        for (Name variableName : scopeVariables.pop()) {
            Stack<String> variableIdStack = localVariableIdMap.get(variableName);
            variableIdStack.pop();
            if (variableIdStack.isEmpty())
                localVariableIdMap.remove(variableName);
        }
    }

    @Override
    public Void visitBlock(BlockTree node, TokenSemantics semantics) {
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
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_INIT_BEGIN, start, 1, semantics);
        super.visitBlock(node, null);
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_INIT_END, end, 1, semantics);
        if (!(isClass || isForLoop)) {
            exitLocalScope();
        }
        return null;
    }

    @Override
    public Void visitClass(ClassTree node, TokenSemantics semantics) {
        for (var member : node.getMembers()) {
            if (member.getKind() == Tree.Kind.VARIABLE) {
                VariableTree variable = (VariableTree) member;
                Name variableName = variable.getName();
                String variableId = variableId();
                // System.out.println("new member " + formatVariable(variableId));
                memberVariableIds.put(variableName, variableId);
                variableNameMap.put(variableId, variableName);
                variableIsMutable.put(variableId, isMutable(variable.getType()));
            }
        }

        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().control().critical().build();
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
            semantics = new TokenSemanticsBuilder().control().critical().build();
            addToken(tokenType, end, 1, semantics);
        }
        memberVariableIds.clear();
        return null;
    }

    @Override
    public Void visitImport(ImportTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_IMPORT, start, 6, semantics);
        super.visitImport(node, semantics);
        return null;
    }

    @Override
    public Void visitPackage(PackageTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_PACKAGE, start, 7, semantics);
        super.visitPackage(node, semantics);
        return null;
    }

    @Override
    public Void visitMethod(MethodTree node, TokenSemantics semantics) {
        enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_METHOD_BEGIN, start, node.getName().length(), semantics);
        super.visitMethod(node, null);
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_METHOD_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitSynchronized(SynchronizedTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_SYNC_BEGIN, start, 12, semantics);
        super.visitSynchronized(node, semantics);
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_SYNC_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().loopBegin().build();
        addToken(JavaTokenType.J_DO_BEGIN, start, 2, semantics);
        scan(node.getStatement(), null);
        semantics = new TokenSemanticsBuilder().loopEnd().build();
        addToken(JavaTokenType.J_DO_END, end, 1, semantics);
        scan(node.getCondition(), semantics);
        return null;
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().loopBegin().build();
        addToken(JavaTokenType.J_WHILE_BEGIN, start, 5, semantics);
        scan(node.getCondition(), semantics);
        scan(node.getStatement(), null);
        semantics = new TokenSemanticsBuilder().loopEnd().build();
        addToken(JavaTokenType.J_WHILE_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitForLoop(ForLoopTree node, TokenSemantics semantics) {
        enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().loopBegin().build();
        addToken(JavaTokenType.J_FOR_BEGIN, start, 3, semantics);
        scan(node.getInitializer(), semantics);
        scan(node.getCondition(), semantics);
        scan(node.getUpdate(), semantics);
        scan(node.getStatement(), null);
        semantics = new TokenSemanticsBuilder().loopEnd().build();
        addToken(JavaTokenType.J_FOR_END, end, 1, semantics);
        exitLocalScope();
        return null;
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree node, TokenSemantics semantics) {
        enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().loopBegin().build();
        addToken(JavaTokenType.J_FOR_BEGIN, start, 3, semantics);
        scan(node.getVariable(), semantics);
        scan(node.getExpression(), semantics);
        scan(node.getStatement(), null);
        semantics = new TokenSemanticsBuilder().loopEnd().build();
        addToken(JavaTokenType.J_FOR_END, end, 1, semantics);
        exitLocalScope();
        return null;
    }

    @Override
    public Void visitSwitch(SwitchTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6, semantics);
        scan(node.getExpression(), semantics);
        scan(node.getCases(), null);
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_SWITCH_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitSwitchExpression(SwitchExpressionTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_SWITCH_BEGIN, start, 6, semantics);
        scan(node.getExpression(), semantics);
        scan(node.getCases(), null);
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_SWITCH_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitCase(CaseTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().build();
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
    public Void visitTry(TryTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().build();
        if (node.getResources().isEmpty()) {
            addToken(JavaTokenType.J_TRY_BEGIN, start, 3, semantics);
        } else {
            addToken(JavaTokenType.J_TRY_WITH_RESOURCE, start, 3, semantics);
        }
        if (node.getFinallyBlock() != null) { // todo fix location (breaks tests)
            semantics = new TokenSemanticsBuilder().control().build();
            addToken(JavaTokenType.J_FINALLY, start, 3, semantics);
        }
        scan(node.getResources(), semantics);
        scan(node.getBlock(), null);
        scan(node.getCatches(), null);
        scan(node.getFinallyBlock(), null);
        return null;
    }

    @Override
    public Void visitCatch(CatchTree node, TokenSemantics semantics) {
        enterLocalScope();
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_CATCH_BEGIN, start, 5, semantics);
        super.visitCatch(node, null); // can leave this since catch parameter is variable declaration and thus always generates a token
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_CATCH_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitIf(IfTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_IF_BEGIN, start, 2, semantics);
        scan(node.getCondition(), semantics);
        scan(node.getThenStatement(), null);
        if (node.getElseStatement() != null) {
            start = positions.getStartPosition(ast, node.getElseStatement());
            semantics = new TokenSemanticsBuilder().control().build();
            addToken(JavaTokenType.J_ELSE, start, 4, semantics);
        }
        scan(node.getElseStatement(), null);
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_IF_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitBreak(BreakTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_BREAK, start, 5, semantics);
        super.visitBreak(node, semantics);
        return null;
    }

    @Override
    public Void visitContinue(ContinueTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_CONTINUE, start, 8, semantics);
        super.visitContinue(node, semantics);
        return null;
    }

    @Override
    public Void visitReturn(ReturnTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_RETURN, start, 6, semantics);
        super.visitReturn(node, semantics);
        return null;
    }

    @Override
    public Void visitThrow(ThrowTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_THROW, start, 5, semantics);
        super.visitThrow(node, semantics);
        return null;
    }

    @Override
    public Void visitNewClass(NewClassTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        if (node.getTypeArguments().size() > 0) {
            semantics = new TokenSemanticsBuilder().build();
            addToken(JavaTokenType.J_GENERIC, start, 3 + node.getIdentifier().toString().length(), semantics);
        }
        semantics = new TokenSemanticsBuilder().build();
        addToken(JavaTokenType.J_NEWCLASS, start, 3, semantics);
        super.visitNewClass(node, semantics);
        return null;
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        // This is odd, but also done like this in Java17
        semantics = new TokenSemanticsBuilder().build();
        addToken(JavaTokenType.J_GENERIC, start, 1, semantics);
        super.visitTypeParameter(node, semantics);
        return null;
    }

    @Override
    public Void visitNewArray(NewArrayTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().build();
        addToken(JavaTokenType.J_NEWARRAY, start, 3, semantics);
        boolean hasInit = node.getInitializers() != null && !node.getInitializers().isEmpty();
        if (hasInit) {
            start = positions.getStartPosition(ast, node.getInitializers().get(0));
            semantics = new TokenSemanticsBuilder().build();
            addToken(JavaTokenType.J_ARRAY_INIT_BEGIN, start, 1, semantics);
        }
        super.visitNewArray(node, semantics); // doesn't break tests :)
        if (hasInit) {
            semantics = new TokenSemanticsBuilder().build();
            addToken(JavaTokenType.J_ARRAY_INIT_END, end, 1, semantics);
        }
        return null;
    }

    private TokenSemantics conditionalCriticalSemantics(ExpressionTree expressionTree, Function<ExpressionTree, Boolean> conditional) {
        TokenSemanticsBuilder semanticsBuilder = new TokenSemanticsBuilder();
        if (conditional.apply(expressionTree)) {
            semanticsBuilder.critical();
        }
        return semanticsBuilder.build();
    }

    @Override
    public Void visitAssignment(AssignmentTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = conditionalCriticalSemantics(node.getVariable(), this::isNotExistingLocalVariable);
        addToken(JavaTokenType.J_ASSIGN, start, 1, semantics);
        nextOperation = NextOperation.WRITE;
        super.visitAssignment(node, semantics);
        return null;
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = conditionalCriticalSemantics(node.getVariable(), this::isNotExistingLocalVariable);
        addToken(JavaTokenType.J_ASSIGN, start, 1, semantics);
        nextOperation = NextOperation.READ_WRITE;
        super.visitCompoundAssignment(node, semantics);
        return null;
    }

    @Override
    public Void visitUnary(UnaryTree node, TokenSemantics semantics) {
        semantics = conditionalCriticalSemantics(node.getExpression(), this::isNotExistingLocalVariable);
        if (Set.of(Tree.Kind.PREFIX_INCREMENT, Tree.Kind.POSTFIX_INCREMENT, Tree.Kind.PREFIX_DECREMENT, Tree.Kind.POSTFIX_DECREMENT)
                .contains(node.getKind())) {
            long start = positions.getStartPosition(ast, node);
            addToken(JavaTokenType.J_ASSIGN, start, 1, semantics);
            nextOperation = NextOperation.READ_WRITE;
        }
        super.visitUnary(node, semantics);
        return null;
    }

    @Override
    public Void visitAssert(AssertTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().critical().build();
        addToken(JavaTokenType.J_ASSERT, start, 6, semantics);
        super.visitAssert(node, semantics);
        return null;
    }

    @Override
    public Void visitVariable(VariableTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = conditionalCriticalSemantics(node.getNameExpression(), (n) -> scopeVariables.isEmpty()); // member variable defs are critical

        if (!scopeVariables.isEmpty()) { // local scope
            Name variableName = node.getName();
            String variableId = variableId();
            // System.out.println("new local " + formatVariable(variableId));
            localVariableIdMap.putIfAbsent(variableName, new Stack<>());
            localVariableIdMap.get(variableName).push(variableId);
            variableNameMap.put(variableId, variableName);
            localVariables.add(variableId);
            scopeVariables.peek().add(variableName);
            variableIsMutable.put(variableId, isMutable(node.getType()));
            registerVariable(variableId, semantics); // somewhat special case, identifier isn't visited
        } // no else, don't want to register member variable defs since the location doesn't matter (also they're going to be up
          // top 99% of the time)

        addToken(JavaTokenType.J_VARDEF, start, node.toString().length(), semantics);
        nextOperation = NextOperation.WRITE;
        super.visitVariable(node, semantics);
        return null;
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().build();
        addToken(JavaTokenType.J_COND, start, 1, semantics);
        super.visitConditionalExpression(node, semantics);
        return null;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().critical().control().build();
        addToken(JavaTokenType.J_APPLY, start, positions.getEndPosition(ast, node.getMethodSelect()) - start, semantics);
        scan(node.getTypeArguments(), semantics);
        // to differentiate bar() and this.bar() (ignore) from bar.foo() (don't ignore), different namespace for variables and
        // methods
        if (isVariable(node.getMethodSelect())) {
            nextOperation = NextOperation.NONE;
        }
        mutableWrite = true;  // when mentioned here, mutable variables can be written to
        scan(node.getMethodSelect(), semantics);
        scan(node.getArguments(), semantics);
        mutableWrite = false;
        return null;
    }

    @Override
    public Void visitAnnotation(AnnotationTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().build();
        addToken(JavaTokenType.J_ANNO, start, 1, semantics);
        super.visitAnnotation(node, semantics);
        return null;
    }

    @Override
    public Void visitModule(ModuleTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node) - 1;
        semantics = new TokenSemanticsBuilder().critical().control().build();
        addToken(JavaTokenType.J_MODULE_BEGIN, start, 6, semantics);
        super.visitModule(node, null);
        semantics = new TokenSemanticsBuilder().critical().control().build();
        addToken(JavaTokenType.J_MODULE_END, end, 1, semantics);
        return null;
    }

    @Override
    public Void visitRequires(RequiresTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().critical().control().build();
        addToken(JavaTokenType.J_REQUIRES, start, 8, semantics);
        super.visitRequires(node, semantics);
        return null;
    }

    @Override
    public Void visitProvides(ProvidesTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().critical().control().build();
        addToken(JavaTokenType.J_PROVIDES, start, 8, semantics);
        super.visitProvides(node, semantics);
        return null;
    }

    @Override
    public Void visitExports(ExportsTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        semantics = new TokenSemanticsBuilder().critical().control().build();
        addToken(JavaTokenType.J_EXPORTS, start, 7, semantics);
        super.visitExports(node, semantics);
        return null;
    }

    @Override
    public Void visitErroneous(ErroneousTree node, TokenSemantics semantics) {
        parsingExceptions.add(new ParsingException(file, "error while visiting %s".formatted(node)));
        super.visitErroneous(node, semantics);
        return null;
    }

    @Override
    public Void visitYield(YieldTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_YIELD, start, end, semantics);
        super.visitYield(node, semantics);
        return null;
    }

    @Override
    public Void visitDefaultCaseLabel(DefaultCaseLabelTree node, TokenSemantics semantics) {
        long start = positions.getStartPosition(ast, node);
        long end = positions.getEndPosition(ast, node);
        semantics = new TokenSemanticsBuilder().control().build();
        addToken(JavaTokenType.J_DEFAULT, start, end, semantics);
        super.visitDefaultCaseLabel(node, semantics);
        return null;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree node, TokenSemantics semantics) {
        if (isOwnMemberSelect(node)) {
            registerVariable(getMemberVariableId(node.getIdentifier()), semantics);
        }
        super.visitMemberSelect(node, semantics);
        return null;
    }

    @Override
    public Void visitIdentifier(IdentifierTree node, TokenSemantics semantics) {
        registerVariable(getVariableId(node.getName()), semantics);
        super.visitIdentifier(node, semantics);
        return null;
    }
}
