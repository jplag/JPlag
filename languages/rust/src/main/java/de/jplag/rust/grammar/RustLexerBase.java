package de.jplag.rust.grammar;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

public abstract class RustLexerBase extends Lexer {
    private Token currentToken;
    private Token previousToken;

    protected RustLexerBase(CharStream input) {
        super(input);
    }

    @Override
    public Token nextToken() {
        Token next = super.nextToken();

        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            // Keep track of the last token on the default channel.
            this.previousToken = this.currentToken;
            this.currentToken = next;
        }

        return next;
    }

    public boolean atFileStart() {
        // Before consuming the first token, input.LA(-1) returns IntStream.EOF = -1
        return _input.LA(-1) == IntStream.EOF;
    }

    public boolean floatDotPossible() {
        // only block . _ identifier after float
        if (lookAheadMatchesOneOf(".", "_")) {
            return false;
        }
        // 1.f32, 1.f64
        if (lookAheadMatchesOneOf("f32", "f64")) {
            return true;
        }

        int next = _input.LA(1);
        return !Character.isAlphabetic(next);
    }

    private boolean lookAheadMatches(String expected) {
        for (int charIndex = 0; charIndex < expected.length(); charIndex++) {
            if (_input.LA(charIndex + 1) != expected.charAt(charIndex))
                return false;
        }
        return true;
    }

    private boolean lookAheadMatchesOneOf(String... expected) {
        return Arrays.stream(expected).anyMatch(this::lookAheadMatches);
    }

    public boolean floatLiteralPossible() {
        if (this.currentToken == null || this.currentToken.getType() != RustLexer.DOT) {
            return true;
        } else if (this.previousToken == null) {
            return true;
        }

        int type = this.previousToken.getType();
        List<Integer> noFloatLiteralTypes = List.of(RustLexer.CHAR_LITERAL, RustLexer.STRING_LITERAL, RustLexer.RAW_STRING_LITERAL,
                RustLexer.BYTE_LITERAL, RustLexer.BYTE_STRING_LITERAL, RustLexer.RAW_BYTE_STRING_LITERAL, RustLexer.INTEGER_LITERAL,
                RustLexer.DEC_LITERAL, RustLexer.HEX_LITERAL, RustLexer.OCT_LITERAL, RustLexer.BIN_LITERAL, RustLexer.KW_SUPER,
                RustLexer.KW_SELFVALUE, RustLexer.KW_SELFTYPE, RustLexer.KW_CRATE, RustLexer.KW_DOLLARCRATE, RustLexer.GT, RustLexer.RCURLYBRACE,
                RustLexer.RSQUAREBRACKET, RustLexer.RPAREN, RustLexer.KW_AWAIT, RustLexer.NON_KEYWORD_IDENTIFIER, RustLexer.RAW_IDENTIFIER,
                RustLexer.KW_MACRORULES);
        return !noFloatLiteralTypes.contains(type);
    }
}