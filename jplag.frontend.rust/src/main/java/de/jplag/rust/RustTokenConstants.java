package de.jplag.rust;

import de.jplag.TokenConstants;

public interface RustTokenConstants extends TokenConstants {
    int NONE = -1;

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
    int STRUCT_BODY_BEGIN = 15;
    int STRUCT_BODY_END = 16;

    int STRUCT_FIELD = 17;

    int UNION = 18;
    int UNION_BODY_START = 19;
    int UNION_BODY_END = 20;

    int TRAIT = 21;
    int TRAIT_BODY_START = 22;
    int TRAIT_BODY_END = 23;

    int IMPLEMENTATION = 24;
    int IMPLEMENTATION_BODY_START = 25;
    int IMPLEMENTATION_BODY_END = 26;

    int ENUM = 27;
    int ENUM_BODY_START = 28;
    int ENUM_BODY_END = 29;
    int ENUM_ITEM = 30;

    int MACRO_RULES_DEFINITION = 31;
    int MACRO_RULES_DEFINITION_BODY_START = 32;
    int MACRO_RULES_DEFINITION_BODY_END = 33;

    int MACRO_RULE = 34;
    int MACRO_RULE_BODY_START = 35;
    int MACRO_RULE_BODY_END = 36;

    int MACRO_INVOCATION = 37;
    int MACRO_INVOCATION_BODY_START = 38;
    int MACRO_INVOCATION_BODY_END = 39;

    int EXTERN_BLOCK = 40;
    int EXTERN_BLOCK_START = 41;
    int EXTERN_BLOCK_END = 42;
    int TYPE_ALIAS = 43;
    int STATIC_ITEM = 44;

    int EXTERN_CRATE = 45;

    int IF_STATEMENT = 46;
    int IF_BODY_START = 47;
    int IF_BODY_END = 48;
    int ELSE_STATEMENT = 49;
    int ELSE_BODY_START = 50;
    int ELSE_BODY_END = 51;

    int LABEL = 52;
    int LOOP_STATEMENT = 53;
    int LOOP_BODY_START = 54;
    int LOOP_BODY_END = 55;
    int FOR_STATEMENT = 56;
    int FOR_BODY_START = 57;
    int FOR_BODY_END = 58;

    int BREAK = 59;

    int MATCH_EXPRESSION = 60;
    int MATCH_BODY_START = 61;
    int MATCH_BODY_END = 62;
    int MATCH_CASE = 63;
    int MATCH_GUARD = 64;

    int INNER_BLOCK_START = 65;
    int INNER_BLOCK_END = 66;

    int ARRAY_BODY_START = 67;
    int ARRAY_BODY_END = 68;
    int ARRAY_ELEMENT = 69;

    int TUPLE = 70;
    int TUPLE_START = 71;
    int TUPLE_END = 72;
    int TUPLE_ELEMENT = 73;

    int CLOSURE = 74;
    int CLOSURE_BODY_START = 75;
    int CLOSURE_BODY_END = 76;

    int APPLY = 77;
    int ARGUMENT = 78;
    int ASSIGNMENT = 79;

    int VARIABLE_DECLARATION = 80;

    int RETURN = 81;

    int NUMBER_DIFF_TOKENS = 82;

}
