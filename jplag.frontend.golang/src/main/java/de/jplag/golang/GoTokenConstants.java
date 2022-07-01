package de.jplag.golang;

import de.jplag.TokenConstants;

public interface GoTokenConstants extends TokenConstants {

    // Top level structures

    int STRUCT_DECLARATION_BEGIN = 2;
    int STRUCT_BODY_BEGIN = 3;
    int STRUCT_BODY_END = 4;
    int MEMBER_DECLARATION = 5;
    int FUNCTION_DECLARATION = 6;
    int METHOD_DECLARATION = 7;
    int FUNCTION_PARAMETER = 8;
    int FUNCTION_BODY_BEGIN = 9;
    int FUNCTION_BODY_END = 10;

    // Control flow statements

    int IF_STATEMENT = 11;
    int IF_BLOCK_BEGIN = 12;
    int IF_BLOCK_END = 13;
    int ELSE_BLOCK_BEGIN = 14;
    int ELSE_BLOCK_END = 15;
    int FOR_STATEMENT = 16;
    int FOR_BLOCK_BEGIN = 17;
    int FOR_BLOCK_END = 18;
    int SWITCH_STATEMENT = 19;
    int SWITCH_BLOCK_BEGIN = 20;
    int SWITCH_BLOCK_END = 21;
    int SWITCH_CASE = 22;
    int CASE_BLOCK_BEGIN = 23;
    int CASE_BLOCK_END = 24;

    // Statements

    int FUNCTION_LITERAL = 25;
    int ASSIGNMENT = 26;
    int INVOCATION = 27;
    int ARGUMENT = 28;
    int STATEMENT_BLOCK_BEGIN = 29;
    int STATEMENT_BLOCK_END = 30;

    // Object Creation

    int STRUCT_CONSTRUCTOR = 31;
    int STRUCT_VALUE = 32;
    int ARRAY_CONSTRUCTOR = 33;
    int SLICE_CONSTRUCTOR = 34;
    int MAP_CONSTRUCTOR = 35;

    // Control Flow Keywords

    int RETURN = 36;
    int BREAK = 37;
    int CONTINUE = 38;
    int GOTO = 39;
    int GO = 40;
    int DEFER = 41;

    int NUM_DIFF_TOKENS = 42;

}
