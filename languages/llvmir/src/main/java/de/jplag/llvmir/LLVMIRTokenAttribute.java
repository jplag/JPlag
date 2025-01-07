package de.jplag.llvmir;

import de.jplag.TokenAttribute;

/**
 * LLVM IR token types extracted by this language module.
 */
public enum LLVMIRTokenAttribute implements TokenAttribute {

    FILENAME("FILENAME"),

    // Functions
    FUNCTION_BODY_BEGIN("FUNC{"),
    FUNCTION_BODY_END("}FUNC"),
    BASIC_BLOCK_BEGIN("BLOCK{"),
    BASIC_BLOCK_END("}BLOCK"),
    FUNCTION_DECLARATION("FUNC_DECL"),
    FUNCTION_DEFINITION("FUNC_DEF"),

    GLOBAL_VARIABLE("GLOB_VAR"),
    ASSEMBLY("ASM"),
    TYPE_DEFINITION("TYPE_DEF"),

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
    INVOKE("INVOKE"),
    CALL_BRANCH("CALL_BR"),
    RESUME("RESUME"),
    CATCH_SWITCH("CATCH_SWITCH"),
    CATCH_RETURN("CATCH_RET"),
    CLEAN_UP_RETURN("CLEAN_UP_RET"),

    // Binary Operations
    ADDITION("ADD"),
    SUBTRACTION("SUB"),
    MULTIPLICATION("MUL"),
    DIVISION("DIV"),
    REMAINDER("REM"),

    // Bitwise instruction
    SHIFT("SHIFT"),
    AND("AND"),
    OR("OR"),
    XOR("XOR"),

    // Vector operations
    EXTRACT_ELEMENT("EXTRACT_ELEM"),
    INSERT_ELEMENT("INSERT_ELEM"),
    SHUFFLE_VECTOR("SHUFFLE_VEC"),

    // Aggregate Operations
    EXTRACT_VALUE("EXTRACT_VAL"),
    INSERT_VALUE("INSERT_VAL"),

    // Memory Operations
    ALLOCATION("ALLOC"),
    LOAD("LOAD"),
    STORE("STORE"),
    FENCE("FENCE"),
    COMPARE_EXCHANGE("CMP_XCHG"),
    ATOMIC_READ_MODIFY_WRITE("ATOMIC_RMW"),
    ATOMIC_ORDERING("ATOMIC"),
    GET_ELEMENT_POINTER("GET_ELEMENT_PTR"),

    // Conversion Operations
    BITCAST("BITCAST"),
    CONVERSION("CONV"),

    // Other Operations
    COMPARISON("COMP"),
    PHI("PHI"),
    SELECT("SELECT"),
    CALL("CALL"),
    VARIABLE_ARGUMENT("VA_ARG"),
    LANDING_PAD("LANDING_PAD"),
    CLAUSE("CLAUSE"),
    CATCH_PAD("CATCH_PAD"),
    CLEAN_UP_PAD("CLEAN_UP_PAD");

    private final String description;

    LLVMIRTokenAttribute(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
