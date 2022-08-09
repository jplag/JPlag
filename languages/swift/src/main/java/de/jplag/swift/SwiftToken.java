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
            case IMPORT -> "IMPORT";
            case CLASS_DECLARATION -> "CLASS";
            case STRUCT_DECLARATION -> "STRUCT";
            case ENUM_DECLARATION -> "ENUM";
            case PROTOCOL_DECLARATION -> "PROTOCOL";
            case CLASS_BODY_BEGIN -> "CLASS{";
            case CLASS_BODY_END -> "}CLASS";
            case STRUCT_BODY_BEGIN -> "STRUCT{";
            case STRUCT_BODY_END -> "}STRUCT";
            case ENUM_BODY_BEGIN -> "ENUM{";
            case ENUM_BODY_END -> "}ENUM";
            case ENUM_LITERAL -> "ENUM CASE";
            case PROTOCOL_BODY_BEGIN -> "PROTOCOL{";
            case PROTOCOL_BODY_END -> "}PROTOCOL";
            case PROPERTY_DECLARATION -> "PROPERTY";
            case PROPERTY_ACCESSOR_BEGIN -> "ACCESSOR{";
            case PROPERTY_ACCESSOR_END -> "}ACCESSOR";
            case FUNCTION -> "FUNCTION";
            case FUNCTION_PARAMETER -> "PARAMETER";
            case FUNCTION_BODY_BEGIN -> "FUNCTION{";
            case FUNCTION_BODY_END -> "}FUNCTION";
            case CLOSURE_BODY_BEGIN -> "CLOSURE{";
            case CLOSURE_BODY_END -> "}CLOSURE";
            case FOR_BODY_BEGIN -> "FOR{";
            case FOR_BODY_END -> "}FOR";
            case IF_BODY_BEGIN -> "IF{";
            case IF_BODY_END -> "}IF";
            case SWITCH_BODY_BEGIN -> "SWITCH{";
            case SWITCH_BODY_END -> "}SWITCH";
            case SWITCH_CASE -> "CASE";
            case WHILE_BODY_BEGIN -> "WHILE{";
            case WHILE_BODY_END -> "}WHILE";
            case REPEAT_WHILE_BODY_BEGIN -> "REPEAT{";
            case REPEAT_WHILE_BODY_END -> "}REPEAT";
            case DEFER_BODY_BEGIN -> "DEFER{";
            case DEFER_BODY_END -> "}DEFER";
            case THROW -> "THROW";
            case RETURN -> "RETURN";
            case CONTINUE -> "CONTINUE";
            case BREAK -> "BREAK";
            case FALLTHROUGH -> "FALLTHROUGH";
            case ASSIGNMENT -> "ASSIGN";
            case FUNCTION_CALL -> "CALL";
            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
