package de.jplag.java;

import de.jplag.TokenConstants;

public interface JavaTokenConstants extends TokenConstants {

    int J_PACKAGE = 2;                // check
    int J_IMPORT = 3;                // check
    int J_CLASS_BEGIN = 4;            // check
    int J_CLASS_END = 5;            // check
    int J_METHOD_BEGIN = 6;        // check
    int J_METHOD_END = 7;            // check
    int J_VARDEF = 8;                // check
    int J_SYNC_BEGIN = 9;            // check
    int J_SYNC_END = 10;            // check
    int J_DO_BEGIN = 11;            // check
    int J_DO_END = 12;                // check
    int J_WHILE_BEGIN = 13;        // check
    int J_WHILE_END = 14;            // check
    int J_FOR_BEGIN = 15;            // check
    int J_FOR_END = 16;            // check
    int J_SWITCH_BEGIN = 17;        // check
    int J_SWITCH_END = 18;            // check
    int J_CASE = 19;                // check
    int J_TRY_BEGIN = 20;            // check
    int J_CATCH_BEGIN = 21;        // check
    int J_CATCH_END = 22;        // check
    int J_FINALLY = 23;            // check
    int J_IF_BEGIN = 24;            // check
    int J_ELSE = 25;                // check
    int J_IF_END = 26;                // check
    int J_COND = 27;                // check
    int J_BREAK = 28;                // check
    int J_CONTINUE = 29;            // check
    int J_RETURN = 30;                // check
    int J_THROW = 31;                // check
    int J_IN_CLASS_BEGIN = 32;        //
    int J_IN_CLASS_END = 33;        //
    int J_APPLY = 34;                // check
    int J_NEWCLASS = 35;            // check
    int J_NEWARRAY = 36;            // check
    int J_ASSIGN = 37;                // check
    int J_INTERFACE_BEGIN = 38;    // check
    int J_INTERFACE_END = 39;        // check
    int J_CONSTR_BEGIN = 40;        //
    int J_CONSTR_END = 41;            //
    int J_INIT_BEGIN = 42;            // check
    int J_INIT_END = 43;            // check
    int J_VOID = 44;                //
    int J_ARRAY_INIT_BEGIN = 45;    // check
    int J_ARRAY_INIT_END = 46;    // check

    // new in 1.5:
    int J_ENUM_BEGIN = 47;            // check
    int J_ENUM_CLASS_BEGIN = 48;    // ?? doesn't exist in JAVAC
    int J_ENUM_END = 49;            // check
    int J_GENERIC = 50;            // check
    int J_ASSERT = 51;                // check

    int J_ANNO = 52;                // check
    int J_ANNO_MARKER = 53;        // ??
    int J_ANNO_M_BEGIN = 54;        // ??
    int J_ANNO_M_END = 55;            // ??
    int J_ANNO_T_BEGIN = 56;        // check
    int J_ANNO_T_END = 57;            // check
    int J_ANNO_C_BEGIN = 58;        // ??
    int J_ANNO_C_END = 59;            // ??

    // new in 1.7
    int J_TRY_WITH_RESOURCE = 60;    // check

    // new in 1.9
    int J_REQUIRES = 61;            // check
    int J_PROVIDES = 62;            // check
    int J_EXPORTS = 63;            // check
    int J_MODULE_BEGIN = 64;       // check
    int J_MODULE_END = 65;        // check

    // new in 13
    int J_YIELD = 66;

    // new in 17
    int J_DEFAULT = 67;
    int J_RECORD_BEGIN = 68;
    int J_RECORD_END = 69;

    final static int NUM_DIFF_TOKENS = 70;	    // @formatter:on
}
