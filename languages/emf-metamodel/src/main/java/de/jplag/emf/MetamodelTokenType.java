package de.jplag.emf;

import de.jplag.TokenType;

/**
 * Ecore meta-metamodel token type. Defines which tokens can be extracted from a metamodel.
 */
public enum MetamodelTokenType implements TokenType {
    PACKAGE("EPackage"),
    PACKAGE_END("EPackage", true),
    ANNOTATION("EAnnotation"),
    CLASS("EClass"),
    CLASS_END("EClass", true),
    DATATYPE("EDatatype"),
    ENUM("EEnum"),
    ENUM_END("EEnum", true),
    ENUM_LITERAL("EEnumLiteral"),
    OPERATION("EOperation"),
    OPERATION_END("EOperation", true),
    REFERENCE("EReference"),
    ATTRIBUTE("EAttribute"),
    PARAMETER("EParameter"),
    INTERFACE("EInterface"),
    INTERFACE_END("EInterface", true),
    SUPER_TYPE("ESuperType"),
    ID_ATTRIBUTE("EAttribute (ID)"),
    CONTAINMENT("EReference (Containment)"),
    ABSTRACT_CLASS("EAbstractClass"),
    ABSTRACT_CLASS_END("EAbstractClass", true),
    RETURN_TYPE("EClassifier (Return Type"),
    THROWS_DECLARATION("EException"),
    TYPE_PARAMETER("Type Parameter"),
    BOUND("Bound");

    private static final String END_TOKEN_SUFFIX = " (End)";

    private final String description;
    private final boolean endToken;

    public String getDescription() {
        return description;
    }

    MetamodelTokenType(String description) {
        this.description = description;
        endToken = false;
    }

    MetamodelTokenType(String description, boolean endToken) {
        this.endToken = endToken;
        this.description = endToken ? END_TOKEN_SUFFIX + description : description;

    }

    public boolean isEndToken() {
        return endToken;
    }
}
