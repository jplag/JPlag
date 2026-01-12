package de.jplag.java_cpg.token.characteristic;

public enum CharacteristicVectorDimension {
    VARIABLE_DECLARATION, //
    METHOD_DECLARATION, //
    LOOP_CARRIED_DEPENDENCY,
    ARRAY_SELECTOR,
    LOGICAL_EXPRESSION,//
    NUMERICAL_EXPRESSION,//
    CONDITIONAL_EXPRESSION,//
    ASSIGNMENT_EXPRESSION,//
    METHOD_INVOCATION,//
    RETURN_STATEMENT,//
    CASE,//
    SWITCH,//
    LAMBDA_EXPRESSION,//
    CONSTRUCTOR_INVOCATION,//
    CLASS_OR_ARRAY_CREATOR,//
    IF,//
    ASSERT,//
    THROW, // not found
    TRY,//
    CATCH,//
    FINALLY, // not found
    LOOP;

    public static final int DIMENSION_COUNT = values().length;
}
