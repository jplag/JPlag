package de.jplag.java_cpg.transformation;

public record Role(String name) {
    public static final Role ARGUMENT = new Role("argument");
    public static final Role ASSIGN_EXPRESSION = new Role("assignExpression");

    public static final Role BODY = new Role("body");

    public static final Role CLASS_DECLARATION = new Role("classDeclaration");

    public static final Role CONDITION = new Role("condition");

    public static final Role CONSTRUCTOR_DECLARATION = new Role("constructorDeclaration");

    public static final Role CONTAINING_FILE = new Role("containingFile");
    public static final Role CONTAINING_STATEMENT = new Role("containingStatement");

    public static final Role CONTAINING_RECORD = new Role("containingRecord");

    public static final Role DECLARATION = new Role("declaration");

    public static final Role DECLARATION_CONTAINER = new Role("declarationContainer");

    public static final Role DECLARATION_STATEMENT = new Role("declarationStatement");

    public static final Role DEFINING_RECORD = new Role("definingRecord");

    public static final Role DEFINING_RECORD_REFERENCE = new Role("definingRecordReference");

    public static final Role DO_STATEMENT = new Role("doStatement");

    public static final Role ELSE_STATEMENT = new Role("elseStatement");

    public static final Role EMPTY_FILE = new Role("emptyFile");

    public static final Role EMPTY_RECORD = new Role("emptyRecord");

    public static final Role FIELD_DECLARATION = new Role("fieldDeclaration");

    public static final Role FIELD_USAGE = new Role("fieldUsage");
    public static final Role FIELD_REFERENCE = new Role("fieldReference");

    public static final Role FIELD_VALUE = new Role("fieldValue");
    public static final Role FIELD_TYPE = new Role("fieldType");

    public static final Role FIRST_CONSTANT_USAGE = new Role("firstConstantUsage");

    public static final Role FOR_STATEMENT = new Role("forStatement");

    public static final Role GETTER_METHOD_REFERENCE = new Role("getMethodReference");

    public static final Role IF_STATEMENT = new Role("ifStatement");

    public static final Role INITIALIZATION_STATEMENT = new Role("initializationStatement");

    public static final Role INNER_CONDITION = new Role("innerCondition");

    public static final Role ITERATION_STATEMENT = new Role("iterationStatement");

    public static final Role MEMBER_CALL = new Role("memberCall");

    public static final Role METHOD_BLOCK = new Role("methodBlock");
    public static final Role METHOD_BODY = new Role("methodBody");

    public static final Role METHOD_DECLARATION = new Role("methodDeclaration");

    public static final Role METHOD_TYPE = new Role("methodType");

    public static final Role OPTIONAL_CLASS = new Role("optionalClass");

    public static final Role OPTIONAL_OBJECT = new Role("optionalObject");
    public static final Role PARAMETER_DECLARATION = new Role("parameterDeclaration");
    public static final Role PARAMETER_REFERENCE = new Role("parameterReference");

    public static final Role PROJECT = new Role("project");

    public static final Role RECORD_DECLARATION = new Role("recordDeclaration");

    public static final Role RETURN_STATEMENT = new Role("returnStatement");

    public static final Role RETURN_TYPE = new Role("returnType");

    public static final Role RETURN_VALUE = new Role("returnValue");

    public static final Role SCOPE_BLOCK = new Role("scopeBlock");

    public static final Role SURROUNDING_BLOCK = new Role("surroundingBlock");

    public static final Role THEN_STATEMENT = new Role("thenStatement");

    public static final Role THROW_EXCEPTION = new Role("throwException");

    public static final Role USING_RECORD = new Role("usingRecord");

    public static final Role VARIABLE_DECLARATION = new Role("variableDeclaration");

    public static final Role VARIABLE_VALUE = new Role("variableValue");
    public static final Role VOID_TYPE = new Role("voidType");

    public static final Role WHILE_STATEMENT = new Role("whileStatement");

    public static final Role WHILE_STATEMENT_BODY = new Role("whileStatementBody");

    public static final Role VARIABLE_USAGE = new Role("variableUsage");

    public static final Role WHILE_BLOCK = new Role("whileBlock");
    public static final Role WRAPPING_BLOCK = new Role("wrappingBlock");
}
