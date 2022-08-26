package de.jplag.emf;

import de.jplag.TokenType;

public enum MetamodelTokenType implements TokenType {
    PACKAGE("EPackage"),
    ANNOTATION("EAnnotation"),
    CLASS("EClass"),
    DATATYPE("EDatatype"),
    ENUM("EEnum"),
    ENUM_LITERAL("EEnumLiteral"),
    OPERATION("EOperation"),
    REFERENCE("EReference"),
    ATTRIBUTE("EAttribute"),
    PARAMETER("EParameter"),
    INTERFACE("EClass (Interface)"),
    SUPER_TYPE("ESuperType"),
    ID_ATTRIBUTE("EAttribute (ID)"),
    CONTAINMENT("EReference (Containment)"),
    ABSTRACT_CLASS("EClass (Abstract)"),
    RETURN_TYPE("EClassifier (Return Type"),
    THROWS_DECLARATION("EClassifier (Exception"),
    TYPE_PARAMETER("Type Parameter"),
    BOUND("Bound");

    private final String description;

    public String getDescription() {
        return description;
    }

    private MetamodelTokenType(String description) {
        this.description = description;
    }
}
