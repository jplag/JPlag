package de.jplag.llvmir;

import de.jplag.TokenType;

public enum LLVMIRTokenType implements TokenType {

    FUNCTION_BODY_BEGIN("FUNC{"),
    FUNCTION_BODY_END("}FUNC"),
    FUNCTION_DECL("FUNC_DECL"),
    FUNCTION_DEF("FUNC_DEF"),
    RETURN("RET"),
    BRANCH("BR"),
    INVOKE("INVOKE"),
    CALL_BRANCH("CALL_BR"),
    RESUME("RESUME"),
    CATCH_SWITCH("CATCH"),
    CATCH_RETURN("CATCH_RET"),
    CLEAN_UP_RETURN("CLEAN_UP_RET"),
    OPERATION("OP"),
    ALLOCATION("ALLOC"),
    LOAD("LOAD"),
    STORE("STORE"),
    GET_ELEMENT_POINTER("GET_ELEMENT_PTR"),
    CONVERSION("CONV"),
    COMPARISON("COMP"),
    PHI("PHI"),
    CALL("CALL");

    private final String description;

    public String getDescription() {
        return description;
    }

    LLVMIRTokenType(String description) {
        this.description = description;
    }
}
