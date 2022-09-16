package de.jplag.kotlin;

import static de.jplag.kotlin.KotlinTokenType.ASSIGNMENT;
import static de.jplag.kotlin.KotlinTokenType.BREAK;
import static de.jplag.kotlin.KotlinTokenType.CATCH;
import static de.jplag.kotlin.KotlinTokenType.CATCH_BODY_END;
import static de.jplag.kotlin.KotlinTokenType.CATCH_BODY_START;
import static de.jplag.kotlin.KotlinTokenType.CLASS_BODY_BEGIN;
import static de.jplag.kotlin.KotlinTokenType.CLASS_BODY_END;
import static de.jplag.kotlin.KotlinTokenType.CLASS_DECLARATION;
import static de.jplag.kotlin.KotlinTokenType.COMPANION_DECLARATION;
import static de.jplag.kotlin.KotlinTokenType.CONSTRUCTOR;
import static de.jplag.kotlin.KotlinTokenType.CONTINUE;
import static de.jplag.kotlin.KotlinTokenType.CONTROL_STRUCTURE_BODY_END;
import static de.jplag.kotlin.KotlinTokenType.CONTROL_STRUCTURE_BODY_START;
import static de.jplag.kotlin.KotlinTokenType.CREATE_OBJECT;
import static de.jplag.kotlin.KotlinTokenType.DO_WHILE_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenType.DO_WHILE_EXPRESSION_START;
import static de.jplag.kotlin.KotlinTokenType.ENUM_CLASS_BODY_BEGIN;
import static de.jplag.kotlin.KotlinTokenType.ENUM_CLASS_BODY_END;
import static de.jplag.kotlin.KotlinTokenType.ENUM_ENTRY;
import static de.jplag.kotlin.KotlinTokenType.FINALLY;
import static de.jplag.kotlin.KotlinTokenType.FINALLY_BODY_END;
import static de.jplag.kotlin.KotlinTokenType.FINALLY_BODY_START;
import static de.jplag.kotlin.KotlinTokenType.FOR_EXPRESSION_BEGIN;
import static de.jplag.kotlin.KotlinTokenType.FOR_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenType.FUNCTION;
import static de.jplag.kotlin.KotlinTokenType.FUNCTION_BODY_BEGIN;
import static de.jplag.kotlin.KotlinTokenType.FUNCTION_BODY_END;
import static de.jplag.kotlin.KotlinTokenType.FUNCTION_INVOCATION;
import static de.jplag.kotlin.KotlinTokenType.FUNCTION_LITERAL_BEGIN;
import static de.jplag.kotlin.KotlinTokenType.FUNCTION_LITERAL_END;
import static de.jplag.kotlin.KotlinTokenType.FUNCTION_PARAMETER;
import static de.jplag.kotlin.KotlinTokenType.GETTER;
import static de.jplag.kotlin.KotlinTokenType.IF_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenType.IF_EXPRESSION_START;
import static de.jplag.kotlin.KotlinTokenType.IMPORT;
import static de.jplag.kotlin.KotlinTokenType.INITIALIZER;
import static de.jplag.kotlin.KotlinTokenType.INITIALIZER_BODY_END;
import static de.jplag.kotlin.KotlinTokenType.INITIALIZER_BODY_START;
import static de.jplag.kotlin.KotlinTokenType.OBJECT_DECLARATION;
import static de.jplag.kotlin.KotlinTokenType.PACKAGE;
import static de.jplag.kotlin.KotlinTokenType.PROPERTY_DECLARATION;
import static de.jplag.kotlin.KotlinTokenType.RETURN;
import static de.jplag.kotlin.KotlinTokenType.SETTER;
import static de.jplag.kotlin.KotlinTokenType.THROW;
import static de.jplag.kotlin.KotlinTokenType.TRY_BODY_END;
import static de.jplag.kotlin.KotlinTokenType.TRY_BODY_START;
import static de.jplag.kotlin.KotlinTokenType.TRY_EXPRESSION;
import static de.jplag.kotlin.KotlinTokenType.TYPE_PARAMETER;
import static de.jplag.kotlin.KotlinTokenType.VARIABLE_DECLARATION;
import static de.jplag.kotlin.KotlinTokenType.WHEN_CONDITION;
import static de.jplag.kotlin.KotlinTokenType.WHEN_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenType.WHEN_EXPRESSION_START;
import static de.jplag.kotlin.KotlinTokenType.WHILE_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenType.WHILE_EXPRESSION_START;

import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.kotlin.grammar.KotlinParser;
import de.jplag.kotlin.grammar.KotlinParserBaseListener;

public class JPlagKotlinListener extends KotlinParserBaseListener {
    private final KotlinParserAdapter parserAdapter;

    public JPlagKotlinListener(KotlinParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the grammar's token given by token.
     * @param tokenType the custom token type that occurred.
     * @param token the corresponding grammar's token
     */
    private void transformToken(KotlinTokenType tokenType, Token token) {
        parserAdapter.addToken(tokenType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the current grammatical context given by
     * start and end.
     * @param tokenType the custom token type that occurred.
     * @param start the first Token of the context
     * @param end the last Token of the context
     */
    private void transformToken(KotlinTokenType tokenType, Token start, Token end) {
        parserAdapter.addToken(tokenType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    @Override
    public void enterPackageHeader(KotlinParser.PackageHeaderContext context) {
        transformToken(PACKAGE, context.getStart(), context.getStop());
        super.enterPackageHeader(context);
    }

    @Override
    public void enterImportHeader(KotlinParser.ImportHeaderContext context) {
        transformToken(IMPORT, context.getStart(), context.getStop());
        super.enterImportHeader(context);
    }

    @Override
    public void enterClassDeclaration(KotlinParser.ClassDeclarationContext context) {
        transformToken(CLASS_DECLARATION, context.getStart());
        super.enterClassDeclaration(context);
    }

    @Override
    public void enterObjectDeclaration(KotlinParser.ObjectDeclarationContext context) {
        transformToken(OBJECT_DECLARATION, context.getStart(), context.getStop());
        super.enterObjectDeclaration(context);
    }

    @Override
    public void enterCompanionObject(KotlinParser.CompanionObjectContext context) {
        transformToken(COMPANION_DECLARATION, context.getStart());
        super.enterCompanionObject(context);
    }

    @Override
    public void enterTypeParameter(KotlinParser.TypeParameterContext context) {
        transformToken(TYPE_PARAMETER, context.getStart(), context.getStop());
        super.enterTypeParameter(context);
    }

    @Override
    public void enterPrimaryConstructor(KotlinParser.PrimaryConstructorContext context) {
        transformToken(CONSTRUCTOR, context.getStart(), context.getStop());
        super.enterPrimaryConstructor(context);
    }

    @Override
    public void enterClassParameter(KotlinParser.ClassParameterContext context) {
        transformToken(PROPERTY_DECLARATION, context.getStart(), context.getStop());
        super.enterClassParameter(context);
    }

    @Override
    public void enterClassBody(KotlinParser.ClassBodyContext context) {
        transformToken(CLASS_BODY_BEGIN, context.getStart());
        super.enterClassBody(context);
    }

    @Override
    public void exitClassBody(KotlinParser.ClassBodyContext context) {
        transformToken(CLASS_BODY_END, context.getStop());
        super.exitClassBody(context);
    }

    @Override
    public void enterEnumClassBody(KotlinParser.EnumClassBodyContext context) {
        transformToken(ENUM_CLASS_BODY_BEGIN, context.getStart());
        super.enterEnumClassBody(context);
    }

    @Override
    public void exitEnumClassBody(KotlinParser.EnumClassBodyContext context) {
        transformToken(ENUM_CLASS_BODY_END, context.getStop());
        super.exitEnumClassBody(context);
    }

    @Override
    public void enterEnumEntry(KotlinParser.EnumEntryContext context) {
        transformToken(ENUM_ENTRY, context.getStart());
        super.enterEnumEntry(context);
    }

    @Override
    public void enterSecondaryConstructor(KotlinParser.SecondaryConstructorContext context) {
        transformToken(CONSTRUCTOR, context.getStart(), context.getStop());
        super.enterSecondaryConstructor(context);
    }

    @Override
    public void enterPropertyDeclaration(KotlinParser.PropertyDeclarationContext context) {
        transformToken(PROPERTY_DECLARATION, context.getStart());
        super.enterPropertyDeclaration(context);
    }

    @Override
    public void enterAnonymousInitializer(KotlinParser.AnonymousInitializerContext context) {
        transformToken(INITIALIZER, context.getStart());
        super.enterAnonymousInitializer(context);
    }

    @Override
    public void enterInitBlock(KotlinParser.InitBlockContext context) {
        transformToken(INITIALIZER_BODY_START, context.getStart());
        super.enterInitBlock(context);
    }

    @Override
    public void exitInitBlock(KotlinParser.InitBlockContext context) {
        transformToken(INITIALIZER_BODY_END, context.getStop());
        super.exitInitBlock(context);
    }

    @Override
    public void enterFunctionDeclaration(KotlinParser.FunctionDeclarationContext context) {
        transformToken(FUNCTION, context.getStart());
        super.enterFunctionDeclaration(context);
    }

    @Override
    public void enterGetter(KotlinParser.GetterContext context) {
        transformToken(GETTER, context.getStart());
        super.enterGetter(context);
    }

    @Override
    public void enterSetter(KotlinParser.SetterContext context) {
        transformToken(SETTER, context.getStart());
        super.enterSetter(context);
    }

    @Override
    public void enterFunctionValueParameter(KotlinParser.FunctionValueParameterContext context) {
        transformToken(FUNCTION_PARAMETER, context.getStart(), context.getStop());
        super.enterFunctionValueParameter(context);
    }

    @Override
    public void enterFunctionBody(KotlinParser.FunctionBodyContext context) {
        transformToken(FUNCTION_BODY_BEGIN, context.getStart());
        super.enterFunctionBody(context);
    }

    @Override
    public void exitFunctionBody(KotlinParser.FunctionBodyContext context) {
        transformToken(FUNCTION_BODY_END, context.getStop());
        super.exitFunctionBody(context);
    }

    @Override
    public void enterFunctionLiteral(KotlinParser.FunctionLiteralContext context) {
        transformToken(FUNCTION_LITERAL_BEGIN, context.getStart());
        super.enterFunctionLiteral(context);
    }

    @Override
    public void exitFunctionLiteral(KotlinParser.FunctionLiteralContext context) {
        transformToken(FUNCTION_LITERAL_END, context.getStop());
        super.exitFunctionLiteral(context);
    }

    @Override
    public void enterForExpression(KotlinParser.ForExpressionContext context) {
        transformToken(FOR_EXPRESSION_BEGIN, context.getStart());
        super.enterForExpression(context);
    }

    @Override
    public void exitForExpression(KotlinParser.ForExpressionContext context) {
        transformToken(FOR_EXPRESSION_END, context.getStop());
        super.exitForExpression(context);
    }

    @Override
    public void enterIfExpression(KotlinParser.IfExpressionContext context) {
        transformToken(IF_EXPRESSION_START, context.getStart());
        super.enterIfExpression(context);
    }

    @Override
    public void exitIfExpression(KotlinParser.IfExpressionContext context) {
        transformToken(IF_EXPRESSION_END, context.getStop());
        super.exitIfExpression(context);
    }

    @Override
    public void enterWhileExpression(KotlinParser.WhileExpressionContext context) {
        transformToken(WHILE_EXPRESSION_START, context.getStart());
        super.enterWhileExpression(context);
    }

    @Override
    public void exitWhileExpression(KotlinParser.WhileExpressionContext context) {
        transformToken(WHILE_EXPRESSION_END, context.getStop());
        super.exitWhileExpression(context);
    }

    @Override
    public void enterDoWhileExpression(KotlinParser.DoWhileExpressionContext context) {
        transformToken(DO_WHILE_EXPRESSION_START, context.getStart());
        super.enterDoWhileExpression(context);
    }

    @Override
    public void exitDoWhileExpression(KotlinParser.DoWhileExpressionContext context) {
        transformToken(DO_WHILE_EXPRESSION_END, context.getStop());
        super.exitDoWhileExpression(context);
    }

    @Override
    public void enterTryExpression(KotlinParser.TryExpressionContext context) {
        transformToken(TRY_EXPRESSION, context.getStart());
        super.enterTryExpression(context);
    }

    @Override
    public void enterTryBody(KotlinParser.TryBodyContext ctx) {
        transformToken(TRY_BODY_START, ctx.getStart());
        super.enterTryBody(ctx);
    }

    @Override
    public void exitTryBody(KotlinParser.TryBodyContext ctx) {
        transformToken(TRY_BODY_END, ctx.getStop());
        super.exitTryBody(ctx);
    }

    @Override
    public void enterCatchStatement(KotlinParser.CatchStatementContext context) {
        transformToken(CATCH, context.getStart());
        super.enterCatchStatement(context);
    }

    @Override
    public void enterCatchBody(KotlinParser.CatchBodyContext context) {
        transformToken(CATCH_BODY_START, context.getStart());
        super.enterCatchBody(context);
    }

    @Override
    public void exitCatchBody(KotlinParser.CatchBodyContext context) {
        transformToken(CATCH_BODY_END, context.getStop());
        super.exitCatchBody(context);
    }

    @Override
    public void enterFinallyStatement(KotlinParser.FinallyStatementContext context) {
        transformToken(FINALLY, context.getStart());
        super.enterFinallyStatement(context);
    }

    @Override
    public void enterFinallyBody(KotlinParser.FinallyBodyContext context) {
        transformToken(FINALLY_BODY_START, context.getStart());
        super.enterFinallyBody(context);
    }

    @Override
    public void exitFinallyBody(KotlinParser.FinallyBodyContext context) {
        transformToken(FINALLY_BODY_END, context.getStop());
        super.exitFinallyBody(context);
    }

    @Override
    public void enterWhenExpression(KotlinParser.WhenExpressionContext context) {
        transformToken(WHEN_EXPRESSION_START, context.getStart());
        super.enterWhenExpression(context);
    }

    @Override
    public void exitWhenExpression(KotlinParser.WhenExpressionContext context) {
        transformToken(WHEN_EXPRESSION_END, context.getStop());
        super.exitWhenExpression(context);
    }

    @Override
    public void enterWhenCondition(KotlinParser.WhenConditionContext context) {
        transformToken(WHEN_CONDITION, context.getStart(), context.getStop());
        super.enterWhenCondition(context);
    }

    @Override
    public void enterControlStructureBody(KotlinParser.ControlStructureBodyContext context) {
        transformToken(CONTROL_STRUCTURE_BODY_START, context.getStart());
        super.enterControlStructureBody(context);
    }

    @Override
    public void exitControlStructureBody(KotlinParser.ControlStructureBodyContext context) {
        transformToken(CONTROL_STRUCTURE_BODY_END, context.getStop());
        super.exitControlStructureBody(context);
    }

    @Override
    public void enterVariableDeclaration(KotlinParser.VariableDeclarationContext context) {
        transformToken(VARIABLE_DECLARATION, context.getStart());
        super.enterVariableDeclaration(context);
    }

    @Override
    public void enterConstructorInvocation(KotlinParser.ConstructorInvocationContext context) {
        transformToken(CREATE_OBJECT, context.getStart(), context.getStop());
        super.enterConstructorInvocation(context);
    }

    @Override
    public void enterCallSuffix(KotlinParser.CallSuffixContext context) {
        transformToken(FUNCTION_INVOCATION, context.getStart(), context.getStop());
        super.enterCallSuffix(context);
    }

    @Override
    public void enterAssignmentOperator(KotlinParser.AssignmentOperatorContext context) {
        transformToken(ASSIGNMENT, context.getStart());
        super.enterAssignmentOperator(context);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        String tokenText = token.getText();
        if (tokenText.contains("@")) {
            tokenText = tokenText.substring(0, tokenText.indexOf("@"));
        }
        Optional<KotlinTokenType> type = switch (tokenText) {
            case "throw" -> Optional.of(THROW);
            case "return" -> Optional.of(RETURN);
            case "continue" -> Optional.of(CONTINUE);
            case "break" -> Optional.of(BREAK);
            default -> Optional.empty();
        };

        type.ifPresent(tokenType -> transformToken(tokenType, token));
    }

}
