package de.jplag.go;

import static de.jplag.go.GoTokenConstants.*;

import java.util.Arrays;
import java.util.Stack;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.go.grammar.GoParser;
import de.jplag.go.grammar.GoParserBaseListener;

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
        assert Arrays.stream(contexts).anyMatch(ctx -> ctx == topContext);
    }

    /* TOP LEVEL STRUCTURES */

    /* STRUCT */

    @Override
    public void enterStructType(GoParser.StructTypeContext ctx) {
        transformToken(STRUCT_DECLARATION_BEGIN, ctx.getStart());
        enterContext(GoBlockContext.STRUCT_BODY);
        super.enterStructType(ctx);
    }

    @Override
    public void exitStructType(GoParser.StructTypeContext ctx) {
        expectAndLeave(GoBlockContext.STRUCT_BODY);
        super.exitStructType(ctx);
    }

    @Override
    public void enterFieldDecl(GoParser.FieldDeclContext ctx) {
        transformToken(MEMBER_DECLARATION, ctx.getStart(), ctx.getStop());
        super.enterFieldDecl(ctx);
    }

    /* FUNCTION */

    @Override
    public void enterFunctionDecl(GoParser.FunctionDeclContext ctx) {
        transformToken(FUNCTION_DECLARATION, ctx.getStart());
        enterContext(GoBlockContext.FUNCTION_BODY);
        super.enterFunctionDecl(ctx);
    }

    @Override
    public void exitFunctionDecl(GoParser.FunctionDeclContext ctx) {
        expectAndLeave(GoBlockContext.FUNCTION_BODY);
        super.exitFunctionDecl(ctx);
    }

    @Override
    public void enterMethodDecl(GoParser.MethodDeclContext ctx) {
        transformToken(METHOD_DECLARATION, ctx.getStart());
        enterContext(GoBlockContext.FUNCTION_BODY);
        super.enterMethodDecl(ctx);
    }

    @Override
    public void exitMethodDecl(GoParser.MethodDeclContext ctx) {
        expectAndLeave(GoBlockContext.FUNCTION_BODY);
        super.exitMethodDecl(ctx);
    }

    @Override
    public void enterParameterDecl(GoParser.ParameterDeclContext ctx) {
        transformToken(FUNCTION_PARAMETER, ctx.getStart(), ctx.getStop());
        super.enterParameterDecl(ctx);
    }

    @Override
    public void enterBlock(GoParser.BlockContext ctx) {
        int tokenType = blockContexts.peek().getBegin();
        transformToken(tokenType, ctx.getStart());
        super.enterBlock(ctx);
    }

    @Override
    public void exitBlock(GoParser.BlockContext ctx) {
        int tokenType = blockContexts.peek().getEnd();
        transformToken(tokenType, ctx.getStop());
        super.enterBlock(ctx);
    }

    /* CONTROL FLOW STATEMENTS */

    @Override
    public void enterIfStmt(GoParser.IfStmtContext ctx) {
        transformToken(IF_STATEMENT, ctx.getStart());
        enterContext(GoBlockContext.IF_BLOCK);
        super.enterIfStmt(ctx);
    }

    @Override
    public void exitIfStmt(GoParser.IfStmtContext ctx) {
        expectAndLeave(GoBlockContext.IF_BLOCK, GoBlockContext.ELSE_BLOCK);
        super.exitIfStmt(ctx);
    }

    @Override
    public void enterForStmt(GoParser.ForStmtContext ctx) {
        transformToken(FOR_STATEMENT, ctx.getStart());
        enterContext(GoBlockContext.FOR_BLOCK);
        super.enterForStmt(ctx);
    }

    @Override
    public void exitForStmt(GoParser.ForStmtContext ctx) {
        expectAndLeave(GoBlockContext.FOR_BLOCK);
        super.exitForStmt(ctx);
    }

    @Override
    public void enterSwitchStmt(GoParser.SwitchStmtContext ctx) {
        transformToken(SWITCH_STATEMENT, ctx.getStart());
        enterContext(GoBlockContext.SWITCH_BLOCK);
        super.enterSwitchStmt(ctx);
    }

    @Override
    public void exitSwitchStmt(GoParser.SwitchStmtContext ctx) {
        expectAndLeave(GoBlockContext.SWITCH_BLOCK);
        super.exitSwitchStmt(ctx);
    }

    @Override
    public void enterExprSwitchCase(GoParser.ExprSwitchCaseContext ctx) {
        transformToken(SWITCH_CASE, ctx.getStart());
        enterContext(GoBlockContext.CASE_BLOCK);
        super.enterExprSwitchCase(ctx);
    }

    @Override
    public void exitExprSwitchCase(GoParser.ExprSwitchCaseContext ctx) {
        expectAndLeave(GoBlockContext.CASE_BLOCK);
        super.exitExprSwitchCase(ctx);
    }

    /* STATEMENTS */

    @Override
    public void enterFunctionLit(GoParser.FunctionLitContext ctx) {
        transformToken(FUNCTION_LITERAL, ctx.getStart());
        enterContext(GoBlockContext.FUNCTION_BODY);
        super.enterFunctionLit(ctx);
    }

    @Override
    public void exitFunctionLit(GoParser.FunctionLitContext ctx) {
        expectAndLeave(GoBlockContext.FUNCTION_BODY);
        super.exitFunctionLit(ctx);
    }

    @Override
    public void enterAssignment(GoParser.AssignmentContext ctx) {
        transformToken(ASSIGNMENT, ctx.getStart(), ctx.getStop());
        super.enterAssignment(ctx);
    }

    @Override
    public void enterArguments(GoParser.ArgumentsContext ctx) {
        transformToken(INVOCATION, ctx.getStart(), ctx.getStop());

        // Arguments consist of ExpressionLists, which consist of Expressions
        // Get all Expressions of all ExpressionLists in this ArgumentsContext
        ctx.children.stream().filter(child -> child instanceof GoParser.ExpressionListContext).map(child -> (GoParser.ExpressionListContext) child)
                .flatMap(child -> child.children.stream()).filter(child -> child instanceof GoParser.ExpressionContext)
                .map(child -> (GoParser.ExpressionContext) child).forEachOrdered(arg -> transformToken(ARGUMENT, arg.getStart(), arg.getStop()));
        super.enterArguments(ctx);
    }

    @Override
    public void enterStatement(GoParser.StatementContext ctx) {
        enterContext(GoBlockContext.STATEMENT_BLOCK);
        super.enterStatement(ctx);
    }

    @Override
    public void exitStatement(GoParser.StatementContext ctx) {
        expectAndLeave(GoBlockContext.STATEMENT_BLOCK);
        super.exitStatement(ctx);
    }

    /* OBJECT CREATION */

    @Override
    public void enterCompositeLit(GoParser.CompositeLitContext ctx) {
        transformToken(STRUCT_CONSTRUCTOR, ctx.getStart());
        super.enterCompositeLit(ctx);
    }

    @Override
    public void enterKeyedElement(GoParser.KeyedElementContext ctx) {
        transformToken(STRUCT_VALUE, ctx.getStart(), ctx.getStop());
        super.enterKeyedElement(ctx);
    }

    @Override
    public void enterArrayType(GoParser.ArrayTypeContext ctx) {
        transformToken(ARRAY_CONSTRUCTOR, ctx.getStart(), ctx.getStop());
        super.enterArrayType(ctx);
    }

    @Override
    public void enterSliceType(GoParser.SliceTypeContext ctx) {
        transformToken(SLICE_CONSTRUCTOR, ctx.getStart(), ctx.getStop());
        super.enterSliceType(ctx);
    }

    @Override
    public void enterMapType(GoParser.MapTypeContext ctx) {
        transformToken(MAP_CONSTRUCTOR, ctx.getStart(), ctx.getStop());
        super.enterMapType(ctx);
    }

    /* CONTROL FLOW KEYWORDS */

    @Override
    public void enterReturnStmt(GoParser.ReturnStmtContext ctx) {
        transformToken(RETURN, ctx.getStart(), ctx.getStop());
        super.enterReturnStmt(ctx);
    }

    @Override
    public void enterBreakStmt(GoParser.BreakStmtContext ctx) {
        transformToken(BREAK, ctx.getStart(), ctx.getStop());
        super.enterBreakStmt(ctx);
    }

    @Override
    public void enterContinueStmt(GoParser.ContinueStmtContext ctx) {
        transformToken(CONTINUE, ctx.getStart(), ctx.getStop());
        super.enterContinueStmt(ctx);
    }

    @Override
    public void enterGotoStmt(GoParser.GotoStmtContext ctx) {
        transformToken(GOTO, ctx.getStart(), ctx.getStop());
        super.enterGotoStmt(ctx);
    }

    @Override
    public void enterGoStmt(GoParser.GoStmtContext ctx) {
        transformToken(GO, ctx.getStart(), ctx.getStop());
        super.enterGoStmt(ctx);
    }

    @Override
    public void enterDeferStmt(GoParser.DeferStmtContext ctx) {
        transformToken(DEFER, ctx.getStart(), ctx.getStop());
        super.enterDeferStmt(ctx);
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
