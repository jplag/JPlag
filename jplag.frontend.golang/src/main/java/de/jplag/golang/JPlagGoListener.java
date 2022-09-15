package de.jplag.golang;

import static de.jplag.golang.GoTokenType.*;

import java.util.*;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.TokenType;
import de.jplag.golang.grammar.GoParser;
import de.jplag.golang.grammar.GoParserBaseListener;

public class JPlagGoListener extends GoParserBaseListener {

    private final GoParserAdapter parserAdapter;
    private final Deque<GoBlockContext> blockContexts;

    public JPlagGoListener(GoParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
        blockContexts = new LinkedList<>();
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the grammar's token given by token.
     * @param tokenType the custom token type that occurred.
     * @param token the corresponding grammar's token
     */
    private void transformToken(TokenType tokenType, Token token) {
        parserAdapter.addToken(tokenType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the current grammatical context given by
     * start and end.
     * @param tokenType the custom token type that occurred.
     * @param start the first Token of the context
     * @param end the last Token of the context
     */
    private void transformToken(GoTokenType tokenType, Token start, Token end) {
        parserAdapter.addToken(tokenType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    private void enterContext(GoBlockContext context) {
        blockContexts.push(context);
    }

    private void expectAndLeave(GoBlockContext... contexts) {
        GoBlockContext topContext = blockContexts.pop();
        assert Arrays.stream(contexts).anyMatch(context -> context == topContext);
    }

    private GoBlockContext getCurrentContext() {
        return blockContexts.peek();
    }

    /* TOP LEVEL STRUCTURES */

    @Override
    public void enterPackageClause(GoParser.PackageClauseContext context) {
        transformToken(PACKAGE, context.getStart(), context.getStop());
        super.enterPackageClause(context);
    }

    @Override
    public void enterImportDecl(GoParser.ImportDeclContext context) {
        transformToken(IMPORT_DECLARATION, context.getStart());

        // if the children contain TerminalNodes, then it must be '(' and ')'
        Optional<TerminalNode> listStart = context.children.stream().filter(TerminalNode.class::isInstance).map(TerminalNode.class::cast).findFirst();
        listStart.ifPresent(lParenTree -> transformToken(IMPORT_CLAUSE_BEGIN, lParenTree.getSymbol()));

        super.enterImportDecl(context);
    }

    @Override
    public void exitImportDecl(GoParser.ImportDeclContext context) {
        if (context.getStop().getText().equals(")")) {
            transformToken(IMPORT_CLAUSE_END, context.getStop());
        }
        super.exitImportDecl(context);
    }

    @Override
    public void enterImportSpec(GoParser.ImportSpecContext context) {
        transformToken(IMPORT_CLAUSE, context.getStart(), context.getStop());
        super.enterImportSpec(context);
    }

    /* INTERFACE */

    @Override
    public void enterInterfaceType(GoParser.InterfaceTypeContext context) {
        transformToken(INTERFACE_DECLARATION, context.getStart());
        enterContext(GoBlockContext.INTERFACE_BODY);
        super.enterInterfaceType(context);
    }

    @Override
    public void exitInterfaceType(GoParser.InterfaceTypeContext context) {
        expectAndLeave(GoBlockContext.INTERFACE_BODY);
        super.exitInterfaceType(context);
    }

    /* STRUCT */

    @Override
    public void enterStructType(GoParser.StructTypeContext context) {
        transformToken(STRUCT_DECLARATION, context.getStart());
        enterContext(GoBlockContext.STRUCT_BODY);
        super.enterStructType(context);
    }

    @Override
    public void exitStructType(GoParser.StructTypeContext context) {
        expectAndLeave(GoBlockContext.STRUCT_BODY);
        super.exitStructType(context);
    }

    @Override
    public void enterFieldDecl(GoParser.FieldDeclContext context) {
        transformToken(MEMBER_DECLARATION, context.getStart(), context.getStop());
        super.enterFieldDecl(context);
    }

    /* FUNCTION */

    @Override
    public void enterFunctionDecl(GoParser.FunctionDeclContext context) {
        transformToken(FUNCTION_DECLARATION, context.getStart());
        enterContext(GoBlockContext.FUNCTION_BODY);
        super.enterFunctionDecl(context);
    }

    @Override
    public void exitFunctionDecl(GoParser.FunctionDeclContext context) {
        expectAndLeave(GoBlockContext.FUNCTION_BODY);
        super.exitFunctionDecl(context);
    }

    @Override
    public void enterMethodDecl(GoParser.MethodDeclContext context) {
        transformToken(FUNCTION_DECLARATION, context.getStart());
        enterContext(GoBlockContext.FUNCTION_BODY);
        super.enterMethodDecl(context);
    }

    @Override
    public void exitMethodDecl(GoParser.MethodDeclContext context) {
        expectAndLeave(GoBlockContext.FUNCTION_BODY);
        super.exitMethodDecl(context);
    }

    @Override
    public void enterParameterDecl(GoParser.ParameterDeclContext context) {
        if (context.parent.parent instanceof GoParser.ReceiverContext) {
            transformToken(RECEIVER, context.getStart(), context.getStop());
        } else {
            transformToken(FUNCTION_PARAMETER, context.getStart(), context.getStop());
        }
        super.enterParameterDecl(context);
    }

    /* CONTROL FLOW STATEMENTS */

    @Override
    public void enterIfStmt(GoParser.IfStmtContext context) {
        transformToken(IF_STATEMENT, context.getStart());
        enterContext(GoBlockContext.IF_BLOCK);
        super.enterIfStmt(context);
    }

    @Override
    public void exitIfStmt(GoParser.IfStmtContext context) {
        expectAndLeave(GoBlockContext.IF_BLOCK, GoBlockContext.ELSE_BLOCK);
        super.exitIfStmt(context);
    }

    @Override
    public void enterForStmt(GoParser.ForStmtContext context) {
        transformToken(FOR_STATEMENT, context.getStart());
        enterContext(GoBlockContext.FOR_BLOCK);
        super.enterForStmt(context);
    }

    @Override
    public void exitForStmt(GoParser.ForStmtContext context) {
        expectAndLeave(GoBlockContext.FOR_BLOCK);
        super.exitForStmt(context);
    }

    @Override
    public void enterSwitchStmt(GoParser.SwitchStmtContext context) {
        transformToken(SWITCH_STATEMENT, context.getStart());
        enterContext(GoBlockContext.SWITCH_BLOCK);
        super.enterSwitchStmt(context);
    }

    @Override
    public void exitSwitchStmt(GoParser.SwitchStmtContext context) {
        expectAndLeave(GoBlockContext.SWITCH_BLOCK);
        super.exitSwitchStmt(context);
    }

    @Override
    public void enterExprCaseClause(GoParser.ExprCaseClauseContext context) {
        transformToken(SWITCH_CASE, context.getStart());
        var caseBlock = context.getChild(GoParser.StatementListContext.class, 0);
        if (caseBlock != null) {
            enterContext(GoBlockContext.CASE_BLOCK);
            transformToken(CASE_BLOCK_BEGIN, caseBlock.getStart());
        }
        super.enterExprCaseClause(context);
    }

    @Override
    public void exitExprCaseClause(GoParser.ExprCaseClauseContext context) {
        if (getCurrentContext() == GoBlockContext.CASE_BLOCK) {
            transformToken(CASE_BLOCK_END, context.getStop());
            expectAndLeave(GoBlockContext.CASE_BLOCK);
        }
        super.exitExprCaseClause(context);
    }

    @Override
    public void enterTypeCaseClause(GoParser.TypeCaseClauseContext context) {
        transformToken(SWITCH_CASE, context.getStart());
        var caseBlock = context.getChild(GoParser.StatementListContext.class, 0);
        if (caseBlock != null) {
            enterContext(GoBlockContext.CASE_BLOCK);
            transformToken(CASE_BLOCK_BEGIN, caseBlock.getStart());
        }
        super.enterTypeCaseClause(context);
    }

    @Override
    public void exitTypeCaseClause(GoParser.TypeCaseClauseContext context) {
        if (getCurrentContext() == GoBlockContext.CASE_BLOCK) {
            transformToken(CASE_BLOCK_END, context.getStop());
            expectAndLeave(GoBlockContext.CASE_BLOCK);
        }
        super.exitTypeCaseClause(context);
    }

    @Override
    public void enterSelectStmt(GoParser.SelectStmtContext context) {
        transformToken(SELECT_STATEMENT, context.getStart());
        enterContext(GoBlockContext.SELECT_CONTEXT);
        super.enterSelectStmt(context);
    }

    @Override
    public void exitSelectStmt(GoParser.SelectStmtContext context) {
        expectAndLeave(GoBlockContext.SELECT_CONTEXT);
        super.exitSelectStmt(context);
    }

    @Override
    public void enterCommCase(GoParser.CommCaseContext context) {
        transformToken(SWITCH_CASE, context.getStart());
        var caseBlock = context.getChild(GoParser.StatementListContext.class, 0);
        if (caseBlock != null) {
            enterContext(GoBlockContext.CASE_BLOCK);
            transformToken(CASE_BLOCK_BEGIN, caseBlock.getStart());
        }
        super.enterCommCase(context);
    }

    @Override
    public void exitCommCase(GoParser.CommCaseContext context) {
        if (getCurrentContext() == GoBlockContext.CASE_BLOCK) {
            transformToken(CASE_BLOCK_END, context.getStop());
            expectAndLeave(GoBlockContext.CASE_BLOCK);
        }
        super.exitCommCase(context);
    }

    /* STATEMENTS */

    @Override
    public void enterVarDecl(GoParser.VarDeclContext context) {
        transformToken(VARIABLE_DECLARATION, context.getStart(), context.getStop());
        super.enterVarDecl(context);
    }

    @Override
    public void enterConstSpec(GoParser.ConstSpecContext context) {
        transformToken(VARIABLE_DECLARATION, context.getStart());
        super.enterConstSpec(context);
    }

    @Override
    public void enterFunctionLit(GoParser.FunctionLitContext context) {
        transformToken(FUNCTION_LITERAL, context.getStart());
        enterContext(GoBlockContext.FUNCTION_BODY);
        super.enterFunctionLit(context);
    }

    @Override
    public void exitFunctionLit(GoParser.FunctionLitContext context) {
        expectAndLeave(GoBlockContext.FUNCTION_BODY);
        super.exitFunctionLit(context);
    }

    @Override
    public void enterAssignment(GoParser.AssignmentContext context) {
        transformToken(ASSIGNMENT, context.getStart(), context.getStop());
        super.enterAssignment(context);
    }

    @Override
    public void enterShortVarDecl(GoParser.ShortVarDeclContext context) {
        transformToken(VARIABLE_DECLARATION, context.getStart());
        transformToken(ASSIGNMENT, context.getStart());
        super.enterShortVarDecl(context);
    }

    @Override
    public void enterArguments(GoParser.ArgumentsContext context) {
        transformToken(INVOCATION, context.getStart(), context.getStop());

        // Arguments consist of ExpressionLists, which consist of Expressions
        // Get all Expressions of all ExpressionLists in this ArgumentsContext
        context.getRuleContexts(GoParser.ExpressionListContext.class).stream()
                .flatMap(child -> child.getRuleContexts(GoParser.ExpressionContext.class).stream())
                .forEachOrdered(arg -> transformToken(ARGUMENT, arg.getStart(), arg.getStop()));
        super.enterArguments(context);
    }

    @Override
    public void enterStatement(GoParser.StatementContext context) {
        enterContext(GoBlockContext.STATEMENT_BLOCK);
        super.enterStatement(context);
    }

    @Override
    public void exitStatement(GoParser.StatementContext context) {
        expectAndLeave(GoBlockContext.STATEMENT_BLOCK);
        super.exitStatement(context);
    }

    /* OBJECT CREATION */

    @Override
    public void enterKeyedElement(GoParser.KeyedElementContext context) {
        Optional<GoTokenType> tokenType = getCurrentContext().getElement();
        tokenType.ifPresent(type -> transformToken(type, context.getStart(), context.getStop()));
        super.enterKeyedElement(context);
    }

    @Override
    public void enterArrayType(GoParser.ArrayTypeContext context) {
        // otherwise, it is just a type expression
        if (context.parent.parent instanceof GoParser.CompositeLitContext) {
            enterContext(GoBlockContext.ARRAY_BODY);
            transformToken(ARRAY_CONSTRUCTOR, context.getStart(), context.getStop());
        }
        super.enterArrayType(context);
    }

    @Override
    public void enterSliceType(GoParser.SliceTypeContext context) {
        // otherwise, it is just a type expression
        if (context.parent.parent instanceof GoParser.CompositeLitContext) {
            enterContext(GoBlockContext.SLICE_BODY);
            transformToken(SLICE_CONSTRUCTOR, context.getStart(), context.getStop());
        }
        super.enterSliceType(context);
    }

    @Override
    public void exitCompositeLit(GoParser.CompositeLitContext context) {
        expectAndLeave(GoBlockContext.MAP_BODY, GoBlockContext.SLICE_BODY, GoBlockContext.ARRAY_BODY, GoBlockContext.NAMED_TYPE_BODY);
        super.exitCompositeLit(context);
    }

    @Override
    public void enterMapType(GoParser.MapTypeContext context) {
        // otherwise, it is just a type expression
        if (context.parent.parent instanceof GoParser.CompositeLitContext) {
            enterContext(GoBlockContext.MAP_BODY);
            transformToken(MAP_CONSTRUCTOR, context.getStart(), context.getStop());
        }
        super.enterMapType(context);
    }

    @Override
    public void enterTypeName(GoParser.TypeNameContext context) {
        if (context.parent.parent instanceof GoParser.CompositeLitContext) {
            transformToken(NAMED_TYPE_CONSTRUCTOR, context.getStart());
            enterContext(GoBlockContext.NAMED_TYPE_BODY);
        } else if (context.parent instanceof GoParser.InterfaceTypeContext) {
            transformToken(TYPE_CONSTRAINT, context.getStart(), context.getStop());
        }
        super.enterTypeName(context);
    }

    @Override
    public void enterTypeAssertion(GoParser.TypeAssertionContext context) {
        transformToken(TYPE_ASSERTION, context.getStart(), context.getStop());
        super.enterTypeAssertion(context);
    }

    @Override
    public void enterMethodSpec(GoParser.MethodSpecContext context) {
        transformToken(INTERFACE_METHOD, context.getStart(), context.getStop());
        super.enterMethodSpec(context);
    }

    /* CONTROL FLOW KEYWORDS */

    @Override
    public void enterReturnStmt(GoParser.ReturnStmtContext context) {
        transformToken(RETURN, context.getStart(), context.getStop());
        super.enterReturnStmt(context);
    }

    @Override
    public void enterBreakStmt(GoParser.BreakStmtContext context) {
        transformToken(BREAK, context.getStart(), context.getStop());
        super.enterBreakStmt(context);
    }

    @Override
    public void enterContinueStmt(GoParser.ContinueStmtContext context) {
        transformToken(CONTINUE, context.getStart(), context.getStop());
        super.enterContinueStmt(context);
    }

    @Override
    public void enterFallthroughStmt(GoParser.FallthroughStmtContext context) {
        transformToken(FALLTHROUGH, context.getStart(), context.getStop());
        super.enterFallthroughStmt(context);
    }

    @Override
    public void enterGotoStmt(GoParser.GotoStmtContext context) {
        transformToken(GOTO, context.getStart(), context.getStop());
        super.enterGotoStmt(context);
    }

    @Override
    public void enterGoStmt(GoParser.GoStmtContext context) {
        transformToken(GO, context.getStart(), context.getStop());
        super.enterGoStmt(context);
    }

    @Override
    public void enterDeferStmt(GoParser.DeferStmtContext context) {
        transformToken(DEFER, context.getStart(), context.getStop());
        super.enterDeferStmt(context);
    }

    @Override
    public void enterSendStmt(GoParser.SendStmtContext ctx) {
        transformToken(SEND_STATEMENT, ctx.getStart(), ctx.getStop());
        super.enterSendStmt(ctx);
    }

    @Override
    public void enterRecvStmt(GoParser.RecvStmtContext ctx) {
        transformToken(RECEIVE_STATEMENT, ctx.getStart(), ctx.getStop());
        super.enterRecvStmt(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        switch (token.getText()) {
            case "else" -> {
                expectAndLeave(GoBlockContext.IF_BLOCK);
                enterContext(GoBlockContext.ELSE_BLOCK);
            }
            case "{" -> transformToken(getCurrentContext().getBegin(), token);
            case "}" -> transformToken(getCurrentContext().getEnd(), token);
            default -> {
                // do nothing.
            }
        }
        super.visitTerminal(node);
    }

    /**
     * This enumeration provides sets of information regarding different types of nesting structures in Go. Each element is
     * a tuple of a token for the beginning of a block, the end of the block, and optionally, for the elements contained.
     * <p>
     * As the Go parser does not differentiate between different kinds of blocks, a stack of these GoBlockContexts is
     * required to be able to assign the correct token types for each block.
     */
    private enum GoBlockContext {
        ARRAY_BODY(ARRAY_BODY_BEGIN, ARRAY_BODY_END, Optional.of(ARRAY_ELEMENT)),
        STRUCT_BODY(STRUCT_BODY_BEGIN, STRUCT_BODY_END, Optional.of(MEMBER_DECLARATION)),
        MAP_BODY(MAP_BODY_BEGIN, MAP_BODY_END, Optional.of(MAP_ELEMENT)),
        SLICE_BODY(SLICE_BODY_BEGIN, SLICE_BODY_END, Optional.of(SLICE_ELEMENT)),
        NAMED_TYPE_BODY(NAMED_TYPE_BODY_BEGIN, NAMED_TYPE_BODY_END, Optional.of(NAMED_TYPE_ELEMENT)),
        FUNCTION_BODY(FUNCTION_BODY_BEGIN, FUNCTION_BODY_END, Optional.empty()),

        IF_BLOCK(IF_BLOCK_BEGIN, IF_BLOCK_END, Optional.empty()),
        ELSE_BLOCK(ELSE_BLOCK_BEGIN, ELSE_BLOCK_END, Optional.empty()),
        FOR_BLOCK(FOR_BLOCK_BEGIN, FOR_BLOCK_END, Optional.empty()),
        SWITCH_BLOCK(SWITCH_BLOCK_BEGIN, SWITCH_BLOCK_END, Optional.empty()),
        SELECT_CONTEXT(SELECT_BLOCK_BEGIN, SELECT_BLOCK_END, Optional.empty()),
        STATEMENT_BLOCK(STATEMENT_BLOCK_BEGIN, STATEMENT_BLOCK_END, Optional.empty()),
        CASE_BLOCK(CASE_BLOCK_BEGIN, CASE_BLOCK_END, Optional.empty()),
        INTERFACE_BODY(INTERFACE_BLOCK_BEGIN, INTERFACE_BLOCK_END, Optional.empty());

        private final GoTokenType beginTokenType;
        private final GoTokenType endTokenType;
        private final Optional<GoTokenType> elementTokenType;

        GoBlockContext(GoTokenType beginTokenType, GoTokenType endTokenType, Optional<GoTokenType> elementTokenType) {
            this.beginTokenType = beginTokenType;
            this.endTokenType = endTokenType;
            this.elementTokenType = elementTokenType;
        }

        GoTokenType getBegin() {
            return this.beginTokenType;
        }

        GoTokenType getEnd() {
            return this.endTokenType;
        }

        public Optional<GoTokenType> getElement() {
            return this.elementTokenType;
        }
    }
}
