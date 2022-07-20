package de.jplag.rust;

import de.jplag.TokenConstants;

public interface RustTokenConstants extends TokenConstants {
    int NONE = -1;

    // TOP LEVEL ELEMENTS

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

    int IMPL = 24;
    int IMPL_BODY_START = 25;
    int IMPL_BODY_END = 26;

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

    int IF_BODY_START = 43;
    int IF_BODY_END = 44;

    int INNER_BLOCK_START = 45;
    int INNER_BLOCK_END = 46;

    int ASSIGNMENT = 47;

    int VARIABLE_DECLARATION = 48;

    int NUMBER_DIFF_TOKENS = 49;

}
