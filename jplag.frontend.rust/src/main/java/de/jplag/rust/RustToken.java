package de.jplag.rust;

import static de.jplag.rust.RustTokenConstants.*;

import de.jplag.Token;

public class RustToken extends Token {
    public RustToken(int type, String currentFile, int line, int start, int length) {
        super(type, currentFile, line, start, length);
    }

    @Override
    protected String type2string() {
        return switch (type) {
            case FILE_END -> "<EOF>";
            case INNER_ATTRIBUTE -> "INNER_ATTR";
            case OUTER_ATTRIBUTE -> "OUTER_ATTR";
            case USE_DECLARATION -> "USE";
            case USE_ITEM -> "USE_ITEM";
            case STRUCT_BODY_BEGIN -> "STRUCT{";
            case STRUCT_BODY_END -> "}STRUCT";
            case FUNCTION -> "FUNCTION";
            case TYPE_PARAMETER -> "<T>";
            case FUNCTION_PARAMETER -> "PARAM";
            case FUNCTION_BODY_START -> "FUNC{";
            case FUNCTION_BODY_END -> "}FUNC";

            case INNER_BLOCK_START -> "INNER{";
            case INNER_BLOCK_END -> "}INNER";

            case ASSIGNMENT -> "ASSIGN";
            case VARIABLE_DECLARATION -> "VAR_DECL";

            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
