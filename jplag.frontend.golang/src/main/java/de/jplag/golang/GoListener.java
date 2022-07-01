package de.jplag.golang;

import static de.jplag.golang.GoTokenConstants.*;

import java.util.Arrays;
import java.util.Stack;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.golang.grammar.GoParser;
import de.jplag.golang.grammar.GoParserBaseListener;

public class GoListener extends GoParserBaseListener {

    private final GoParserAdapter parserAdapter;
    private final Stack<GoBlockContext> blockContexts;

    public GoListener(GoParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
        blockContexts = new Stack<>();
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the grammar's token given by token.
     * @param tokenType the custom token type that occurred.
     * @param token the corresponding grammar's token
     */
    private void transformToken(int tokenType, Token token) {
        parserAdapter.addToken(tokenType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the current grammatical context given by
     * start and end.
     * @param tokenType the custom token type that occurred.
     * @param start the first Token of the context
     * @param end the last Token of the context
     */
    private void transformToken(int tokenType, Token start, Token end) {
        parserAdapter.addToken(tokenType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    private void enterContext(GoBlockContext context) {
        blockContexts.push(context);
    }

    private void expectAndLeave(GoBlockContext... contexts) {
        GoBlockContext topContext = blockContexts.pop();
        assert Arrays.stream(contexts).anyMatch(context -> context == topContext);
    }

    /* TOP LEVEL STRUCTURES */

    /* STRUCT */

    @Override
    public void enterStructType(GoParser.StructTypeContext context) {
        transformToken(STRUCT_DECLARATION_BEGIN, context.getStart());
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
        transformToken(METHOD_DECLARATION, context.getStart());
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
        transformToken(FUNCTION_PARAMETER, context.getStart(), context.getStop());
        super.enterParameterDecl(context);
    }

    @Override
    public void enterBlock(GoParser.BlockContext context) {
        int tokenType = blockContexts.peek().getBegin();
        transformToken(tokenType, context.getStart());
        super.enterBlock(context);
    }

    @Override
    public void exitBlock(GoParser.BlockContext context) {
        int tokenType = blockContexts.peek().getEnd();
        transformToken(tokenType, context.getStop());
        super.enterBlock(context);
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
    public void enterExprSwitchCase(GoParser.ExprSwitchCaseContext context) {
        transformToken(SWITCH_CASE, context.getStart());
        enterContext(GoBlockContext.CASE_BLOCK);
        super.enterExprSwitchCase(context);
    }

    @Override
    public void exitExprSwitchCase(GoParser.ExprSwitchCaseContext context) {
        expectAndLeave(GoBlockContext.CASE_BLOCK);
        super.exitExprSwitchCase(context);
    }

    /* STATEMENTS */

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
    public void enterArguments(GoParser.ArgumentsContext context) {
        transformToken(INVOCATION, context.getStart(), context.getStop());

        // Arguments consist of ExpressionLists, which consist of Expressions
        // Get all Expressions of all ExpressionLists in this ArgumentsContext
        context.children.stream().filter(child -> child instanceof GoParser.ExpressionListContext)
                .map(child -> (GoParser.ExpressionListContext) child)
                .flatMap(child -> child.children.stream()).filter(child -> child instanceof GoParser.ExpressionContext)
                .map(child -> (GoParser.ExpressionContext) child)
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
    public void enterCompositeLit(GoParser.CompositeLitContext context) {
        transformToken(STRUCT_CONSTRUCTOR, context.getStart());
        super.enterCompositeLit(context);
    }

    @Override
    public void enterKeyedElement(GoParser.KeyedElementContext context) {
        transformToken(STRUCT_VALUE, context.getStart(), context.getStop());
        super.enterKeyedElement(context);
    }

    @Override
    public void enterArrayType(GoParser.ArrayTypeContext context) {
        transformToken(ARRAY_CONSTRUCTOR, context.getStart(), context.getStop());
        super.enterArrayType(context);
    }

    @Override
    public void enterSliceType(GoParser.SliceTypeContext context) {
        transformToken(SLICE_CONSTRUCTOR, context.getStart(), context.getStop());
        super.enterSliceType(context);
    }

    @Override
    public void enterMapType(GoParser.MapTypeContext context) {
        transformToken(MAP_CONSTRUCTOR, context.getStart(), context.getStop());
        super.enterMapType(context);
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
    public void visitTerminal(TerminalNode node) {
        if (node.getSymbol().getText().equals("else")) {
            expectAndLeave(GoBlockContext.IF_BLOCK);
            enterContext(GoBlockContext.ELSE_BLOCK);
        }
        super.visitTerminal(node);
    }

    private enum GoBlockContext {
        STRUCT_BODY(STRUCT_BODY_BEGIN, STRUCT_BODY_END),
        FUNCTION_BODY(FUNCTION_BODY_BEGIN, FUNCTION_BODY_END),
        IF_BLOCK(IF_BLOCK_BEGIN, IF_BLOCK_END),
        ELSE_BLOCK(ELSE_BLOCK_BEGIN, ELSE_BLOCK_END),
        FOR_BLOCK(FOR_BLOCK_BEGIN, FOR_BLOCK_END),
        SWITCH_BLOCK(SWITCH_BLOCK_BEGIN, SWITCH_BLOCK_END),
        CASE_BLOCK(CASE_BLOCK_BEGIN, CASE_BLOCK_END),
        STATEMENT_BLOCK(STATEMENT_BLOCK_BEGIN, STATEMENT_BLOCK_END);

        private final int beginTokenType;
        private final int endTokenType;

        GoBlockContext(int beginTokenType, int endTokenType) {
            this.beginTokenType = beginTokenType;
            this.endTokenType = endTokenType;
        }

        int getBegin() {
            return this.beginTokenType;
        }

        int getEnd() {
            return this.endTokenType;
        }
    }
}
