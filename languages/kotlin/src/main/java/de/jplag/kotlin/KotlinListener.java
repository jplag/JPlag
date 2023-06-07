package de.jplag.kotlin;

import java.io.File;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.TokenCollector;
import de.jplag.kotlin.grammar.KotlinParser;

public class KotlinListener extends AbstractAntlrListener {
    public KotlinListener(TokenCollector collector, File currentFile) {
        super(collector, currentFile);

        this.createRangeMapping(KotlinParser.PackageHeaderContext.class, KotlinTokenType.PACKAGE);
        this.createRangeMapping(KotlinParser.ImportHeaderContext.class, KotlinTokenType.IMPORT);
        this.createStartMapping(KotlinParser.ClassDeclarationContext.class, KotlinTokenType.CLASS_DECLARATION);
        this.createRangeMapping(KotlinParser.ObjectDeclarationContext.class, KotlinTokenType.OBJECT_DECLARATION);
        this.createRangeMapping(KotlinParser.CompanionObjectContext.class, KotlinTokenType.COMPANION_DECLARATION);

        this.createRangeMapping(KotlinParser.TypeParameterContext.class, KotlinTokenType.TYPE_PARAMETER);
        this.createRangeMapping(KotlinParser.PrimaryConstructorContext.class, KotlinTokenType.CONSTRUCTOR);
        this.createRangeMapping(KotlinParser.ClassParameterContext.class, KotlinTokenType.PROPERTY_DECLARATION);

        this.createStartStopMapping(KotlinParser.ClassBodyContext.class, KotlinTokenType.CLASS_BODY_BEGIN, KotlinTokenType.CLASS_BODY_END);
        this.createStartStopMapping(KotlinParser.EnumClassBodyContext.class, KotlinTokenType.ENUM_CLASS_BODY_BEGIN,
                KotlinTokenType.ENUM_CLASS_BODY_END);
        this.createStartMapping(KotlinParser.EnumEntryContext.class, KotlinTokenType.ENUM_ENTRY);
        this.createRangeMapping(KotlinParser.SecondaryConstructorContext.class, KotlinTokenType.CONSTRUCTOR);
        this.createStartMapping(KotlinParser.PropertyDeclarationContext.class, KotlinTokenType.PROPERTY_DECLARATION);
        this.createStartMapping(KotlinParser.AnonymousInitializerContext.class, KotlinTokenType.INITIALIZER);
        this.createStartStopMapping(KotlinParser.InitBlockContext.class, KotlinTokenType.INITIALIZER_BODY_START,
                KotlinTokenType.INITIALIZER_BODY_END);
        this.createStartMapping(KotlinParser.FunctionDeclarationContext.class, KotlinTokenType.FUNCTION);
        this.createStartMapping(KotlinParser.GetterContext.class, KotlinTokenType.GETTER);
        this.createStartMapping(KotlinParser.SetterContext.class, KotlinTokenType.SETTER);
        this.createRangeMapping(KotlinParser.FunctionValueParameterContext.class, KotlinTokenType.FUNCTION_PARAMETER);
        this.createStartStopMapping(KotlinParser.FunctionBodyContext.class, KotlinTokenType.FUNCTION_BODY_BEGIN, KotlinTokenType.FUNCTION_BODY_END);
        this.createStartStopMapping(KotlinParser.FunctionLiteralContext.class, KotlinTokenType.FUNCTION_LITERAL_BEGIN,
                KotlinTokenType.FUNCTION_LITERAL_END);
        this.createStartStopMapping(KotlinParser.ForExpressionContext.class, KotlinTokenType.FOR_EXPRESSION_BEGIN,
                KotlinTokenType.FOR_EXPRESSION_END);
        this.createStartStopMapping(KotlinParser.IfExpressionContext.class, KotlinTokenType.IF_EXPRESSION_START, KotlinTokenType.IF_EXPRESSION_END); // TODO
                                                                                                                                                     // inconsistent
                                                                                                                                                     // name
                                                                                                                                                     // |
                                                                                                                                                     // start
                                                                                                                                                     // instead
                                                                                                                                                     // of
                                                                                                                                                     // begin
        this.createStartStopMapping(KotlinParser.WhileExpressionContext.class, KotlinTokenType.WHILE_EXPRESSION_START,
                KotlinTokenType.WHILE_EXPRESSION_END);
        this.createStartStopMapping(KotlinParser.DoWhileExpressionContext.class, KotlinTokenType.DO_WHILE_EXPRESSION_START,
                KotlinTokenType.DO_WHILE_EXPRESSION_END); // TODO does separating do-while from while allow obfuscating
        this.createStartMapping(KotlinParser.TryExpressionContext.class, KotlinTokenType.TRY_EXPRESSION);
        this.createStartStopMapping(KotlinParser.TryBodyContext.class, KotlinTokenType.TRY_BODY_START, KotlinTokenType.TRY_BODY_END);
        this.createStartMapping(KotlinParser.CatchStatementContext.class, KotlinTokenType.CATCH);
        this.createStartStopMapping(KotlinParser.CatchBodyContext.class, KotlinTokenType.CATCH_BODY_START, KotlinTokenType.CATCH_BODY_END);
        this.createStartMapping(KotlinParser.FinallyStatementContext.class, KotlinTokenType.FINALLY);
        this.createStartStopMapping(KotlinParser.FinallyBodyContext.class, KotlinTokenType.FINALLY_BODY_START, KotlinTokenType.FINALLY_BODY_END);
        this.createStartStopMapping(KotlinParser.WhenExpressionContext.class, KotlinTokenType.WHEN_EXPRESSION_START,
                KotlinTokenType.WHEN_EXPRESSION_END);
        this.createStartMapping(KotlinParser.WhenConditionContext.class, KotlinTokenType.WHEN_CONDITION);
        this.createStartStopMapping(KotlinParser.ControlStructureBodyContext.class, KotlinTokenType.CONTROL_STRUCTURE_BODY_START,
                KotlinTokenType.CONTROL_STRUCTURE_BODY_END);
        this.createStartMapping(KotlinParser.VariableDeclarationContext.class, KotlinTokenType.VARIABLE_DECLARATION);
        this.createRangeMapping(KotlinParser.ConstructorInvocationContext.class, KotlinTokenType.CREATE_OBJECT);
        this.createRangeMapping(KotlinParser.CallSuffixContext.class, KotlinTokenType.FUNCTION_INVOCATION);
        this.createStartMapping(KotlinParser.AssignmentOperatorContext.class, KotlinTokenType.ASSIGNMENT);

        this.createTerminalMapping("throw(@.*)?", KotlinTokenType.THROW);
        this.createTerminalMapping("return(@.*)?", KotlinTokenType.RETURN);
        this.createTerminalMapping("continue(@.*)?", KotlinTokenType.CONTINUE);
        this.createTerminalMapping("break(@.*)?", KotlinTokenType.BREAK);
    }
}
