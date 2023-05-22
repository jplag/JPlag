package de.jplag.semantics;

/**
 * The scopes a variable can have. Scopes dictate a variable's visibility.
 */
public enum VariableScope {
    /**
     * The variable is visible in the entire file.
     */
    FILE,
    /**
     * The variable is only visible in the class it was declared in.
     */
    CLASS,
    /**
     * The variable is only visible in the local scope it was declared in.
     */
    LOCAL
}
