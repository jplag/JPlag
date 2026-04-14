package de.jplag.bash;

import de.jplag.TokenType;

/**
 * Token types for Bash.
 */
public enum BashTokenType implements TokenType {
    FUNCTION("FUNCTION"),
    FUNCTION_BODY_START("FUNC{"),
    FUNCTION_BODY_END("}FUNC"),

    IF_STATEMENT("IF"),
    IF_BODY_START("IF{"),
    IF_BODY_END("}IF"),
    ELIF_STATEMENT("ELIF"),
    ELIF_BODY_START("ELIF{"),
    ELIF_BODY_END("}ELIF"),
    ELSE_STATEMENT("ELSE"),
    ELSE_BODY_START("ELSE{"),
    ELSE_BODY_END("}ELSE"),

    FOR_STATEMENT("FOR"),
    FOR_BODY_START("FOR{"),
    FOR_BODY_END("}FOR"),

    WHILE_STATEMENT("WHILE"),
    WHILE_BODY_START("WHILE{"),
    WHILE_BODY_END("}WHILE"),

    UNTIL_STATEMENT("UNTIL"),
    UNTIL_BODY_START("UNTIL{"),
    UNTIL_BODY_END("}UNTIL"),

    CASE_STATEMENT("CASE"),
    CASE_BODY_START("CASE{"),
    CASE_BODY_END("}CASE"),
    CASE_ITEM("CASE_ITEM"),

    SELECT_STATEMENT("SELECT"),
    SELECT_BODY_START("SELECT{"),
    SELECT_BODY_END("}SELECT"),

    SUBSHELL_START("SUBSHELL("),
    SUBSHELL_END(")SUBSHELL"),

    BRACE_GROUP_START("BRACE{"),
    BRACE_GROUP_END("}BRACE"),

    VARIABLE_ASSIGNMENT("ASSIGN"),
    LOCAL_VARIABLE("LOCAL"),
    DECLARE_VARIABLE("DECLARE"),
    EXPORT_VARIABLE("EXPORT"),
    READONLY_VARIABLE("READONLY"),

    PIPELINE("PIPE"),
    AND_LOGICAL("AND"),
    OR_LOGICAL("OR"),

    COMMAND("CMD"),
    ARGUMENT("ARG"),

    REDIRECTION("REDIR"),

    COMMAND_SUBSTITUTION_START("CMD_SUB("),
    COMMAND_SUBSTITUTION_END(")CMD_SUB"),

    ARITHMETIC_EXPRESSION("ARITH"),

    TEST_EXPRESSION("TEST"),

    RETURN_STATEMENT("RETURN"),
    BREAK_STATEMENT("BREAK"),
    CONTINUE_STATEMENT("CONTINUE");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }

    BashTokenType(String description) {
        this.description = description;
    }
}
