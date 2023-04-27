package de.jplag.emf.normalization;

import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;

import de.jplag.emf.MetamodelTokenType;

/**
 * Utility class for the token extraction rules for the normalization.
 */
public final class TokenExtractionRules {

    private TokenExtractionRules() {
        // private constructor for non-instantiability.
    }

    /*
     * TODO TS: These rules are currently duplicated from the token generator, if these rules converge we need to merge
     * them. Furthermore they could also be moved to the token types themselves?
     */
    /**
     * Returns the corresponding the token type for a model element.
     * @param modelElement is the model element.
     * @return the token type or null if no token is extracted for that element.
     */
    public static MetamodelTokenType element2Token(EObject modelElement) {
        if (modelElement instanceof EAnnotation) {
            return MetamodelTokenType.ANNOTATION;
        } else if (modelElement instanceof EAttribute eAttribute) {
            if (eAttribute.isID()) {
                return MetamodelTokenType.ID_ATTRIBUTE;
            } else {
                return MetamodelTokenType.ATTRIBUTE;
            }
            // TODO TS: Decision for EAttribute?
            // return MetamodelTokenType.ATTRIBUTE;
        } else if (modelElement instanceof EClass eClass) {
            if (eClass.isInterface()) {
                return MetamodelTokenType.INTERFACE;
            } else if (eClass.isAbstract()) {
                return MetamodelTokenType.ABSTRACT_CLASS;
            } else {
                return MetamodelTokenType.CLASS;
            }
        } else if (modelElement instanceof EEnum) {
            return MetamodelTokenType.ENUM;
        } else if (modelElement instanceof EDataType) {
            return MetamodelTokenType.DATATYPE;
        } else if (modelElement instanceof EEnumLiteral) {
            return MetamodelTokenType.ENUM_LITERAL;
        } else if (modelElement instanceof EOperation) {
            return MetamodelTokenType.OPERATION;
        } else if (modelElement instanceof EPackage) {
            return MetamodelTokenType.PACKAGE;
        } else if (modelElement instanceof EParameter) {
            return MetamodelTokenType.PARAMETER;
        } else if (modelElement instanceof EReference eReference) {
            if (eReference.isContainment()) {
                if (eReference.getUpperBound() == 1) {
                    return MetamodelTokenType.CONTAINMENT;
                } else {
                    return MetamodelTokenType.CONTAINMENT_MULT;
                }
            } else {
                if (eReference.getUpperBound() == 1) {
                    return MetamodelTokenType.REFERENCE;
                } else {
                    return MetamodelTokenType.REFERENCE_MULT;
                }
            }
            // TODO TS: Decision for EReference?
            // if (eReference.isContainment()) {
            // return MetamodelTokenType.CONTAINMENT;
            // } else {
            // return MetamodelTokenType.REFERENCE;
            // }
        } else if (modelElement instanceof ETypeParameter) {
            return MetamodelTokenType.TYPE_PARAMETER;
        }

        return null;
    }

    /**
     * Returns the corresponding the token types for a list of model elements. See
     * {@link TokenExtractionRules#element2Token(EObject)}.
     * @param modelElements contains the model elements.
     * @return the list of corresponding token types, might contain less entries than elements.
     */
    public static List<MetamodelTokenType> elements2Tokens(List<EObject> modelElements) {
        return modelElements.stream().map(it -> element2Token(it)).filter(it -> it != null).toList();
    }
}
