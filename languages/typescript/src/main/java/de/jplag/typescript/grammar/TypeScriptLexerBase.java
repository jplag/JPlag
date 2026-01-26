package de.jplag.typescript.grammar;

import java.util.ArrayDeque;
import java.util.Deque;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

/**
 * Copied from https://github.com/antlr/grammars-v4/tree/master/javascript/typescript/Java. Slightly modified to fit
 * JPlag code style.
 */
abstract class TypeScriptLexerBase extends Lexer {
    /**
     * Stores values of nested modes. By default mode is strict or defined externally (useStrictDefault).
     */
    private final Deque<Boolean> scopeStrictModes = new ArrayDeque<>();

    private Token lastToken = null;
    /**
     * Default value of strict mode Can be defined externally by setUseStrictDefault.
     */
    private boolean useStrictDefault = false;
    /**
     * Current value of strict mode Can be defined during parsing, see StringFunctions.js and StringGlobal.js samples.
     */
    private boolean useStrictCurrent = false;
    /**
     * Keeps track of the current depth of nested template string backticks. E.g. after the X in: `${a ? `${X templateDepth
     * will be 2. This variable is needed to determine if a `}` is a plain CloseBrace, or one that closes an expression
     * inside a template string.
     */
    private int templateDepth = 0;

    private int openBracesCount = 0;

    protected TypeScriptLexerBase(CharStream input) {
        super(input);
    }

    public boolean getStrictDefault() {
        return useStrictDefault;
    }

    public void setUseStrictDefault(boolean value) {
        useStrictDefault = value;
        useStrictCurrent = value;
    }

    public boolean isStrictMode() {
        return useStrictCurrent;
    }

    public void startTemplateString() {
        this.openBracesCount = 0;
    }

    public boolean isInTemplateString() {
        return this.templateDepth > 0 && this.openBracesCount == 0;
    }

    /**
     * Return the next token from the character stream and records this last token in case it resides on the default
     * channel. This recorded token is used to determine when the lexer could possibly match a regex literal. Also changes
     * scopeStrictModes stack if tokenize special string 'use strict';.
     * @return the next token from the character stream.
     */
    @Override
    public Token nextToken() {
        Token next = super.nextToken();

        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            // Keep track of the last token on the default channel.
            this.lastToken = next;
        }

        return next;
    }

    protected void processOpenBrace() {
        openBracesCount++;
        useStrictCurrent = !scopeStrictModes.isEmpty() && scopeStrictModes.peek() || useStrictDefault;
        scopeStrictModes.push(useStrictCurrent);
    }

    protected void processCloseBrace() {
        openBracesCount--;
        useStrictCurrent = scopeStrictModes.isEmpty() ? useStrictDefault : scopeStrictModes.pop();
    }

    protected void processStringLiteral() {
        if (lastToken == null || lastToken.getType() == TypeScriptLexer.OpenBrace) {
            String text = getText();
            if ("\"use strict\"".equals(text) || "'use strict'".equals(text)) {
                if (!scopeStrictModes.isEmpty()) {
                    scopeStrictModes.pop();
                }
                useStrictCurrent = true;
                scopeStrictModes.push(true);
            }
        }
    }

    protected void increaseTemplateDepth() {
        this.templateDepth++;
    }

    protected void decreaseTemplateDepth() {
        this.templateDepth--;
    }

    /**
     * @return {@code true} if the lexer can match a regex literal.
     */
    protected boolean isRegexPossible() {

        if (this.lastToken == null) {
            // No token has been produced yet: at the start of the input,
            // no division is possible, so a regex literal _is_ possible.
            return true;
        }

        return switch (this.lastToken.getType()) {
            case TypeScriptLexer.Identifier, TypeScriptLexer.NullLiteral, TypeScriptLexer.BooleanLiteral, TypeScriptLexer.This, TypeScriptLexer.CloseBracket, TypeScriptLexer.CloseParen, TypeScriptLexer.OctalIntegerLiteral, TypeScriptLexer.DecimalLiteral, TypeScriptLexer.HexIntegerLiteral, TypeScriptLexer.StringLiteral, TypeScriptLexer.PlusPlus, TypeScriptLexer.MinusMinus ->
                    // After any of the tokens above, no regex literal can follow.
                    false;
            default ->
                    // In all other cases, a regex literal _is_ possible.
                    true;
        };
    }
}