package de.jplag.swift;

import de.jplag.TokenConstants;

public interface SwiftTokenConstants extends TokenConstants {

    int IMPORT = 2;

    int CLASS_DECLARATION = 3;
    int STRUCT_DECLARATION = 4;
    int ENUM_DECLARATION = 5;
    int PROTOCOL_DECLARATION = 6;

    int CLASS_BODY_BEGIN = 7;
    int CLASS_BODY_END = 8;

    int STRUCT_BODY_BEGIN = 9;
    int STRUCT_BODY_END = 10;

    int ENUM_BODY_BEGIN = 11;
    int ENUM_BODY_END = 12;
    int ENUM_LITERAL = 13;

    int PROTOCOL_BODY_BEGIN = 14;
    int PROTOCOL_BODY_END = 15;

    int PROPERTY_DECLARATION = 16;
    int PROPERTY_ACCESSOR_BEGIN = 17;
    int PROPERTY_ACCESSOR_END = 18;

    int FUNCTION = 19;
    int FUNCTION_PARAMETER = 20;
    int FUNCTION_BODY_BEGIN = 21;
    int FUNCTION_BODY_END = 22;

    int CLOSURE_BODY_BEGIN = 23;
    int CLOSURE_BODY_END = 24;

    int FOR_BODY_BEGIN = 25;
    int FOR_BODY_END = 26;

    int IF_BODY_BEGIN = 27;
    int IF_BODY_END = 28;

    int SWITCH_BODY_BEGIN = 29;
    int SWITCH_BODY_END = 30;
    int SWITCH_CASE = 31;

    int WHILE_BODY_BEGIN = 32;
    int WHILE_BODY_END = 33;

    int REPEAT_WHILE_BODY_BEGIN = 34;
    int REPEAT_WHILE_BODY_END = 35;

    int DEFER_BODY_BEGIN = 36;
    int DEFER_BODY_END = 37;
    int THROW = 38;
    int RETURN = 39;
    int CONTINUE = 40;
    int BREAK = 41;
    int FALLTHROUGH = 42;

    int ASSIGNMENT = 43;
    int FUNCTION_CALL = 44;

    int NUMBER_DIFF_TOKENS = 45;
}
