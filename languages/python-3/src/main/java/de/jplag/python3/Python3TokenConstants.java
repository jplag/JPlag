package de.jplag.python3;

import de.jplag.TokenConstants;

public interface Python3TokenConstants extends TokenConstants {
    int IMPORT = 2;
    int CLASS_BEGIN = 3;
    int CLASS_END = 4;
    int METHOD_BEGIN = 5;
    int METHOD_END = 6;
    int ASSIGN = 7;
    int WHILE_BEGIN = 8;
    int WHILE_END = 9;
    int FOR_BEGIN = 10;
    int FOR_END = 11;
    int TRY_BEGIN = 12;
    int EXCEPT_BEGIN = 13;
    int EXCEPT_END = 14;
    int FINALLY = 15;
    int IF_BEGIN = 16;
    int IF_END = 18;
    int APPLY = 19;
    int BREAK = 20;
    int CONTINUE = 21;
    int RETURN = 22;
    int RAISE = 23;
    int DEC_BEGIN = 24;
    int DEC_END = 25;
    int LAMBDA = 26;
    int ARRAY = 27;
    int ASSERT = 28;
    int YIELD = 29;
    int DEL = 30;
    int WITH_BEGIN = 31;
    int WITH_END = 32;

    int NUM_DIFF_TOKENS = 33;
}
