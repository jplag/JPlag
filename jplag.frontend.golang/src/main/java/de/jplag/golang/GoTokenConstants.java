package de.jplag.golang;

import de.jplag.TokenConstants;

public interface GoTokenConstants extends TokenConstants {

    // Top level structures

    int PACKAGE = 2;
    int IMPORT_CLAUSE = 3;
    int IMPORT_CLAUSE_BEGIN = 4;
    int IMPORT_CLAUSE_END = 5;
    int IMPORT_DECL = 6;
    int ARRAY_BODY_BEGIN = 7;
    int ARRAY_BODY_END = 8;
    int STRUCT_DECLARATION_BEGIN = 9;
    int STRUCT_BODY_BEGIN = 10;
    int STRUCT_BODY_END = 11;
    int MAP_BODY_BEGIN = 12;
    int MAP_BODY_END = 13;
    int SLICE_BODY_BEGIN = 14;
    int SLICE_BODY_END = 15;
    int NAMED_TYPE_BODY_BEGIN = 16;
    int NAMED_TYPE_BODY_END = 17;
    int MEMBER_DECLARATION = 18;
    int FUNCTION_DECLARATION = 19;
    int METHOD_DECLARATION = 20;
    int FUNCTION_PARAMETER = 21;
    int FUNCTION_BODY_BEGIN = 22;
    int FUNCTION_BODY_END = 23;

    // Control flow statements

    int IF_STATEMENT = 24;
    int IF_BLOCK_BEGIN = 25;
    int IF_BLOCK_END = 26;
    int ELSE_BLOCK_BEGIN = 27;
    int ELSE_BLOCK_END = 28;
    int FOR_STATEMENT = 29;
    int FOR_BLOCK_BEGIN = 30;
    int FOR_BLOCK_END = 31;
    int SWITCH_STATEMENT = 32;
    int SWITCH_BLOCK_BEGIN = 33;
    int SWITCH_BLOCK_END = 34;
    int SWITCH_CASE = 35;
    int SELECT_STATEMENT = 36;
    int SELECT_BLOCK_BEGIN = 37;
    int SELECT_BLOCK_END = 38;
    int CASE_BLOCK_BEGIN = 39;
    int CASE_BLOCK_END = 40;

    // Statements

    int VARIABLE_DECLARATION = 41;
    int CONSTANT_DECLARATION = 42;
    int FUNCTION_LITERAL = 43;
    int ASSIGNMENT = 44;
    int SEND_STATEMENT = 45;
    int RECEIVE_STATEMENT = 46;
    int INVOCATION = 47;
    int ARGUMENT = 48;
    int STATEMENT_BLOCK_BEGIN = 49;
    int STATEMENT_BLOCK_END = 50;

    // Object creation

    int ARRAY_ELEMENT = 51;
    int MAP_ELEMENT = 52;
    int SLICE_ELEMENT = 53;
    int NAMED_TYPE_ELEMENT = 54;
    int ARRAY_CONSTRUCTOR = 55;
    int MAP_CONSTRUCTOR = 56;
    int SLICE_CONSTRUCTOR = 57;
    int NAMED_TYPE_CONSTRUCTOR = 58;

    // Control flow keywords

    int RETURN = 59;
    int BREAK = 60;
    int CONTINUE = 61;
    int FALLTHROUGH = 62;
    int GOTO = 63;
    int GO = 64;
    int DEFER = 65;

    int NUM_DIFF_TOKENS = 66;

}
