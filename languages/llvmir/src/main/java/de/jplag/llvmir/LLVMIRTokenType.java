package de.jplag.llvmir;

import de.jplag.TokenType;

public enum LLVMIRTokenType implements TokenType {

    FILENAME("FILENAME"),

    // Functions
    FUNCTION_BODY_BEGIN("FUNC{"),
    FUNCTION_BODY_END("}FUNC"),
    FUNCTION_DECL("FUNC_DECL"),
    FUNCTION_DEF("FUNC_DEF"),

    GLOBAL_VARIABLE("GLOBAL_VAR"),
    ASSEMBLY("ASM"),

    // Constants
    STRUCTURE("STRUCT"),
    ARRAY("ARR"),
    VECTOR("VEC"),

    // Terminator Instructions
    RETURN("RET"),
    BRANCH("BR"),
    SWITCH("SWITCH"),
    CASE("CASE"),
    CONDITIONAL_BRANCH("COND_BR"),
    INVOKE("TRY"),
    CALL_BRANCH("CALL_BR"),
    RESUME("THROW"),
    CATCH_SWITCH("CATCH_SWITCH"),
    CATCH_RETURN("CATCH"),
    CLEAN_UP_RETURN("CLEAN_UP_RET"),

    // Binary Operations
    ADD("ADDITION"),
    SUB("SUBTRACTION"),
    MUL("MULTIPLICATION"),
    DIV("DIVISION"),
    REM("REMAINDER"),

    // Bitwise instruction
    SHIFT("SHIFT"),
    AND("AND"),
    OR("OR"),
    XOR("XOR"),

    // Vector operations
    EXTRACT_ELEM("EXTRACT_ELEMENT"),
    INSERT_ELEM("INSERT_ELEM"),
    SHUFFLE_VEC("SHUFFLE_VECTOR"),

    // Aggregate Operations
    EXTRACT_VAL("EXTRACT_VALUE"),
    INSERT_VAL("INSERT_VALUE"),

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
    CALL("CALL"),
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
