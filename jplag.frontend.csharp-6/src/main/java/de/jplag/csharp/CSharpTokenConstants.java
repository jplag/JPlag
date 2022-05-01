package de.jplag.csharp;

import de.jplag.TokenConstants;

/**
 * Based on the legacy CSharp-1.2 constants. The constants L_BRACE, R_BRACE, and STATIC_CONSTR were removed. Moreover,
 * the following constants were added: METHOD_BEGIN, METHOD_END, STRUCT, IF_BEGIN, CLASS, INTERFACE, ENUM_BEGIN,
 * ENUM_END and ENUMERAL.
 * @author Timur Saglam
 */
public interface CSharpTokenConstants extends TokenConstants {
    /*
     * Token constants:
     */
    int INVOCATION = 2;
    int OBJECT_CREATION = 3;
    int ARRAY_CREATION = 4;
    int ASSIGNMENT = 5;
    int DECLARE_VAR = 6;
    int DECLARE_CONST = 7;
    int IF = 8;
    int ELSE = 9; // TODO TS does not exist
    int SWITCH_BEGIN = 10;
    int SWITCH_END = 11;
    int CASE = 12;
    int DO = 13;
    int WHILE = 14;
    int FOR = 15;
    int FOREACH = 16;
    int BREAK = 17;
    int CONTINUE = 18;
    int GOTO = 19;
    int RETURN = 20;
    int THROW = 21;
    int CHECKED = 22;
    int UNCHECKED = 23;
    int LOCK = 24;
    int USING = 25;
    int TRY = 26;
    int CATCH = 27;
    int FINALLY = 28;
    int NAMESPACE_BEGIN = 29;
    int NAMESPACE_END = 30;
    int USING_DIRECTIVE = 31;
    int CLASS_BEGIN = 32;
    int CLASS_END = 33;
    int METHOD = 34;
    int PROPERTY = 35;
    int EVENT = 36;
    int INDEXER = 37;
    int OPERATOR = 38;
    int CONSTRUCTOR = 39;
    int DESTRUCTOR = 40;
    int STRUCT_BEGIN = 41;
    int STRUCT_END = 42;
    int INTERFACE_BEGIN = 43;
    int INTERFACE_END = 44;
    int ENUM = 45;
    int DELEGATE = 46;
    int ATTRIBUTE = 47;
    int END_IF = 48;
    int UNSAFE = 49;
    int FIXED = 50;
    int METHOD_BEGIN = 51;
    int METHOD_END = 52;
    int STRUCT = 53;
    int IF_BEGIN = 54;
    int CLASS = 55;
    int INTERFACE = 56;
    int ENUM_BEGIN = 57;
    int ENUM_END = 58;
    int ENUMERAL = 59;

    /*
     * Number of token constants:
     */
    int NUM_DIFF_TOKENS = 61;
}
