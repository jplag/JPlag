package de.jplag.rust;

import de.jplag.TokenConstants;

public interface RustTokenConstants extends TokenConstants {

    int INNER_ATTRIBUTE = 2;
    int OUTER_ATTRIBUTE = 3;

    int USE_DECLARATION = 4;
    int USE_ITEM = 5;

    int MODULE = 6;
    int MODULE_START = 7;
    int MODULE_END = 8;

    int FUNCTION = 9;
    int TYPE_PARAMETER = 10;
    int FUNCTION_PARAMETER = 11;
    int FUNCTION_BODY_START = 12;
    int FUNCTION_BODY_END = 13;

    int STRUCT = 14;
    int STRUCT_BODY_START = 15;
    int STRUCT_BODY_END = 16;
    int STRUCT_INITIALISATION = 17;

    int STRUCT_FIELD = 18;

    int UNION = 19;
    int UNION_BODY_START = 20;
    int UNION_BODY_END = 21;

    int TRAIT = 22;
    int TRAIT_BODY_START = 23;
    int TRAIT_BODY_END = 24;

    int IMPLEMENTATION = 25;
    int IMPLEMENTATION_BODY_START = 26;
    int IMPLEMENTATION_BODY_END = 27;

    int ENUM = 28;
    int ENUM_BODY_START = 29;
    int ENUM_BODY_END = 30;
    int ENUM_ITEM = 31;

    int MACRO_RULES_DEFINITION = 32;
    int MACRO_RULES_DEFINITION_BODY_START = 33;
    int MACRO_RULES_DEFINITION_BODY_END = 34;

    int MACRO_RULE = 35;
    int MACRO_RULE_BODY_START = 36;
    int MACRO_RULE_BODY_END = 37;

    int MACRO_INVOCATION = 38;
    int MACRO_INVOCATION_BODY_START = 39;
    int MACRO_INVOCATION_BODY_END = 40;

    int EXTERN_BLOCK = 41;
    int EXTERN_BLOCK_START = 42;
    int EXTERN_BLOCK_END = 43;
    int TYPE_ALIAS = 44;
    int STATIC_ITEM = 45;

    int EXTERN_CRATE = 46;

    int IF_STATEMENT = 47;
    int IF_BODY_START = 48;
    int IF_BODY_END = 49;
    int ELSE_STATEMENT = 50;
    int ELSE_BODY_START = 51;
    int ELSE_BODY_END = 52;

    int LABEL = 53;
    int LOOP_STATEMENT = 54;
    int LOOP_BODY_START = 55;
    int LOOP_BODY_END = 56;
    int FOR_STATEMENT = 57;
    int FOR_BODY_START = 58;
    int FOR_BODY_END = 59;

    int BREAK = 60;

    int MATCH_EXPRESSION = 61;
    int MATCH_BODY_START = 62;
    int MATCH_BODY_END = 63;
    int MATCH_CASE = 64;
    int MATCH_GUARD = 65;

    int INNER_BLOCK_START = 66;
    int INNER_BLOCK_END = 67;

    int ARRAY_BODY_START = 68;
    int ARRAY_BODY_END = 69;
    int ARRAY_ELEMENT = 70;

    int TUPLE = 71;
    int TUPLE_START = 72;
    int TUPLE_END = 73;
    int TUPLE_ELEMENT = 74;

    int CLOSURE = 75;
    int CLOSURE_BODY_START = 76;
    int CLOSURE_BODY_END = 77;

    int APPLY = 78;
    int ARGUMENT = 79;
    int ASSIGNMENT = 80;

    int VARIABLE_DECLARATION = 81;

    int TYPE_ARGUMENT = 82;

    int RETURN = 83;

    int NUMBER_DIFF_TOKENS = 84;

}
