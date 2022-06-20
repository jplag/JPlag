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

    int PROPERTY_DECLARATION = 11;

    int INITIALIZER = 12;

    int FUNCTION = 13;

    int GETTER = 14;
    int SETTER = 15;
    int FUNCTION_PARAMETER = 16;
    int FUNCTION_BODY_BEGIN = 17;
    int FUNCTION_BODY_END = 18;

    int FUNCTION_LITERAL_BEGIN = 19;
    int FUNCTION_LITERAL_END = 20;

    int BLOCK_BEGIN = 21;
    int BLOCK_END = 22;

    int FOR_EXPRESSION_BEGIN = 23;
    int FOR_EXPRESSION_END = 24;

    int IF_EXPRESSION_START = 25;
    int IF_EXPRESSION_END = 26;

    int WHILE_EXPRESSION_START = 27;
    int WHILE_EXPRESSION_END = 28;

    int DO_WHILE_EXPRESSION_START = 29;
    int DO_WHILE_EXPRESSION_END = 30;

    int TRY_EXPRESSION_START = 31;
    int TRY_EXPRESSION_END = 32;
    int CATCH = 33;
    int FINALLY = 34;
    int WHEN_EXPRESSION_START = 35;
    int WHEN_EXPRESSION_END = 36;
    int WHEN_CONDITION = 37;

    int DO = 38;

    int VARIABLE_DECLARATION = 39;
    int FUNCTION_INVOCATION = 40;
    int CREATE_OBJECT = 41;
    int ASSIGNMENT = 42;

    int THROW = 43;
    int RETURN = 44;
    int CONTINUE = 45;
    int BREAK = 46;

    int INCR = 47;
    int DECR = 48;

    int STRING = 49;
    int NUMBER_DIFF_TOKENS = 50;
}
