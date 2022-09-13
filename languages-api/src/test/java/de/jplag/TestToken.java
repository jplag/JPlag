package de.jplag;

import static de.jplag.TokenConstants.FILE_END;
import static de.jplag.simple.TestTokenConstants.STRING;

public class TestToken extends Token {
    public TestToken(int tokenType, String file, int line, int column, int length) {
        super(tokenType, file, line, column, length);
    }

    @Override
    protected String type2string() {
        return switch (type) {
            case STRING -> "STRING";
            case FILE_END -> "<EOF>";
            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
