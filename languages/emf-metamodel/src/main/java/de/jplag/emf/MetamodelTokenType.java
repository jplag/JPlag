package de.jplag.emf;

import de.jplag.TokenType;

/**
 * Ecore meta-metamodel token type. Defines which tokens can be extracted from a metamodel.
 */
public enum MetamodelTokenType implements TokenType {
    PACKAGE("EPackage"),
    PACKAGE_END("EPackage (End)"),
    ANNOTATION("EAnnotation"),
    CLASS("EClass"),
    CLASS_END("EClass (End)"),
    DATATYPE("EDatatype"),
    ENUM("EEnum"),
    ENUM_END("EEnum (End)"),
    ENUM_LITERAL("EEnumLiteral"),
    OPERATION("EOperation"),
    OPERATION_END("EOperation (End)"),
    REFERENCE("EReference"),
    ATTRIBUTE("EAttribute"),
    PARAMETER("EParameter"),
    INTERFACE("EClass (Interface)"),
    INTERFACE_END("EClass (Interface, End)"),
    SUPER_TYPE("ESuperType"),
    ID_ATTRIBUTE("EAttribute (ID)"),
    CONTAINMENT("EReference (Containment)"),
    ABSTRACT_CLASS("EClass (Abstract)"),
    ABSTRACT_CLASS_END("EClass (Abstract, End)"),
    RETURN_TYPE("EClassifier (Return Type"),
    THROWS_DECLARATION("EClassifier (Exception"),
    TYPE_PARAMETER("Type Parameter"),
    BOUND("Bound");

    private final String description;

    public String getDescription() {
        return description;
    }

    MetamodelTokenType(String description) {
        this.description = description;
    }
}
