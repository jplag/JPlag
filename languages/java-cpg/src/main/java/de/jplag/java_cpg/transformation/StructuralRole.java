package de.jplag.java_cpg.transformation;

/**
 * Predefined {@link Role}s for use in graph patterns.
 */
public enum StructuralRole implements Role {
    ARGUMENT("argument"),
    ASSIGN_EXPRESSION("assignExpression"),
    BODY("body"),
    CLASS_DECLARATION("classDeclaration"),
    CONDITION("condition"),
    CONSTRUCTOR_DECLARATION("constructorDeclaration"),
    CONTAINING_FILE("containingFile"),
    CONTAINING_STATEMENT("containingStatement"),
    CONTAINING_RECORD("containingRecord"),
    DECLARATION("declaration"),
    DECLARATION_CONTAINER("declarationContainer"),
    DECLARATION_STATEMENT("declarationStatement"),
    DEFINING_RECORD("definingRecord"),
    DEFINING_RECORD_REFERENCE("definingRecordReference"),
    DO_STATEMENT("doStatement"),
    ELSE_STATEMENT("elseStatement"),
    EMPTY_FILE("emptyFile"),
    EMPTY_RECORD("emptyRecord"),
    FIELD_DECLARATION("fieldDeclaration"),
    FIELD_USAGE("fieldUsage"),
    FIELD_REFERENCE("fieldReference"),
    FIELD_VALUE("fieldValue"),
    FIELD_TYPE("fieldType"),
    FIRST_CONSTANT_USAGE("firstConstantUsage"),
    FOR_STATEMENT("forStatement"),
    GETTER_METHOD_REFERENCE("getMethodReference"),
    IF_STATEMENT("ifStatement"),
    INITIALIZATION_STATEMENT("initializationStatement"),
    INNER_CONDITION("innerCondition"),
    ITERATION_STATEMENT("iterationStatement"),
    MEMBER_CALL("memberCall"),
    METHOD_BLOCK("methodBlock"),
    METHOD_BODY("methodBody"),
    METHOD_DECLARATION("methodDeclaration"),
    METHOD_TYPE("methodType"),
    OPTIONAL_CLASS("optionalClass"),
    OPTIONAL_OBJECT("optionalObject"),
    PARAMETER_DECLARATION("parameterDeclaration"),
    PARAMETER_REFERENCE("parameterReference"),
    PROJECT("project"),
    RECORD_DECLARATION("recordDeclaration"),
    RETURN_STATEMENT("returnStatement"),
    RETURN_TYPE("returnType"),
    RETURN_VALUE("returnValue"),
    SCOPE_BLOCK("scopeBlock"),
    SURROUNDING_BLOCK("surroundingBlock"),
    THEN_STATEMENT("thenStatement"),
    THROW_EXCEPTION("throwException"),
    USING_RECORD("usingRecord"),
    VARIABLE_DECLARATION("variableDeclaration"),
    VARIABLE_VALUE("variableValue"),
    VOID_TYPE("voidType"),
    WHILE_STATEMENT("whileStatement"),
    WHILE_STATEMENT_BODY("whileStatementBody"),
    VARIABLE_USAGE("variableUsage"),
    WHILE_BLOCK("whileBlock"),
    WRAPPING_BLOCK("wrappingBlock");

    /**
     * The name of the role, serving to identify it.
     */
    final String name;

    StructuralRole(String wrappingBlock) {
        this.name = wrappingBlock;
    }

    @Override
    public String getName() {
        return name;
    }
}
