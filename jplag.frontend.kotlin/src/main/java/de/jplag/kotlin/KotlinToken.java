package de.jplag.kotlin;

import static de.jplag.kotlin.KotlinTokenConstants.*;

import de.jplag.Token;

public class KotlinToken extends Token {

    public KotlinToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    public String type2string() {
        return switch (type) {
            case FILE_END -> "<EOF>";
            case SEPARATOR_TOKEN -> "-----";
            case PACKAGE -> "PACKAGE";
            case IMPORT -> "IMPORT";
            case CLASS_DECLARATION -> "CLASS";
            case OBJECT_DECLARATION -> "OBJECT";
            case COMPANION_DECLARATION -> "COMPANION";
            case TYPE_PARAMETER -> "<T>";
            case CONSTRUCTOR -> "CONSTRUCTOR";
            case CLASS_BODY_BEGIN -> "CLASS{";
            case CLASS_BODY_END -> "}CLASS";
            case PROPERTY_DECLARATION -> "PROPERTY";
            case INITIALIZER -> "INIT";
            case FUNCTION -> "FUN";
            case GETTER -> "GET";
            case SETTER -> "SET";
            case FUNCTION_PARAMETER -> "PARAM";
            case FUNCTION_BODY_BEGIN -> "FUN{";
            case FUNCTION_BODY_END -> "}FUN";
            case BLOCK_BEGIN -> "{";
            case BLOCK_END -> "}";
            case FOR_EXPRESSION_BEGIN -> "FOR";
            case FOR_EXPRESSION_END -> "}FOR";
            case IF_EXPRESSION_START -> "IF";
            case IF_EXPRESSION_END -> "}IF(-ELSE)";
            case WHILE_EXPRESSION_START -> "WHILE";
            case WHILE_EXPRESSION_END -> "}WHILE";
            case DO_WHILE_EXPRESSION_START -> "DO";
            case DO_WHILE_EXPRESSION_END -> "}DO-WHILE";
            case TRY_EXPRESSION_START -> "TRY";
            case TRY_EXPRESSION_END -> "}TRY";
            case CATCH -> "CATCH";
            case FINALLY -> "FINALLY";
            case WHEN_EXPRESSION_START -> "WHEN";
            case WHEN_EXPRESSION_END -> "}WHEN";
            case WHEN_CONDITION -> "COND";
            case DO -> "{";
            case VARIABLE_DECLARATION -> "VARDECL";
            case FUNCTION_INVOCATION -> "INVOC";
            case CREATE_OBJECT -> "CONST";
            case ASSIGNMENT -> "ASSIGN";
            case THROW -> "THROW";
            case RETURN -> "RETURN";
            case CONTINUE -> "CONTINUE";
            case BREAK -> "BREAK";
            case INCR -> "INCR";
            case DECR -> "DECR";
            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
