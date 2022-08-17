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

    private Stack<Boolean> conditions = new Stack<>();
    private HashSet<String> ConditionalSymbols = new HashSet<>();

    protected CSharpPreprocessorParserBase(TokenStream input) {
        super(input);
        conditions.push(true);
        ConditionalSymbols.add(DEBUG);
    }

    protected Boolean AllConditions() {
        return conditions.stream().allMatch(it -> it);
    }

    protected void OnPreprocessorDirectiveDefine() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorDeclarationContext context = (PreprocessorDeclarationContext) ruleContext;
        ConditionalSymbols.add(context.CONDITIONAL_SYMBOL().getText());
        context.value = AllConditions();
    }

    protected void OnPreprocessorDirectiveUndef() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorDeclarationContext context = (PreprocessorDeclarationContext) ruleContext;
        ConditionalSymbols.remove(context.CONDITIONAL_SYMBOL().getText());
        context.value = AllConditions();
    }

    protected void OnPreprocessorDirectiveIf() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorConditionalContext context = (PreprocessorConditionalContext) ruleContext;
        context.value = context.expr.value.equals(TRUE) && AllConditions();
        conditions.push(context.expr.value.equals(TRUE));
    }

    protected void OnPreprocessorDirectiveElif() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorConditionalContext context = (PreprocessorConditionalContext) ruleContext;
        if (Boolean.FALSE.equals(conditions.peek())) {
            conditions.pop();
            context.value = context.expr.value.equals(TRUE) && AllConditions();
            conditions.push(context.expr.value.equals(TRUE));
        } else {
            context.value = false;
        }
    }

    protected void OnPreprocessorDirectiveElse() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorConditionalContext context = (PreprocessorConditionalContext) ruleContext;
        if (Boolean.FALSE.equals(conditions.peek())) {
            conditions.pop();
            context.value = AllConditions();
            conditions.push(true);
        } else {
            context.value = false;
        }
    }

    protected void OnPreprocessorDirectiveEndif() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorConditionalContext context = (PreprocessorConditionalContext) ruleContext;
        conditions.pop();
        context.value = conditions.peek();
    }

    protected void OnPreprocessorDirectiveLine() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorLineContext context = (PreprocessorLineContext) ruleContext;
        context.value = AllConditions();
    }

    protected void OnPreprocessorDirectiveError() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorDiagnosticContext context = (PreprocessorDiagnosticContext) ruleContext;
        context.value = AllConditions();
    }

    protected void OnPreprocessorDirectiveWarning() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorDiagnosticContext context = (PreprocessorDiagnosticContext) ruleContext;
        context.value = AllConditions();
    }

    protected void OnPreprocessorDirectiveRegion() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorRegionContext context = (PreprocessorRegionContext) ruleContext;
        context.value = AllConditions();
    }

    protected void OnPreprocessorDirectiveEndregion() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorRegionContext context = (PreprocessorRegionContext) ruleContext;
        context.value = AllConditions();
    }

    protected void OnPreprocessorDirectivePragma() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorPragmaContext context = (PreprocessorPragmaContext) ruleContext;
        context.value = AllConditions();
    }

    protected void OnPreprocessorDirectiveNullable() {
        ParserRuleContext ruleContext = this._ctx;
        PreprocessorNullableContext context = (PreprocessorNullableContext) ruleContext;
        context.value = AllConditions();
    }

    protected void OnPreprocessorExpressionTrue() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = TRUE;
    }

    protected void OnPreprocessorExpressionFalse() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = FALSE;
    }

    protected void OnPreprocessorExpressionConditionalSymbol() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = ConditionalSymbols.contains(context.CONDITIONAL_SYMBOL().getText()) ? TRUE : FALSE;
    }

    protected void OnPreprocessorExpressionConditionalOpenParens() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = context.expr.value;
    }

    protected void OnPreprocessorExpressionConditionalBang() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = context.expr.value.equals(TRUE) ? FALSE : TRUE;
    }

    protected void OnPreprocessorExpressionConditionalEq() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = (Objects.equals(context.expr1.value, context.expr2.value) ? TRUE : FALSE);
    }

    protected void OnPreprocessorExpressionConditionalNe() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = (!Objects.equals(context.expr1.value, context.expr2.value) ? TRUE : FALSE);
    }

    protected void OnPreprocessorExpressionConditionalAnd() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = (context.expr1.value.equals(TRUE) && context.expr2.value.equals(TRUE) ? TRUE : FALSE);
    }

    protected void OnPreprocessorExpressionConditionalOr() {
        ParserRuleContext ruleContext = this._ctx;
        Preprocessor_expressionContext context = (Preprocessor_expressionContext) ruleContext;
        context.value = (context.expr1.value.equals(TRUE) || context.expr2.value.equals(TRUE) ? TRUE : FALSE);
    }
}
