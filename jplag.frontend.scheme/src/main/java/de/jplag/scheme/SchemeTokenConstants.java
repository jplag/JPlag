package de.jplag.scheme;

import de.jplag.TokenConstants;

public interface SchemeTokenConstants extends TokenConstants {

    int S_BOOL = 2;
    int S_NUMBER = 3;
    int S_CHAR = 4;
    int S_STRING = 5;
    int S_ID = 6;
    int S_LIST_BEGIN = 7;
    int S_LIST_END = 8;
    int S_VECTOR_BEGIN = 9;
    int S_VECTOR_END = 10;
    int S_LITERAL = 11;
    int S_QUOT_BEGIN = 12;
    int S_QUOT_END = 13;
    int S_CALL = 14;
    int S_LAMBDA_BEGIN = 15;
    int S_LAMBDA_END = 16;
    int S_FORMAL_BEGIN = 17;
    int S_FORMAL_END = 18;
    int S_BODY_BEGIN = 19;
    int S_BODY_END = 20;
    int S_IF_BEGIN = 21;
    int S_IF_END = 22;
    int S_ALTERN = 23;
    int S_ASSIGN_BEGIN = 24;
    int S_ASSIGN_END = 25;
    int S_COND_BEGIN = 26;
    int S_COND_END = 27;
    int S_ELSE = 28;
    int S_CASE_BEGIN = 29;
    int S_CASE_END = 30;
    int S_DO_BEGIN = 31;
    int S_DO_END = 32;
    int S_COMMAND = 33;
    int S_DEF_BEGIN = 34;
    int S_DEF_END = 35;
    int S_BEGIN = 36;
    int S_END = 37;
    int S_AND = 38;
    int S_OR = 39;
    int S_LET = 40;
    int S_DELAY = 41;
    int S_VAR = 42;

    int NUM_DIFF_TOKENS = 43;
}
