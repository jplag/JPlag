package de.jplag.golang;

import de.jplag.TokenConstants;

public interface GoTokenConstants extends TokenConstants {

    // TOP LEVEL STRUCTURES

    int PACKAGE = 2;
    int IMPORT_CLAUSE = 3;
    int IMPORT_CLAUSE_BEGIN = 4;
    int IMPORT_CLAUSE_END = 5;
    int IMPORT_DECLARATION = 6;
    int ARRAY_BODY_BEGIN = 7;
    int ARRAY_BODY_END = 8;
    int STRUCT_DECLARATION = 9;
    int STRUCT_BODY_BEGIN = 10;
    int STRUCT_BODY_END = 11;
    int INTERFACE_DECLARATION = 12;
    int INTERFACE_BLOCK_BEGIN = 13;
    int INTERFACE_BLOCK_END = 14;
    int INTERFACE_METHOD = 15;
    int TYPE_CONSTRAINT = 16;
    int TYPE_ASSERTION = 17;
    int MAP_BODY_BEGIN = 18;
    int MAP_BODY_END = 19;
    int SLICE_BODY_BEGIN = 20;
    int SLICE_BODY_END = 21;
    int NAMED_TYPE_BODY_BEGIN = 22;
    int NAMED_TYPE_BODY_END = 23;
    int MEMBER_DECLARATION = 24;

    // FUNCTIONS AND METHODS

    int FUNCTION_DECLARATION = 25;
    int RECEIVER = 26;
    int FUNCTION_PARAMETER = 27;
    int FUNCTION_BODY_BEGIN = 28;
    int FUNCTION_BODY_END = 29;

    // CONTROL FLOW STATEMENTS

    int IF_STATEMENT = 30;
    int IF_BLOCK_BEGIN = 31;
    int IF_BLOCK_END = 32;
    int ELSE_BLOCK_BEGIN = 33;
    int ELSE_BLOCK_END = 34;
    int FOR_STATEMENT = 35;
    int FOR_BLOCK_BEGIN = 36;
    int FOR_BLOCK_END = 37;
    int SWITCH_STATEMENT = 38;
    int SWITCH_BLOCK_BEGIN = 39;
    int SWITCH_BLOCK_END = 40;
    int SWITCH_CASE = 41;
    int SELECT_STATEMENT = 42;
    int SELECT_BLOCK_BEGIN = 43;
    int SELECT_BLOCK_END = 44;
    int CASE_BLOCK_BEGIN = 45;
    int CASE_BLOCK_END = 46;

    // STATEMENTS

    int VARIABLE_DECLARATION = 47;
    int FUNCTION_LITERAL = 48;
    int ASSIGNMENT = 49;
    int SEND_STATEMENT = 50;
    int RECEIVE_STATEMENT = 51;
    int INVOCATION = 52;
    int ARGUMENT = 53;
    int STATEMENT_BLOCK_BEGIN = 54;
    int STATEMENT_BLOCK_END = 55;

    // OBJECT CREATION

    int ARRAY_ELEMENT = 56;
    int MAP_ELEMENT = 57;
    int SLICE_ELEMENT = 58;
    int NAMED_TYPE_ELEMENT = 59;
    int ARRAY_CONSTRUCTOR = 60;
    int MAP_CONSTRUCTOR = 61;
    int SLICE_CONSTRUCTOR = 62;
    int NAMED_TYPE_CONSTRUCTOR = 63;

    // CONTROL FLOW KEYWORDS

    int RETURN = 64;
    int BREAK = 65;
    int CONTINUE = 66;
    int FALLTHROUGH = 67;
    int GOTO = 68;
    int GO = 69;
    int DEFER = 70;

    int NUM_DIFF_TOKENS = 71;

}
