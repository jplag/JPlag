package de.jplag.kotlin;

import de.jplag.TokenConstants;

public interface KotlinTokenConstants extends TokenConstants {

    int PACKAGE = 2;
    int IMPORT = 3;
    int CLASS_DECLARATION = 4;
    int OBJECT_DECLARATION = 5;
    int COMPANION_DECLARATION = 6;

    int TYPE_PARAMETER = 7;
    int CONSTRUCTOR = 8;

    int CLASS_BODY_BEGIN = 9;
    int CLASS_BODY_END = 10;

    int ENUM_CLASS_BODY_BEGIN = 11;
    int ENUM_CLASS_BODY_END = 12;

    int PROPERTY_DECLARATION = 13;

    int INITIALIZER = 14;

    int FUNCTION = 15;

    int GETTER = 16;
    int SETTER = 17;
    int FUNCTION_PARAMETER = 18;
    int FUNCTION_BODY_BEGIN = 19;
    int FUNCTION_BODY_END = 20;

    int FUNCTION_LITERAL_BEGIN = 21;
    int FUNCTION_LITERAL_END = 22;

    int BLOCK_BEGIN = 23;
    int BLOCK_END = 24;

    int FOR_EXPRESSION_BEGIN = 25;
    int FOR_EXPRESSION_END = 26;

    int IF_EXPRESSION_START = 27;
    int IF_EXPRESSION_END = 28;

    int WHILE_EXPRESSION_START = 29;
    int WHILE_EXPRESSION_END = 30;

    int DO_WHILE_EXPRESSION_START = 31;
    int DO_WHILE_EXPRESSION_END = 32;

    int TRY_EXPRESSION_START = 33;
    int TRY_EXPRESSION_END = 34;
    int CATCH = 35;
    int FINALLY = 36;
    int WHEN_EXPRESSION_START = 37;
    int WHEN_EXPRESSION_END = 38;
    int WHEN_CONDITION = 39;

    int DO = 40;

    int VARIABLE_DECLARATION = 41;
    int ENUM_ENTRY = 42;
    int FUNCTION_INVOCATION = 43;
    int CREATE_OBJECT = 44;
    int ASSIGNMENT = 45;

    int THROW = 46;
    int RETURN = 47;
    int CONTINUE = 48;
    int BREAK = 49;

    int INCR = 50;
    int DECR = 51;

    int NUMBER_DIFF_TOKENS = 52;
}
