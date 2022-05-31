package de.jplag.go;

import de.jplag.TokenConstants;

public interface GoTokenConstants extends TokenConstants {

    enum TokenType {
        FILE_END,
        SEPARATOR_TOKEN,
        STRUCT_DECLARATION
    }

    // Top level structures
    int STRUCT_DECLARATION_BEGIN = 2, STRUCT_BODY_BEGIN = 3, STRUCT_BODY_END = 4,

            MEMBER_DECLARATION = 5,

            FUNCTION_DECLARATION = 6, METHOD_DECLARATION = 7, FUNCTION_PARAMETER = 8, FUNCTION_BODY_BEGIN = 9, FUNCTION_BODY_END = 10,

            // Control flow statements

            IF_STATEMENT = 11,

            IF_BLOCK_BEGIN = 12, IF_BLOCK_END = 13, ELSE_BLOCK_BEGIN = 14, ELSE_BLOCK_END = 15,

            FOR_STATEMENT = 16, FOR_BLOCK_BEGIN = 17, FOR_BLOCK_END = 18,

            SWITCH_STATEMENT = 19, SWITCH_BLOCK_BEGIN = 20, SWITCH_BLOCK_END = 21,

            SWITCH_CASE = 22, CASE_BLOCK_BEGIN = 23, CASE_BLOCK_END = 24,

            // Statements

            FUNCTION_LITERAL = 25,

            ASSIGNMENT = 26, INVOCATION = 27, ARGUMENT = 28, STATEMENT_BLOCK_BEGIN = 29, STATEMENT_BLOCK_END = 30,

            // Object Creation
            STRUCT_CONSTRUCTOR = 31, STRUCT_VALUE = 32,

            ARRAY_CONSTRUCTOR = 33, SLICE_CONSTRUCTOR = 34, MAP_CONSTRUCTOR = 35,

            // Control Flow Keywords

            RETURN = 36, BREAK = 37, CONTINUE = 38, GOTO = 39,
            GO = 40, DEFER = 41;

    int NUM_DIFF_TOKENS = 42;

}
