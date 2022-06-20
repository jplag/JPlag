package de.jplag.kotlin;

import static de.jplag.kotlin.KotlinTokenConstants.*;

import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.kotlin.grammar.KotlinParser;
import de.jplag.kotlin.grammar.KotlinParserBaseListener;

public class KotlinListener extends KotlinParserBaseListener {
    private final KotlinParserAdapter parserAdapter;

    public KotlinListener(KotlinParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
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
        parserAdapter.addToken(tokenType, start.getLine(), start.getCharPositionInLine(), end.getStopIndex() - start.getStartIndex() + 1);
    }

    @Override
    public void enterPackageHeader(KotlinParser.PackageHeaderContext ctx) {
        transformToken(PACKAGE, ctx.getStart(), ctx.getStop());
        super.enterPackageHeader(ctx);
    }

    @Override
    public void enterImportHeader(KotlinParser.ImportHeaderContext ctx) {
        transformToken(IMPORT, ctx.getStart(), ctx.getStop());
        super.enterImportHeader(ctx);
    }

    @Override
    public void enterClassDeclaration(KotlinParser.ClassDeclarationContext ctx) {
        transformToken(CLASS_DECLARATION, ctx.getStart(), ctx.getStop());
        super.enterClassDeclaration(ctx);
    }

    @Override
    public void enterObjectDeclaration(KotlinParser.ObjectDeclarationContext ctx) {
        transformToken(OBJECT_DECLARATION, ctx.getStart(), ctx.getStop());
        super.enterObjectDeclaration(ctx);
    }

    @Override
    public void enterCompanionObject(KotlinParser.CompanionObjectContext ctx) {
        transformToken(COMPANION_DECLARATION, ctx.getStart(), ctx.getStop());
        super.enterCompanionObject(ctx);
    }

    @Override
    public void enterTypeParameter(KotlinParser.TypeParameterContext ctx) {
        transformToken(TYPE_PARAMETER, ctx.getStart(), ctx.getStop());
        super.enterTypeParameter(ctx);
    }

    @Override
    public void enterPrimaryConstructor(KotlinParser.PrimaryConstructorContext ctx) {
        transformToken(CONSTRUCTOR, ctx.getStart(), ctx.getStop());
        super.enterPrimaryConstructor(ctx);
    }

    @Override
    public void enterClassParameter(KotlinParser.ClassParameterContext ctx) {
        transformToken(PROPERTY_DECLARATION, ctx.getStart(), ctx.getStop());
        super.enterClassParameter(ctx);
    }

    @Override
    public void enterClassBody(KotlinParser.ClassBodyContext ctx) {
        transformToken(CLASS_BODY_BEGIN, ctx.getStart());
        super.enterClassBody(ctx);
    }

    @Override
    public void exitClassBody(KotlinParser.ClassBodyContext ctx) {
        transformToken(CLASS_BODY_END, ctx.getStop());
        super.exitClassBody(ctx);
    }

    @Override
    public void enterSecondaryConstructor(KotlinParser.SecondaryConstructorContext ctx) {
        transformToken(CONSTRUCTOR, ctx.getStart(), ctx.getStop());
        super.enterSecondaryConstructor(ctx);
    }

    @Override
    public void enterPropertyDeclaration(KotlinParser.PropertyDeclarationContext ctx) {
        transformToken(PROPERTY_DECLARATION, ctx.getStart(), ctx.getStop());
        super.enterPropertyDeclaration(ctx);
    }

    @Override
    public void enterAnonymousInitializer(KotlinParser.AnonymousInitializerContext ctx) {
        transformToken(INITIALIZER, ctx.getStart());
        super.enterAnonymousInitializer(ctx);
    }

    @Override
    public void enterFunctionDeclaration(KotlinParser.FunctionDeclarationContext ctx) {
        transformToken(FUNCTION, ctx.getStart());
        super.enterFunctionDeclaration(ctx);
    }

    @Override
    public void enterGetter(KotlinParser.GetterContext ctx) {
        transformToken(GETTER, ctx.getStart());
        super.enterGetter(ctx);
    }

    @Override
    public void enterSetter(KotlinParser.SetterContext ctx) {
        transformToken(SETTER, ctx.getStart());
        super.enterSetter(ctx);
    }

    @Override
    public void enterFunctionValueParameter(KotlinParser.FunctionValueParameterContext ctx) {
        transformToken(FUNCTION_PARAMETER, ctx.getStart(), ctx.getStop());
        super.enterFunctionValueParameter(ctx);
    }

    @Override
    public void enterFunctionBody(KotlinParser.FunctionBodyContext ctx) {
        transformToken(FUNCTION_BODY_BEGIN, ctx.getStart());
        super.enterFunctionBody(ctx);
        super.enterFunctionBody(ctx);
    }

    @Override
    public void exitFunctionBody(KotlinParser.FunctionBodyContext ctx) {
        transformToken(FUNCTION_BODY_END, ctx.getStop());
        super.exitFunctionBody(ctx);
    }

    @Override
    public void enterFunctionLiteral(KotlinParser.FunctionLiteralContext ctx) {
        transformToken(FUNCTION_LITERAL_BEGIN, ctx.getStart());
        super.enterFunctionLiteral(ctx);
    }

    @Override
    public void exitFunctionLiteral(KotlinParser.FunctionLiteralContext ctx) {
        transformToken(FUNCTION_LITERAL_END, ctx.getStop());
        super.exitFunctionLiteral(ctx);
    }

    @Override
    public void enterBlock(KotlinParser.BlockContext ctx) {
        transformToken(BLOCK_BEGIN, ctx.getStart());
        super.enterBlock(ctx);
    }

    @Override
    public void exitBlock(KotlinParser.BlockContext ctx) {
        transformToken(BLOCK_END, ctx.getStop());
        super.exitBlock(ctx);
    }

    @Override
    public void enterForExpression(KotlinParser.ForExpressionContext ctx) {
        transformToken(FOR_EXPRESSION_BEGIN, ctx.getStart());
        super.enterForExpression(ctx);
    }

    @Override
    public void exitForExpression(KotlinParser.ForExpressionContext ctx) {
        transformToken(FOR_EXPRESSION_END, ctx.getStop());
        super.exitForExpression(ctx);
    }

    @Override
    public void enterIfExpression(KotlinParser.IfExpressionContext ctx) {
        transformToken(IF_EXPRESSION_START, ctx.getStart());
        super.enterIfExpression(ctx);
    }

    @Override
    public void exitIfExpression(KotlinParser.IfExpressionContext ctx) {
        transformToken(IF_EXPRESSION_END, ctx.getStop());
        super.exitIfExpression(ctx);
    }

    @Override
    public void enterWhileExpression(KotlinParser.WhileExpressionContext ctx) {
        transformToken(WHILE_EXPRESSION_START, ctx.getStart());
        super.enterWhileExpression(ctx);
    }

    @Override
    public void exitWhileExpression(KotlinParser.WhileExpressionContext ctx) {
        transformToken(WHILE_EXPRESSION_END, ctx.getStop());
        super.exitWhileExpression(ctx);
    }

    @Override
    public void enterDoWhileExpression(KotlinParser.DoWhileExpressionContext ctx) {
        transformToken(DO_WHILE_EXPRESSION_START, ctx.getStart());
        super.enterDoWhileExpression(ctx);
    }

    @Override
    public void exitDoWhileExpression(KotlinParser.DoWhileExpressionContext ctx) {
        transformToken(DO_WHILE_EXPRESSION_END, ctx.getStop());
        super.exitDoWhileExpression(ctx);
    }

    @Override
    public void enterTryExpression(KotlinParser.TryExpressionContext ctx) {
        transformToken(TRY_EXPRESSION_START, ctx.getStart());
        super.enterTryExpression(ctx);
    }

    @Override
    public void exitTryExpression(KotlinParser.TryExpressionContext ctx) {
        transformToken(TRY_EXPRESSION_END, ctx.getStop());
        super.exitTryExpression(ctx);
    }

    @Override
    public void enterCatchBlock(KotlinParser.CatchBlockContext ctx) {
        transformToken(CATCH, ctx.getStart());
        super.enterCatchBlock(ctx);
    }

    @Override
    public void enterFinallyBlock(KotlinParser.FinallyBlockContext ctx) {
        transformToken(FINALLY, ctx.getStart());
        super.enterFinallyBlock(ctx);
    }

    @Override
    public void enterWhenExpression(KotlinParser.WhenExpressionContext ctx) {
        transformToken(WHEN_EXPRESSION_START, ctx.getStart());
        super.enterWhenExpression(ctx);
    }

    @Override
    public void exitWhenExpression(KotlinParser.WhenExpressionContext ctx) {
        transformToken(WHEN_EXPRESSION_END, ctx.getStop());
        super.exitWhenExpression(ctx);
    }

    @Override
    public void enterWhenCondition(KotlinParser.WhenConditionContext ctx) {
        transformToken(WHEN_CONDITION, ctx.getStart(), ctx.getStop());
        super.enterWhenCondition(ctx);
    }

    @Override
    public void enterControlStructureBody(KotlinParser.ControlStructureBodyContext ctx) {
        transformToken(DO, ctx.getStart(), ctx.getStop());
        super.enterControlStructureBody(ctx);
    }

    @Override
    public void enterVariableDeclaration(KotlinParser.VariableDeclarationContext ctx) {
        transformToken(VARIABLE_DECLARATION, ctx.getStart());
        super.enterVariableDeclaration(ctx);
    }

    @Override
    public void enterConstructorInvocation(KotlinParser.ConstructorInvocationContext ctx) {
        transformToken(CREATE_OBJECT, ctx.getStart(), ctx.getStop());
        super.enterConstructorInvocation(ctx);
    }

    @Override
    public void enterCallSuffix(KotlinParser.CallSuffixContext ctx) {
        transformToken(FUNCTION_INVOCATION, ctx.getStart(), ctx.getStop());
        super.enterCallSuffix(ctx);
    }

    @Override
    public void enterAssignmentOperator(KotlinParser.AssignmentOperatorContext ctx) {
        transformToken(ASSIGNMENT, ctx.getStart());
        super.enterAssignmentOperator(ctx);
    }

    @Override
    public void enterStringLiteral(KotlinParser.StringLiteralContext ctx) {
        transformToken(STRING, ctx.getStart(), ctx.getStop());
        super.enterStringLiteral(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String tokenText = token.getText();
        if (tokenText.contains("@")) {
            tokenText = tokenText.substring(0, tokenText.indexOf("@"));
        }
        Optional<Integer> type = switch (tokenText) {
            case "throw" -> Optional.of(THROW);
            case "return", "return@" -> Optional.of(RETURN);
            case "continue", "continue@" -> Optional.of(CONTINUE);
            case "break", "break@" -> Optional.of(BREAK);
            case "++" -> Optional.of(INCR);
            case "--" -> Optional.of(DECR);
            default -> Optional.empty();
        };

        type.ifPresent(tokenType -> transformToken(tokenType, token));
    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }

}
