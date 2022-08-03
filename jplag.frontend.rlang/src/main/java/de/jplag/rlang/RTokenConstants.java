package de.jplag.rlang;

import de.jplag.TokenConstants;

/**
 * Tokens in R that are deemed important when comparing submissions for plagiarisms. Based on an R frontend for JPlag
 * v2.15 by Olmo Kramer, see their <a href="https://github.com/CodeGra-de/jplag/tree/master/jplag.frontend.R">JPlag
 * fork</a>.
 * @author Robin Maisch
 */
public interface RTokenConstants extends TokenConstants {

    int BEGIN_FUNCTION = 2;
    int END_FUNCTION = 3;
    int FUNCTION_CALL = 4;
    int NUMBER = 5;
    int STRING = 6;
    int BOOL = 7;
    int ASSIGN = 8;
    int ASSIGN_FUNC = 9;
    int ASSIGN_LIST = 10;
    int HELP = 11;
    int INDEX = 12;
    int PACKAGE = 13;
    int IF_BEGIN = 14;
    int IF_END = 15;
    int FOR_BEGIN = 16;
    int FOR_END = 17;
    int WHILE_BEGIN = 18;
    int WHILE_END = 19;
    int REPEAT_BEGIN = 20;
    int REPEAT_END = 21;
    int NEXT = 22;
    int BREAK = 23;
    int COMPOUND_BEGIN = 24;
    int COMPOUND_END = 25;

    int NUM_DIFF_TOKENS = 26;
}
