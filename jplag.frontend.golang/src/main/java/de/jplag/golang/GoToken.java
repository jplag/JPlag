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

            case PACKAGE -> "PACKAGE";
            case IMPORT_CLAUSE -> "IMPORT";
            case IMPORT_CLAUSE_BEGIN -> "IMPORT(";
            case IMPORT_CLAUSE_END -> ")IMPORT";
            case IMPORT_DECLARATION -> "IMPORT_DECL";

            case ARRAY_BODY_BEGIN -> "ARRAY{";
            case ARRAY_BODY_END -> "}ARRAY";

            case STRUCT_DECLARATION -> "STRUCT";
            case STRUCT_BODY_BEGIN -> "STRUCT{";
            case STRUCT_BODY_END -> "}STRUCT";

            case INTERFACE_DECLARATION -> "INTERFACE";
            case INTERFACE_BLOCK_BEGIN -> "INTERFACE{";
            case INTERFACE_BLOCK_END -> "}INTERFACE";

            case INTERFACE_METHOD -> "INTERFACE_METHOD";
            case TYPE_CONSTRAINT -> "TYPE_CONSTRAINT";
            case TYPE_ASSERTION -> "TYPE_ASSERTION";

            case MAP_BODY_BEGIN -> "MAP{";
            case MAP_BODY_END -> "}MAP";

            case SLICE_BODY_BEGIN -> "SLICE{";
            case SLICE_BODY_END -> "}SLICE";

            case NAMED_TYPE_BODY_BEGIN -> "CTYPE{";
            case NAMED_TYPE_BODY_END -> "}CTYPE";

            case MEMBER_DECLARATION -> "FIELD";

            // Functions and Methods

            case FUNCTION_DECLARATION -> "FUNC";
            case RECEIVER -> "RECEIVER";
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
            case SELECT_STATEMENT -> "SELECT";
            case SELECT_BLOCK_BEGIN -> "SELECT{";
            case SELECT_BLOCK_END -> "}SELECT";
            case CASE_BLOCK_BEGIN -> "CASE{";
            case CASE_BLOCK_END -> "}CASE";

            // Statements

            case VARIABLE_DECLARATION -> "VAR_DECL";
            case FUNCTION_LITERAL -> "FUNC_LIT";
            case ASSIGNMENT -> "ASSIGN";
            case SEND_STATEMENT -> "SEND";
            case RECEIVE_STATEMENT -> "RECV";
            case INVOCATION -> "INVOC";
            case ARGUMENT -> "ARG";
            case STATEMENT_BLOCK_BEGIN -> "INNER{";
            case STATEMENT_BLOCK_END -> "}INNER";

            // Object Creation

            case ARRAY_ELEMENT -> "ARRAY_ELEM";
            case MAP_ELEMENT -> "MAP_ELEM";
            case SLICE_ELEMENT -> "SLICE_ELEM";
            case NAMED_TYPE_ELEMENT -> "CTYPE_ELEM";
            case ARRAY_CONSTRUCTOR -> "ARRAY()";
            case SLICE_CONSTRUCTOR -> "SLICE()";
            case MAP_CONSTRUCTOR -> "MAP()";
            case NAMED_TYPE_CONSTRUCTOR -> "CTYPE()";

            // Control Flow Keywords

            case RETURN -> "RETURN";
            case BREAK -> "BREAK";
            case CONTINUE -> "CONTINUE";
            case FALLTHROUGH -> "FALLTHROUGH";
            case GOTO -> "GOTO";
            case GO -> "GO";
            case DEFER -> "DEFER";
            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
