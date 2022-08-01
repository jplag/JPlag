package de.jplag.swift;

import static de.jplag.swift.SwiftTokenConstants.*;

import de.jplag.Token;

public class SwiftToken extends Token {

    public SwiftToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    public String type2string() {
        return switch (type) {
            case FILE_END -> "<EOF>";
            case SEPARATOR_TOKEN -> "-----";
            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
