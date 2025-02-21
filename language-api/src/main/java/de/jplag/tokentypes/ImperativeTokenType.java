package de.jplag.tokentypes;

import de.jplag.TokenType;

public enum ImperativeTokenType implements TokenType {
    VARIABLE_DEFINITION("VAR_DEF"),
    ASSIGNMENT("ASSIGN"),

    STRUCTURE_DEFINITION("STRUCT_DEF"),
    STRUCTURE_END("STRUCT_END"),
    STRUCTURE_MEMBER("STRUCT_MEMBER"),

    IF("IF"),
    ELSE("ELSE"),
    IF_END("IF_END"),
    SWITCH("SWITCH"),
    SWITCH_END("SWITCH_END"),
    CASE("CASE"),
    CASE_END("CASE_END"),
    LOOP("LOOP"),
    LOOP_END("LOOP_END"),
    GOTO("GOTO"), // TODO different context?
    BREAK("BREAK"),
    CONTINUE("CONTINUE"),

    FUNCTION_DEFINITION("FUNCTION_DEF"),
    FUNCTION_END("FUNCTION_END"),
    RETURN("RETURN"),
    CALL("CALL");

    private String description;

    ImperativeTokenType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
