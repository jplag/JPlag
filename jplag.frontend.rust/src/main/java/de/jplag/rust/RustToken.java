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

            case IMPL -> "IMPL";
            case IMPL_BODY_START -> "IMPL{";
            case IMPL_BODY_END -> "}IMPL";

            case ENUM -> "ENUM";
            case ENUM_BODY_START -> "ENUM{";
            case ENUM_BODY_END -> "}ENUM";

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

            case IF_STATEMENT -> "IF";
            case IF_BODY_START -> "IF{";
            case IF_BODY_END -> "}IF";

            case LOOP_STATEMENT -> "LOOP";
            case LOOP_BODY_START -> "LOOP{";
            case LOOP_BODY_END -> "}LOOP";

            case INNER_BLOCK_START -> "INNER{";
            case INNER_BLOCK_END -> "}INNER";

            case ASSIGNMENT -> "ASSIGN";
            case VARIABLE_DECLARATION -> "VAR_DECL";

            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
