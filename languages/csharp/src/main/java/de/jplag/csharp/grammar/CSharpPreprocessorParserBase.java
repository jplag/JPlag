package de.jplag.csharp.grammar;

import java.util.HashSet;
import java.util.Objects;
import java.util.Stack;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;

import de.jplag.csharp.grammar.CSharpPreprocessorParser.PreprocessorConditionalContext;
import de.jplag.csharp.grammar.CSharpPreprocessorParser.PreprocessorDeclarationContext;
import de.jplag.csharp.grammar.CSharpPreprocessorParser.PreprocessorDiagnosticContext;
import de.jplag.csharp.grammar.CSharpPreprocessorParser.PreprocessorLineContext;
import de.jplag.csharp.grammar.CSharpPreprocessorParser.PreprocessorNullableContext;
import de.jplag.csharp.grammar.CSharpPreprocessorParser.PreprocessorPragmaContext;
import de.jplag.csharp.grammar.CSharpPreprocessorParser.PreprocessorRegionContext;
import de.jplag.csharp.grammar.CSharpPreprocessorParser.Preprocessor_expressionContext;

/**
 * This class was taken from <a href="https://github.com/antlr/grammars-v4/tree/master/csharp">antlr/grammars-v4</a>. It
 * was originally written by Ken Domino. Note that this class is licensed under Eclipse Public License - v 1.0.
 */
abstract class CSharpPreprocessorParserBase extends Parser {
    private static final String FALSE = Boolean.toString(false);
    private static final String TRUE = Boolean.toString(true);
    private static final String DEBUG = "DEBUG";

    private final Stack<Boolean> conditions = new Stack<>();
    private final HashSet<String> conditionalSymbols = new HashSet<>();

    protected CSharpPreprocessorParserBase(TokenStream input) {
        super(input);
        conditions.push(true);
        conditionalSymbols.add(DEBUG);
    }

    protected Boolean allConditions() {
        return conditions.stream().allMatch(it -> it);
    }

    protected void onPreprocessorDirectiveDefine() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorDeclarationContext context = (PreprocessorDeclarationContext) ruleContext;
        conditionalSymbols.add(context.CONDITIONAL_SYMBOL().getText());
        context.value = allConditions();
    }

    protected void onPreprocessorDirectiveUndef() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorDeclarationContext context = (PreprocessorDeclarationContext) ruleContext;
        conditionalSymbols.remove(context.CONDITIONAL_SYMBOL().getText());
        context.value = allConditions();
    }

    protected void onPreprocessorDirectiveIf() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorConditionalContext context = (PreprocessorConditionalContext) ruleContext;
        context.value = context.expr.value.equals(TRUE) && allConditions();
        conditions.push(context.expr.value.equals(TRUE));
    }

    protected void onPreprocessorDirectiveElif() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorConditionalContext context = (PreprocessorConditionalContext) ruleContext;
        if (Boolean.FALSE.equals(conditions.peek())) {
            conditions.pop();
            context.value = context.expr.value.equals(TRUE) && allConditions();
            conditions.push(context.expr.value.equals(TRUE));
        } else {
            context.value = false;
        }
    }

    protected void onPreprocessorDirectiveElse() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorConditionalContext context = (PreprocessorConditionalContext) ruleContext;
        if (Boolean.FALSE.equals(conditions.peek())) {
            conditions.pop();
            context.value = allConditions();
            conditions.push(true);
        } else {
            context.value = false;
        }
    }

    protected void onPreprocessorDirectiveEndif() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorConditionalContext context = (PreprocessorConditionalContext) ruleContext;
        conditions.pop();
        context.value = conditions.peek();
    }

    protected void onPreprocessorDirectiveLine() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorLineContext context = (PreprocessorLineContext) ruleContext;
        context.value = allConditions();
    }

    protected void onPreprocessorDirectiveError() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorDiagnosticContext context = (PreprocessorDiagnosticContext) ruleContext;
        context.value = allConditions();
    }

    protected void onPreprocessorDirectiveWarning() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorDiagnosticContext context = (PreprocessorDiagnosticContext) ruleContext;
        context.value = allConditions();
    }

    protected void onPreprocessorDirectiveRegion() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorRegionContext context = (PreprocessorRegionContext) ruleContext;
        context.value = allConditions();
    }

    protected void onPreprocessorDirectiveEndregion() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorRegionContext context = (PreprocessorRegionContext) ruleContext;
        context.value = allConditions();
    }

    protected void onPreprocessorDirectivePragma() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorPragmaContext context = (PreprocessorPragmaContext) ruleContext;
        context.value = allConditions();
    }

    protected void onPreprocessorDirectiveNullable() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorNullableContext context = (PreprocessorNullableContext) ruleContext;
        context.value = allConditions();
    }

    protected void onPreprocessorExpressionTrue() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = TRUE;
    }

    protected void onPreprocessorExpressionFalse() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = FALSE;
    }

    protected void onPreprocessorExpressionConditionalSymbol() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = conditionalSymbols.contains(context.CONDITIONAL_SYMBOL().getText()) ? TRUE : FALSE;
    }

    protected void onPreprocessorExpressionConditionalOpenParens() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = context.expr.value;
    }

    protected void onPreprocessorExpressionConditionalBang() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = context.expr.value.equals(TRUE) ? FALSE : TRUE;
    }

    protected void onPreprocessorExpressionConditionalEq() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = (Objects.equals(context.expr1.value, context.expr2.value) ? TRUE : FALSE);
    }

    protected void onPreprocessorExpressionConditionalNe() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = (!Objects.equals(context.expr1.value, context.expr2.value) ? TRUE : FALSE);
    }

    protected void onPreprocessorExpressionConditionalAnd() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = (context.expr1.value.equals(TRUE) && context.expr2.value.equals(TRUE) ? TRUE : FALSE);
    }

    protected void onPreprocessorExpressionConditionalOr() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = (context.expr1.value.equals(TRUE) || context.expr2.value.equals(TRUE) ? TRUE : FALSE);
    }
}
