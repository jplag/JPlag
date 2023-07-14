package de.jplag.llvmir;

import de.jplag.TokenType;

public enum LLVMIRTokenType implements TokenType {

    // Functions
    FUNCTION_BODY_BEGIN("FUNC{"),
    FUNCTION_BODY_END("}FUNC"),
    FUNCTION_DECL("FUNC_DECL"),
    FUNCTION_DEF("FUNC_DEF"),

    // Terminator Instructions
    RETURN("RET"),
    BRANCH("BR"),
    COND_BRANCH("COND_BR"),
    INVOKE("TRY"),
    CALL_BRANCH("CALL_BR"),
    SWITCH("SWITCH"),
    INDIRECT_BRANCH("INDIRECT_BRANCH"),
    RESUME("THROW"),
    CATCH_SWITCH("CATCH_SWITCH"),
    CATCH_RETURN("CATCH"),
    CLEAN_UP_RETURN("CLEAN_UP_RET"),

    // Operations
    OPERATION("OP"),

    // Memory Operations
    ALLOCATION("ALLOC"),
    LOAD("LOAD"),
    STORE("STORE"),
    FENCE("FENCE"),
    COMPARE_EXCHANGE("CMP_XCHG"),
    ATOMIC_CRMW("ATOMIC_CRMW"),
    GET_ELEMENT_POINTER("GET_ELEMENT_PTR"),

    // Conversion Operations
    CONVERSION("CONV"),

    // Other Operations
    COMPARISON("COMP"),
    PHI("PHI"),
    SELECT("SELECT"),
    FREEZE("FREEZE"),
    CALL("CALL"),
    VA_ARG("VA_ARG"),
    LANDING_PAD("LANDING_PAD"),
    CATCH_PAD("CATCH_PAD"),
    CLEAN_UP_PAD("CLEAN_UP_PAD");

    private final String description;

    public String getDescription() {
        return description;
    }

    LLVMIRTokenType(String description) {
        this.description = description;
    }
}
