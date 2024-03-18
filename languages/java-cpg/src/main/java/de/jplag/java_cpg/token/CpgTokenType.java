package de.jplag.java_cpg.token;

import de.jplag.TokenType;

/**
 * A {@link CpgTokenType} represents a type of {@link CpgToken} related to a syntactic element of code.
 */
public enum CpgTokenType implements TokenType {

    RECORD_DECL_BEGIN("CLASS{"),
    RECORD_DECL_END("}CLASS"),

    ENUM_DECL_BEGIN("ENUM{"),
    ENUM_DECL_END("}ENUM"),
    ENUM_ELEMENT("ENUM_ELEMENT"),

    FIELD_DECL("FIELD_DECL", false),

    METHOD_DECL_BEGIN("METHOD_DECL"),
    METHOD_PARAM("PARAM"),
    METHOD_BODY_BEGIN("METHOD_BODY{"),
    METHOD_BODY_END("}METHOD_BODY"),

    VARIABLE_DECL("VAR_DECL"),
    ASSIGNMENT("ASSIGN"),

    METHOD_CALL("APPLY"),
    METHOD_ARGUMENT("ARG"),

    CONSTRUCTOR_CALL("NEW()"),
    NEW_ARRAY("NEW_ARRAY"),

    IF_STATEMENT("IF"),
    IF_BLOCK_BEGIN("THEN{"),
    IF_BLOCK_END("}THEN"),
    ELSE_BLOCK_BEGIN("ELSE{"),
    ELSE_BLOCK_END("}ELSE"),

    WHILE_STATEMENT("WHILE"),
    WHILE_BLOCK_START("W_DO{"),
    WHILE_BLOCK_END("}W_DO"),

    DO_WHILE_STATEMENT("DO_WHILE"),
    DO_WHILE_BLOCK_START("DW_DO{"),
    DO_WHILE_BLOCK_END("}DW_DO"),

    FOR_STATEMENT("FOR"),
    FOR_BLOCK_BEGIN("FOR{"),
    FOR_BLOCK_END("}FOR"),
    SWITCH_STATEMENT("SWITCH{"),
    SWITCH_BLOCK_START("SWITCH{"),
    SWITCH_BLOCK_END("}SWITCH"),
    CASE_STATEMENT("CASE"),
    DEFAULT_STATEMENT("DEFAULT"),

    BREAK("BREAK"),
    CONTINUE("CONTINUE"),
    RETURN("RETURN"),

    GOTO_STATEMENT("GOTO"),

    ASSERT_STATEMENT("ASSERT"),

    TRY_STATEMENT("TRY{"),
    TRY_BLOCK_START("TRY{"),
    TRY_BLOCK_END("}TRY"),
    THROW("THROW"),
    CATCH_CLAUSE_BEGIN("CATCH{"),
    CATCH_CLAUSE_END("}CATCH"),
    FINALLY_CLAUSE_BEGIN("FINALLY{"),
    FINALLY_CLAUSE_END("}FINALLY"),

    SYNCHRONIZED_STATEMENT("SYNC"),
    SYNCHRONIZED_BLOCK_START("SYNC{"),
    SYNCHRONIZED_BLOCK_END("}SYNC"),

    // TODO: set excluded
    BLOCK_BEGIN("{", false),
    BLOCK_END("}", false);

    private final boolean isExcluded;
    private final String description;

    CpgTokenType(String description) {
        this(description, false);
    }

    CpgTokenType(String description, boolean excluded) {
        this.description = description;
        this.isExcluded = excluded;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Boolean isExcludedFromMatching() {
        return this.isExcluded;
    }
}
