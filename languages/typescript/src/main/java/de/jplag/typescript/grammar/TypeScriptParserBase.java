package de.jplag.typescript.grammar;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

/**
 * All parser methods that used in grammar (p, prev, notLineTerminator, etc.) should start with lower case char similar
 * to parser rules. Copied from https://github.com/antlr/grammars-v4/tree/master/javascript/typescript/Java. Slightly
 * modified to fit JPlag code style.
 */
public abstract class TypeScriptParserBase extends Parser {
    protected TypeScriptParserBase(TokenStream input) {
        super(input);
    }

    /**
     * Short form for {@link TypeScriptParserBase#prev(String)}. Checks whether the previous token's text matches the given
     * string.
     * @param text the token text to compare with the previous token's text
     * @return {@code true} if the previous token's text matches the specified text, otherwise {@code false}
     */
    protected boolean p(String text) {
        return prev(text);
    }

    /**
     * Checks whether the previous token's text matches the given string.
     * @param text the token text to compare with the previous token's text
     * @return {@code true} if the previous token's text equals the specified text, otherwise {@code false}
     */
    protected boolean prev(String text) {
        return _input.LT(-1).getText().equals(text);
    }

    /**
     * Short form for {@link TypeScriptParserBase#next(String)}. Checks whether the next token's text matches the given
     * string.
     * @param text the token text to compare with the next token's text
     * @return {@code true} if the next token's text matches the specified text, otherwise {@code false}
     */
    protected boolean n(String text) {
        return next(text);
    }

    /**
     * Checks whether the next token's text matches the given string.
     * @param text the token text to compare with the next token's text
     * @return {@code true} if the next token's text equals the specified text, otherwise {@code false}
     */
    protected boolean next(String text) {
        return _input.LT(1).getText().equals(text);
    }

    protected boolean notLineTerminator() {
        return !here(TypeScriptParser.LineTerminator);
    }

    protected boolean notOpenBraceAndNotFunction() {
        int nextTokenType = _input.LT(1).getType();
        return nextTokenType != TypeScriptParser.OpenBrace && nextTokenType != TypeScriptParser.Function_;
    }

    protected boolean closeBrace() {
        return _input.LT(1).getType() == TypeScriptParser.CloseBrace;
    }

    /**
     * Returns {@code true} iff on the current index of the parser's token stream a token of the given {@code type} exists
     * on the {@code HIDDEN} channel.
     * @param type the type of the token on the {@code HIDDEN} channel to check.
     * @return {@code true} iff on the current index of the parser's token stream a token of the given {@code type} exists
     * on the {@code HIDDEN} channel.
     */
    private boolean here(final int type) {

        // Get the token ahead of the current index.
        int possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 1;
        Token ahead = _input.get(possibleIndexEosToken);

        // Check if the token resides on the HIDDEN channel and if it's of the
        // provided type.
        return ahead.getChannel() == Lexer.HIDDEN && ahead.getType() == type;
    }

    /**
     * Returns {@code true} iff on the current index of the parser's token stream a token exists on the {@code HIDDEN}
     * channel which either is a line terminator, or is a multi line comment that contains a line terminator.
     * @return {@code true} iff on the current index of the parser's token stream a token exists on the {@code HIDDEN}
     * channel which either is a line terminator, or is a multi line comment that contains a line terminator.
     */
    protected boolean lineTerminatorAhead() {

        // Get the token ahead of the current index.
        int possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 1;
        Token ahead = _input.get(possibleIndexEosToken);

        if (ahead.getChannel() != Lexer.HIDDEN) {
            // We're only interested in tokens on the HIDDEN channel.
            return false;
        }

        if (ahead.getType() == TypeScriptParser.LineTerminator) {
            // There is definitely a line terminator ahead.
            return true;
        }

        if (ahead.getType() == TypeScriptParser.WhiteSpaces) {
            // Get the token ahead of the current whitespaces.
            possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 2;
            ahead = _input.get(possibleIndexEosToken);
        }

        // Get the token's text and type.
        String text = ahead.getText();
        int type = ahead.getType();

        // Check if the token is, or contains a line terminator.
        return type == TypeScriptParser.MultiLineComment && (text.contains("\r") || text.contains("\n")) || type == TypeScriptParser.LineTerminator;
    }
}
