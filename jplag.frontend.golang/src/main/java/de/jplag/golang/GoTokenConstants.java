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
    int METHOD_DECLARATION = 26;
    int RECEIVER = 27;
    int FUNCTION_PARAMETER = 28;
    int FUNCTION_BODY_BEGIN = 29;
    int FUNCTION_BODY_END = 30;

    // CONTROL FLOW STATEMENTS

    int IF_STATEMENT = 31;
    int IF_BLOCK_BEGIN = 32;
    int IF_BLOCK_END = 33;
    int ELSE_BLOCK_BEGIN = 34;
    int ELSE_BLOCK_END = 35;
    int FOR_STATEMENT = 36;
    int FOR_BLOCK_BEGIN = 37;
    int FOR_BLOCK_END = 38;
    int SWITCH_STATEMENT = 39;
    int SWITCH_BLOCK_BEGIN = 40;
    int SWITCH_BLOCK_END = 41;
    int SWITCH_CASE = 42;
    int SELECT_STATEMENT = 43;
    int SELECT_BLOCK_BEGIN = 44;
    int SELECT_BLOCK_END = 45;
    int CASE_BLOCK_BEGIN = 46;
    int CASE_BLOCK_END = 47;

    // Statements

    int VARIABLE_DECLARATION = 48;
    int CONSTANT_DECLARATION = 49;
    int FUNCTION_LITERAL = 50;
    int ASSIGNMENT = 51;
    int SEND_STATEMENT = 52;
    int RECEIVE_STATEMENT = 53;
    int INVOCATION = 54;
    int ARGUMENT = 55;
    int STATEMENT_BLOCK_BEGIN = 56;
    int STATEMENT_BLOCK_END = 57;

    // OBJECT CREATION

    int ARRAY_ELEMENT = 58;
    int MAP_ELEMENT = 59;
    int SLICE_ELEMENT = 60;
    int NAMED_TYPE_ELEMENT = 61;
    int ARRAY_CONSTRUCTOR = 62;
    int MAP_CONSTRUCTOR = 63;
    int SLICE_CONSTRUCTOR = 64;
    int NAMED_TYPE_CONSTRUCTOR = 65;

    // CONTROL FLOW KEYWORDS

    int RETURN = 66;
    int BREAK = 67;
    int CONTINUE = 68;
    int FALLTHROUGH = 69;
    int GOTO = 70;
    int GO = 71;
    int DEFER = 72;

    int NUM_DIFF_TOKENS = 73;

}
