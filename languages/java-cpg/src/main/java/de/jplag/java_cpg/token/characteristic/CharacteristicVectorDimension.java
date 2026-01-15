package de.jplag.java_cpg.token.characteristic;

/**
 * This enum represents the dimensions of a characteristic vector.
 */
public enum CharacteristicVectorDimension {
    VARIABLE_DECLARATION,
    METHOD_DECLARATION,
    LOOP_CARRIED_DEPENDENCY,
    ARRAY_SELECTOR,
    LOGICAL_EXPRESSION,
    NUMERICAL_EXPRESSION,
    CONDITIONAL_EXPRESSION,
    ASSIGNMENT_EXPRESSION,
    METHOD_INVOCATION,
    RETURN_STATEMENT,
    CASE,
    SWITCH,
    LAMBDA_EXPRESSION,//
    CONSTRUCTOR_INVOCATION,
    CLASS_OR_ARRAY_CREATOR,
    IF,
    ASSERT,
    THROW,
    TRY,
    CATCH,
    LOOP;

    /**
     * The number of dimensions in a characteristic vector.
     */
    public static final int DIMENSION_COUNT = values().length;
}
