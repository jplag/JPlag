package de.jplag.golang;

import static de.jplag.golang.GoTokenConstants.*;

import de.jplag.Token;

public class GoToken extends Token {

    public GoToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    protected String type2string() {
        return switch (type) {
            case FILE_END -> "<EOF>";
            case SEPARATOR_TOKEN -> "---------";

            case STRUCT_DECLARATION_BEGIN -> "STRUCT";
            case STRUCT_BODY_BEGIN -> "STRUCT{";
            case STRUCT_BODY_END -> "}STRUCT";

            case MEMBER_DECLARATION -> "FIELD";

            case FUNCTION_DECLARATION -> "FUNC";
            case METHOD_DECLARATION -> "METHOD";
            case FUNCTION_PARAMETER -> "PARAM";
            case FUNCTION_BODY_BEGIN -> "FUNC{";
            case FUNCTION_BODY_END -> "}FUNC";

            // Control flow statements

            case IF_STATEMENT -> "IF";

            case IF_BLOCK_BEGIN -> "IF{";
            case IF_BLOCK_END -> "}IF";
            case ELSE_BLOCK_BEGIN -> "ELSE{";
            case ELSE_BLOCK_END -> "}ELSE";
            case FOR_STATEMENT -> "FOR";
            case FOR_BLOCK_BEGIN -> "FOR{";
            case FOR_BLOCK_END -> "}FOR";
            case SWITCH_STATEMENT -> "SWITCH";
            case SWITCH_BLOCK_BEGIN -> "SWITCH{";
            case SWITCH_BLOCK_END -> "}SWITCH";
            case SWITCH_CASE -> "CASE";
            case CASE_BLOCK_BEGIN -> "CASE{";
            case CASE_BLOCK_END -> "}CASE";

            // Statements

            case FUNCTION_LITERAL -> "FUNC_LIT";
            case ASSIGNMENT -> "ASSIGN";
            case INVOCATION -> "INVOC";
            case ARGUMENT -> "ARG";
            case STATEMENT_BLOCK_BEGIN -> "INNER{";
            case STATEMENT_BLOCK_END -> "}INNER";

            // Object Creation
            case STRUCT_CONSTRUCTOR -> "STRUCT()";
            case STRUCT_VALUE -> "ARG";
            case ARRAY_CONSTRUCTOR -> "ARRAY()";
            case SLICE_CONSTRUCTOR -> "SLICE()";
            case MAP_CONSTRUCTOR -> "MAP()";

            // Control Flow Keywords

            case RETURN -> "RETURN";
            case BREAK -> "BREAK";
            case CONTINUE -> "CONTINUE";
            case GOTO -> "GOTO";
            case GO -> "GO";
            case DEFER -> "DEFER";
            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
