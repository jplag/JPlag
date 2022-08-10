package de.jplag.rust;

import static de.jplag.rust.RustTokenConstants.*;

import de.jplag.Token;

public class RustToken extends Token {
    public RustToken(int type, String currentFile, int line, int start, int length) {
        super(type, currentFile, line, start, length);
    }

    @Override
    protected String type2string() {
        return switch (type) {
            case FILE_END -> "<EOF>";
            case SEPARATOR_TOKEN -> "--------";
            case INNER_ATTRIBUTE -> "INNER_ATTR";
            case OUTER_ATTRIBUTE -> "OUTER_ATTR";

            case USE_DECLARATION -> "USE";
            case USE_ITEM -> "USE_ITEM";

            case MODULE -> "MODULE";
            case MODULE_START -> "MODULE{";
            case MODULE_END -> "}MODULE";

            case FUNCTION -> "FUNCTION";
            case TYPE_PARAMETER -> "<T>";
            case FUNCTION_PARAMETER -> "PARAM";
            case FUNCTION_BODY_START -> "FUNC{";
            case FUNCTION_BODY_END -> "}FUNC";

            case STRUCT -> "STRUCT";
            case STRUCT_BODY_BEGIN -> "STRUCT{";
            case STRUCT_BODY_END -> "}STRUCT";

            case STRUCT_FIELD -> "FIELD";

            case UNION -> "UNION";
            case UNION_BODY_START -> "UNION{";
            case UNION_BODY_END -> "}UNION";

            case TRAIT -> "TRAIT";
            case TRAIT_BODY_START -> "TRAIT{";
            case TRAIT_BODY_END -> "}TRAIT";

            case IMPLEMENTATION -> "IMPL";
            case IMPLEMENTATION_BODY_START -> "IMPL{";
            case IMPLEMENTATION_BODY_END -> "}IMPL";

            case ENUM -> "ENUM";
            case ENUM_BODY_START -> "ENUM{";
            case ENUM_BODY_END -> "}ENUM";
            case ENUM_ITEM -> "ENUM_ITEM";

            case MACRO_RULES_DEFINITION -> "MACRO_RULES";
            case MACRO_RULES_DEFINITION_BODY_START -> "MACRO_RULES{";
            case MACRO_RULES_DEFINITION_BODY_END -> "}MACRO_RULES";

            case MACRO_RULE -> "MACRO_RULE";
            case MACRO_RULE_BODY_START -> "MACRO_RULE{";
            case MACRO_RULE_BODY_END -> "}MACRO_RULE";

            case MACRO_INVOCATION -> "MACRO()";
            case MACRO_INVOCATION_BODY_START -> "MACRO(){";
            case MACRO_INVOCATION_BODY_END -> "}MACRO()";

            case EXTERN_BLOCK -> "EXTERN";
            case EXTERN_BLOCK_START -> "EXTERN{";
            case EXTERN_BLOCK_END -> "}EXTERN";
            case TYPE_ALIAS -> "TYPE_ALIAS";
            case STATIC_ITEM -> "STATIC";

            case EXTERN_CRATE -> "EXTERN";

            case IF_STATEMENT -> "IF";
            case IF_BODY_START -> "IF{";
            case IF_BODY_END -> "}IF";
            case ELSE_STATEMENT -> "ELSE";
            case ELSE_BODY_START -> "ELSE{";
            case ELSE_BODY_END -> "ELSE}";

            case LABEL -> "LABEL";
            case LOOP_STATEMENT -> "LOOP";
            case LOOP_BODY_START -> "LOOP{";
            case LOOP_BODY_END -> "}LOOP";
            case FOR_STATEMENT -> "FOR";
            case FOR_BODY_START -> "FOR{";
            case FOR_BODY_END -> "}FOR";

            case BREAK -> "BREAK";

            case MATCH_EXPRESSION -> "MATCH";
            case MATCH_BODY_START -> "MATCH{";
            case MATCH_BODY_END -> "}MATCH";
            case MATCH_CASE -> "CASE";
            case MATCH_GUARD -> "GUARD";

            case INNER_BLOCK_START -> "INNER{";
            case INNER_BLOCK_END -> "}INNER";

            case ARRAY_BODY_START -> "ARRAY{";
            case ARRAY_BODY_END -> "}ARRAY";
            case ARRAY_ELEMENT -> "ARRAY_ELEM";

            case TUPLE -> "TUPLE";
            case TUPLE_START -> "TUPLE(";
            case TUPLE_END -> ")TUPLE";
            case TUPLE_ELEMENT -> "T_ELEM";

            case CLOSURE -> "CLOSURE";
            case CLOSURE_BODY_START -> "CLOSURE{";
            case CLOSURE_BODY_END -> "}CLOSURE";

            case APPLY -> "APPLY";
            case ARGUMENT -> "ARG";
            case ASSIGNMENT -> "ASSIGN";

            case VARIABLE_DECLARATION -> "VAR_DECL";

            case RETURN -> "RETURN";

            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
