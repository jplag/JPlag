package de.jplag.emf;

import de.jplag.TokenAttribute;

/**
 * Ecore meta-metamodel token type. Defines which tokens can be extracted from a metamodel.
 */
public enum MetamodelTokenAttribute implements TokenAttribute {
    PACKAGE("EPackage"),
    PACKAGE_END(PACKAGE),
    ANNOTATION("EAnnotation"),
    CLASS("EClass"),
    CLASS_END(CLASS),
    DATATYPE("EDatatype"),
    ENUM("EEnum"),
    ENUM_END(ENUM),
    ENUM_LITERAL("EEnumLiteral"),
    OPERATION("EOperation"),
    REFERENCE("EReference"),
    REFERENCE_MULT("EReference (multi-valued)"),
    ATTRIBUTE("EAttribute"),
    PARAMETER("EParameter"),
    INTERFACE("EInterface"),
    INTERFACE_END(INTERFACE),
    ID_ATTRIBUTE("EAttribute (ID)"),
    CONTAINMENT("EReference (Containment)"),
    CONTAINMENT_MULT("EReference (Containment, multi-valued)"),
    ABSTRACT_CLASS("EAbstractClass"),
    ABSTRACT_CLASS_END(ABSTRACT_CLASS),
    RETURN_TYPE("EClassifier (Return Type"),
    THROWS_DECLARATION("EException"),
    TYPE_PARAMETER("Type Parameter"),
    BOUND("Bound");

    private static final String END_TOKEN_SUFFIX = " (End)";

    private final String description;
    private final boolean isEndToken;

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Creates a normal metamodel token type.
     * @param description is the textual description.
     */
    MetamodelTokenAttribute(String description) {
        this.description = description;
        isEndToken = false;
    }

    /**
     * Create a metamodel end token type, meaning a token that indicates the end of a containment.
     * @param beginType is the corresponding begin token.
     */
    MetamodelTokenAttribute(MetamodelTokenAttribute beginType) {
        description = beginType.getDescription() + END_TOKEN_SUFFIX;
        isEndToken = true;
    }

    /**
     * @return true if the token is a end token type, meaning a token that indicates the end of a containment.
     */
    public boolean isEndToken() {
        return isEndToken;
    }
}
