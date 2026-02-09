package de.jplag.java_cpg.transformation;

/**
 * This record represents a role of a node in the graph. It is used to identify the role of a node in the graph and to
 * distinguish between different nodes with the same type.
 * @param name the name of the role
 */
public record Role(String name) {
    /**
     * This role represents an argument of a method call or a constructor call.
     */
    public static final Role ARGUMENT = new Role("argument");
    /**
     * This role represents an assignment expression.
     */
    public static final Role ASSIGN_EXPRESSION = new Role("assignExpression");
    /**
     * This role represents the body of a method, constructor, or lambda expression.
     */
    public static final Role BODY = new Role("body");
    /**
     * This role represents the declaration of a class.
     */
    public static final Role CLASS_DECLARATION = new Role("classDeclaration");
    /**
     * This role represents a condition of an if statement, a while statement, a do-while statement, or a for statement.
     */
    public static final Role CONDITION = new Role("condition");

    /**
     * This role represents a constructor declaration.
     */
    public static final Role CONSTRUCTOR_DECLARATION = new Role("constructorDeclaration");
    /**
     * This role represents a containing file.
     */
    public static final Role CONTAINING_FILE = new Role("containingFile");
    /**
     * This role represents a containing statement.
     */
    public static final Role CONTAINING_STATEMENT = new Role("containingStatement");
    /**
     * This role represents a containing record.
     */
    public static final Role CONTAINING_RECORD = new Role("containingRecord");
    /**
     * This role represents a declaration.
     */
    public static final Role DECLARATION = new Role("declaration");
    /**
     * This role represents a declaration container.
     */
    public static final Role DECLARATION_CONTAINER = new Role("declarationContainer");
    /**
     * This role represents a declaration statement.
     */
    public static final Role DECLARATION_STATEMENT = new Role("declarationStatement");
    /**
     * This role represents a defining record.
     */
    public static final Role DEFINING_RECORD = new Role("definingRecord");
    /**
     * This role represents a defining record reference.
     */
    public static final Role DEFINING_RECORD_REFERENCE = new Role("definingRecordReference");
    /**
     * This role represents a do statement.
     */
    public static final Role DO_STATEMENT = new Role("doStatement");
    /**
     * This role represents an else statement.
     */
    public static final Role ELSE_STATEMENT = new Role("elseStatement");
    /**
     * This role represents an empty file.
     */
    public static final Role EMPTY_FILE = new Role("emptyFile");
    /**
     * This role represents an empty record.
     */
    public static final Role EMPTY_RECORD = new Role("emptyRecord");
    /**
     * This role represents a field declaration.
     */
    public static final Role FIELD_DECLARATION = new Role("fieldDeclaration");
    /**
     * This role represents a field usage or a field reference.
     */
    public static final Role FIELD_USAGE = new Role("fieldUsage");
    /**
     * This role represents a field reference.
     */
    public static final Role FIELD_REFERENCE = new Role("fieldReference");
    /**
     * This role represents a field type.
     */
    public static final Role FIELD_VALUE = new Role("fieldValue");
    /**
     * This role represents a field type.
     */
    public static final Role FIELD_TYPE = new Role("fieldType");
    /**
     * This role represents a for-each statement.
     */
    public static final Role FIRST_CONSTANT_USAGE = new Role("firstConstantUsage");
    /**
     * This role represents a for statement.
     */
    public static final Role FOR_STATEMENT = new Role("forStatement");
    /**
     * This role represents a reference to a getter or setter method.
     */
    public static final Role GETTER_METHOD_REFERENCE = new Role("getMethodReference");
    /**
     * This role represents a if statement.
     */
    public static final Role IF_STATEMENT = new Role("ifStatement");
    /**
     * This role represents an initialization statement of a for statement.
     */
    public static final Role INITIALIZATION_STATEMENT = new Role("initializationStatement");
    /**
     * This role represents an inner condition of a for statement.
     */
    public static final Role INNER_CONDITION = new Role("innerCondition");
    /**
     * This role represents an inner update of a for statement.
     */
    public static final Role ITERATION_STATEMENT = new Role("iterationStatement");
    /**
     * This role represents a call to a member.
     */
    public static final Role MEMBER_CALL = new Role("memberCall");
    /**
     * This role represents a method block.
     */
    public static final Role METHOD_BLOCK = new Role("methodBlock");
    /**
     * This role represents a method body.
     */
    public static final Role METHOD_BODY = new Role("methodBody");
    /**
     * This role represents a method declaration.
     */
    public static final Role METHOD_DECLARATION = new Role("methodDeclaration");
    /**
     * This role represents a method type.
     */
    public static final Role METHOD_TYPE = new Role("methodType");
    /**
     * This role represents a optional class.
     */
    public static final Role OPTIONAL_CLASS = new Role("optionalClass");
    /**
     * This role represents an optional object.
     */
    public static final Role OPTIONAL_OBJECT = new Role("optionalObject");
    /**
     * This role represents a parameter declaration.
     */
    public static final Role PARAMETER_DECLARATION = new Role("parameterDeclaration");
    /**
     * This role represents a parameter reference.
     */
    public static final Role PARAMETER_REFERENCE = new Role("parameterReference");

    /**
     * This role represents a project.
     */
    public static final Role PROJECT = new Role("project");
    /**
     * This role represents a record declaration.
     */
    public static final Role RECORD_DECLARATION = new Role("recordDeclaration");
    /**
     * This role represents a return statement.
     */
    public static final Role RETURN_STATEMENT = new Role("returnStatement");
    /**
     * This role represents a return type.
     */
    public static final Role RETURN_TYPE = new Role("returnType");
    /**
     * This role represents a return value.
     */
    public static final Role RETURN_VALUE = new Role("returnValue");
    /**
     * This role represents a scope block (e.g. a block of an if statement, a while statement, a for statement, a do-while
     * statement, a method body, or a constructor body).
     */
    public static final Role SCOPE_BLOCK = new Role("scopeBlock");
    /**
     * This role represents a surrounding block (e.g. a block of an if statement, a while statement, a for statement, a
     * do-while statement, a method body, or a constructor body).
     */
    public static final Role SURROUNDING_BLOCK = new Role("surroundingBlock");

    /**
     * This role represents a then statement of an if statement.
     */
    public static final Role THEN_STATEMENT = new Role("thenStatement");
    /**
     * This role represents a throw statement.
     */
    public static final Role THROW_EXCEPTION = new Role("throwException");
    /**
     * This role represents a using record.
     */
    public static final Role USING_RECORD = new Role("usingRecord");
    /**
     * This role represents a variable declaration.
     */
    public static final Role VARIABLE_DECLARATION = new Role("variableDeclaration");
    /**
     * This role represents a variable value.
     */
    public static final Role VARIABLE_VALUE = new Role("variableValue");
    /**
     * This role represents a void type.
     */
    public static final Role VOID_TYPE = new Role("voidType");
    /**
     * This role represents a while statement.
     */
    public static final Role WHILE_STATEMENT = new Role("whileStatement");
    /**
     * This role represents the body of a while statement.
     */
    public static final Role WHILE_STATEMENT_BODY = new Role("whileStatementBody");
    /**
     * This role represents the usage of a variable.
     */
    public static final Role VARIABLE_USAGE = new Role("variableUsage");
    /**
     * This role represents a while block.
     */
    public static final Role WHILE_BLOCK = new Role("whileBlock");
    /**
     * This role represents a wrapping block (e.g. a block that wraps a lambda expression or an anonymous class).
     */
    public static final Role WRAPPING_BLOCK = new Role("wrappingBlock");
}
