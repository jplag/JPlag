package de.jplag.llvmir;

import de.jplag.TokenType;

public enum LLVMTokenType implements TokenType {
    ;

    private final String description;

    public String getDescription() {
        return description;
    }

    LLVMTokenType(String description) {
        this.description = description;
    }
}
