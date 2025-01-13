package de.jplag.kotlin;

import static de.jplag.kotlin.KotlinTokenType.IMPORT;
import static de.jplag.tokentypes.CodeStructureTokenTypes.CONTEXT_DEFINITION;
import static de.jplag.tokentypes.ExceptionHandlingTokenTypes.TRY;
import static de.jplag.tokentypes.ExceptionHandlingTokenTypes.TRY_END;
import static de.jplag.tokentypes.ImperativeTokenType.CALL;
import static de.jplag.tokentypes.ImperativeTokenType.CASE;
import static de.jplag.tokentypes.ImperativeTokenType.ELSE;
import static de.jplag.tokentypes.ImperativeTokenType.FUNCTION_DEFINITION;
import static de.jplag.tokentypes.ImperativeTokenType.FUNCTION_END;
import static de.jplag.tokentypes.ImperativeTokenType.IF;
import static de.jplag.tokentypes.ImperativeTokenType.IF_END;
import static de.jplag.tokentypes.ImperativeTokenType.LOOP;
import static de.jplag.tokentypes.ImperativeTokenType.LOOP_END;
import static de.jplag.tokentypes.ImperativeTokenType.STRUCTURE_DEFINITION;
import static de.jplag.tokentypes.ImperativeTokenType.STRUCTURE_END;
import static de.jplag.tokentypes.ImperativeTokenType.SWITCH;
import static de.jplag.tokentypes.ImperativeTokenType.SWITCH_END;
import static de.jplag.tokentypes.ImperativeTokenType.VARIABLE_DEFINITION;
import static de.jplag.tokentypes.ObjectOrientationTokens.NEW;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.kotlin.grammar.KotlinParser;
import de.jplag.kotlin.grammar.KotlinParser.AssignmentOperatorContext;
import de.jplag.kotlin.grammar.KotlinParser.CallSuffixContext;
import de.jplag.kotlin.grammar.KotlinParser.CatchStatementContext;
import de.jplag.kotlin.grammar.KotlinParser.ClassBodyContext;
import de.jplag.kotlin.grammar.KotlinParser.ClassDeclarationContext;
import de.jplag.kotlin.grammar.KotlinParser.ClassParameterContext;
import de.jplag.kotlin.grammar.KotlinParser.ConstructorInvocationContext;
import de.jplag.kotlin.grammar.KotlinParser.DoWhileExpressionContext;
import de.jplag.kotlin.grammar.KotlinParser.EnumEntryContext;
import de.jplag.kotlin.grammar.KotlinParser.FinallyStatementContext;
import de.jplag.kotlin.grammar.KotlinParser.ForExpressionContext;
import de.jplag.kotlin.grammar.KotlinParser.FunctionDeclarationContext;
import de.jplag.kotlin.grammar.KotlinParser.FunctionLiteralContext;
import de.jplag.kotlin.grammar.KotlinParser.FunctionValueParameterContext;
import de.jplag.kotlin.grammar.KotlinParser.GetterContext;
import de.jplag.kotlin.grammar.KotlinParser.IfExpressionContext;
import de.jplag.kotlin.grammar.KotlinParser.ImportHeaderContext;
import de.jplag.kotlin.grammar.KotlinParser.ObjectDeclarationContext;
import de.jplag.kotlin.grammar.KotlinParser.PackageHeaderContext;
import de.jplag.kotlin.grammar.KotlinParser.PropertyDeclarationContext;
import de.jplag.kotlin.grammar.KotlinParser.SecondaryConstructorContext;
import de.jplag.kotlin.grammar.KotlinParser.SetterContext;
import de.jplag.kotlin.grammar.KotlinParser.TryExpressionContext;
import de.jplag.kotlin.grammar.KotlinParser.VariableDeclarationContext;
import de.jplag.kotlin.grammar.KotlinParser.WhenConditionContext;
import de.jplag.kotlin.grammar.KotlinParser.WhenExpressionContext;
import de.jplag.kotlin.grammar.KotlinParser.WhileExpressionContext;
import de.jplag.tokentypes.ExceptionHandlingTokenTypes;
import de.jplag.tokentypes.ImperativeTokenType;

class KotlinListener extends AbstractAntlrListener {

    KotlinListener() {
        visit(PackageHeaderContext.class).map(CONTEXT_DEFINITION);
        visit(ImportHeaderContext.class).map(IMPORT);
        visit(ClassDeclarationContext.class).map(STRUCTURE_DEFINITION, STRUCTURE_END);
        visit(ObjectDeclarationContext.class).map(STRUCTURE_DEFINITION, STRUCTURE_END);
        visit(ClassBodyContext.class, rule -> rule.getParent() instanceof ObjectDeclarationContext).mapEnter(VARIABLE_DEFINITION);
        visit(ClassBodyContext.class, rule -> rule.getParent() instanceof ObjectDeclarationContext).mapEnter(ImperativeTokenType.ASSIGNMENT);
        visit(ClassBodyContext.class, rule -> rule.getParent() instanceof ObjectDeclarationContext).mapEnter(NEW);
        visit(KotlinParser.ClassParametersContext.class).map(FUNCTION_DEFINITION, FUNCTION_END);
        visit(KotlinParser.AnonymousInitializerContext.class).map(FUNCTION_DEFINITION, FUNCTION_END);
        visit(ClassParameterContext.class).map(VARIABLE_DEFINITION, ImperativeTokenType.ASSIGNMENT);
        visit(EnumEntryContext.class).map(VARIABLE_DEFINITION);
        visit(SecondaryConstructorContext.class).map(FUNCTION_DEFINITION, FUNCTION_END);
        visit(PropertyDeclarationContext.class).map(VARIABLE_DEFINITION);
        visit(FunctionDeclarationContext.class).map(FUNCTION_DEFINITION, FUNCTION_END);
        visit(GetterContext.class).map(FUNCTION_DEFINITION, FUNCTION_END);
        visit(SetterContext.class).map(FUNCTION_DEFINITION, FUNCTION_END);
        visit(FunctionValueParameterContext.class).map(VARIABLE_DEFINITION);
        visit(FunctionLiteralContext.class,
                rule -> !(rule.getParent().getParent().getParent().getParent().getParent().getParent() instanceof ClassDeclarationContext))
                        .map(FUNCTION_DEFINITION, FUNCTION_END);
        visit(ForExpressionContext.class).map(LOOP, LOOP_END);
        visit(IfExpressionContext.class).map(IF, IF_END);
        visit(WhileExpressionContext.class).map(LOOP, LOOP_END);
        visit(DoWhileExpressionContext.class).map(LOOP, LOOP_END);
        visit(TryExpressionContext.class).map(TRY, TRY_END);
        visit(CatchStatementContext.class).map(ExceptionHandlingTokenTypes.CATCH);
        visit(FinallyStatementContext.class).map(ExceptionHandlingTokenTypes.FINALLY);
        visit(WhenExpressionContext.class).map(SWITCH, SWITCH_END);
        visit(WhenConditionContext.class).map(CASE);
        visit(VariableDeclarationContext.class).map(VARIABLE_DEFINITION);
        visit(ConstructorInvocationContext.class, rule -> !(rule.getParent().getParent().getParent() instanceof ClassDeclarationContext)).map(NEW);
        visit(CallSuffixContext.class, rule -> !(rule.getParent().getParent().getParent().getParent() instanceof ClassDeclarationContext)).map(CALL);
        visit(AssignmentOperatorContext.class).map(ImperativeTokenType.ASSIGNMENT);

        visit(KotlinParser.ELSE).map(ELSE);

        visit(KotlinParser.THROW).map(ExceptionHandlingTokenTypes.THROW);
        visit(KotlinParser.RETURN).map(ImperativeTokenType.RETURN);
        visit(KotlinParser.CONTINUE).map(ImperativeTokenType.CONTINUE);
        visit(KotlinParser.BREAK).map(ImperativeTokenType.BREAK);
        visit(KotlinParser.BREAK_AT).map(ImperativeTokenType.BREAK);
    }
}
