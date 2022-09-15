package de.jplag.scheme;

import de.jplag.TokenType;

public enum SchemeTokenType implements TokenType {
    S_BOOL("BOOL"),
    S_NUMBER("NUMBER"),
    S_CHAR("CHAR"),
    S_STRING("STRING"),
    S_ID("ID"),
    S_LIST_BEGIN("(LIST"),
    S_LIST_END("LIST)"),
    S_VECTOR_BEGIN("(VECTOR"),
    S_VECTOR_END("VECTOR)"),
    S_LITERAL("LITERAL"),
    S_QUOT_BEGIN("(QUOT"),
    S_QUOT_END("QUOT)"),
    S_CALL("CALL"),
    S_LAMBDA_BEGIN("(LAMBDA"),
    S_LAMBDA_END("LAMBDA)"),
    S_FORMAL_BEGIN("(FORMAL"),
    S_FORMAL_END("FORMAL)"),
    S_BODY_BEGIN("(BODY"),
    S_BODY_END("BODY)"),
    S_IF_BEGIN("(IF"),
    S_IF_END("IF)"),
    S_ALTERN("ALTERN"),
    S_ASSIGN_BEGIN("(ASSIGN"),
    S_ASSIGN_END("ASSIGN)"),
    S_COND_BEGIN("(COND"),
    S_COND_END("COND)"),
    S_ELSE("ELSE"),
    S_CASE_BEGIN("(CASE"),
    S_CASE_END("CASE)"),
    S_DO_BEGIN("(DO"),
    S_DO_END("DO)"),
    S_COMMAND("COMMAND"),
    S_DEF_BEGIN("(DEF"),
    S_DEF_END("DEF)"),
    S_BEGIN("("),
    S_END(")"),
    S_AND("AND"),
    S_OR("OR"),
    S_LET("LET"),
    S_DELAY("DELAY"),
    S_VAR("VAR");

    private final String description;

    public String getDescription() {
        return this.description;
    }

    SchemeTokenType(String description) {
        this.description = description;
    }
}
