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
    int INITIALIZER_BODY_START = 15;
    int INITIALIZER_BODY_END = 16;

    int FUNCTION = 17;

    int GETTER = 18;
    int SETTER = 19;
    int FUNCTION_PARAMETER = 20;
    int FUNCTION_BODY_BEGIN = 21;
    int FUNCTION_BODY_END = 22;

    int FUNCTION_LITERAL_BEGIN = 23;
    int FUNCTION_LITERAL_END = 24;

    int FOR_EXPRESSION_BEGIN = 25;
    int FOR_EXPRESSION_END = 26;

    int IF_EXPRESSION_START = 27;
    int IF_EXPRESSION_END = 28;

    int WHILE_EXPRESSION_START = 29;
    int WHILE_EXPRESSION_END = 30;

    int DO_WHILE_EXPRESSION_START = 31;
    int DO_WHILE_EXPRESSION_END = 32;

    int TRY_EXPRESSION = 33;
    int TRY_BODY_START = 34;
    int TRY_BODY_END = 35;
    int CATCH = 36;
    int CATCH_BODY_START = 37;
    int CATCH_BODY_END = 38;
    int FINALLY = 39;
    int FINALLY_BODY_START = 40;
    int FINALLY_BODY_END = 41;
    int WHEN_EXPRESSION_START = 42;
    int WHEN_EXPRESSION_END = 43;
    int WHEN_CONDITION = 44;

    int CONTROL_STRUCTURE_BODY_START = 45;
    int CONTROL_STRUCTURE_BODY_END = 46;

    int VARIABLE_DECLARATION = 47;
    int ENUM_ENTRY = 48;
    int FUNCTION_INVOCATION = 49;
    int CREATE_OBJECT = 50;
    int ASSIGNMENT = 51;

    int THROW = 52;
    int RETURN = 53;
    int CONTINUE = 54;
    int BREAK = 55;

    int NUMBER_DIFF_TOKENS = 56;
}
