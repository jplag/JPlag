package de.jplag.kotlin;

import de.jplag.TokenConstants;

public interface KotlinTokenConstants extends TokenConstants {

    int CLASS_DECLARATION = 3;
    int OBJECT_DECLARATION = 4;
    int COMPANION_DECLARATION = 5;

    int TYPE_PARAMETER = 6;
    int CONSTRUCTOR = 7;

    int CLASS_BODY_BEGIN = 8;
    int CLASS_BODY_END = 9;

    int PROPERTY_DECLARATION = 10;

    int INITIALIZER = 11;

    int FUNCTION = 12;

    int GETTER = 13;
    int SETTER = 14;
    int FUNCTION_PARAMETER = 15;
    int FUNCTION_BODY_BEGIN = 16;
    int FUNCTION_BODY_END = 17;

    int BLOCK_BEGIN = 18;
    int BLOCK_END = 19;

    int FOR_EXPRESSION_BEGIN = 20;
    int FOR_EXPRESSION_END = 21;

    int IF_EXPRESSION_START = 22;
    int IF_EXPRESSION_END = 23;

    int WHILE_EXPRESSION_START = 24;
    int WHILE_EXPRESSION_END = 25;

    int DO_WHILE_EXPRESSION_START = 26;
    int DO_WHILE_EXPRESSION_END = 27;

    int TRY_EXPRESSION_START = 28;
    int TRY_EXPRESSION_END = 29;
    int CATCH = 30;
    int FINALLY = 31;
    int WHEN_EXPRESSION_START = 32;
    int WHEN_EXPRESSION_END = 33;
    int WHEN_CONDITION = 34;

    int DO = 35;
    int VARIABLE_DECLARATION = 36;
    int FUNCTION_INVOCATION = 37;
    int CREATE_OBJECT = 38;
    int ASSIGNMENT = 39;

    int THROW = 40;
    int RETURN = 41;
    int CONTINUE = 42;
    int BREAK = 43;

    int NUMBER_DIFF_TOKENS = 43;
}
