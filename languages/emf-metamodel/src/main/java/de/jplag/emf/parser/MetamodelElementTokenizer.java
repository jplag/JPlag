package de.jplag.emf.parser;

import java.util.Set;

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
import org.eclipse.emf.ecore.util.EcoreSwitch;

import de.jplag.TokenType;
import de.jplag.emf.MetamodelTokenType;

/**
 * Tokenizer for metamodel elements. Maps any {@link EObject} to a {@link MetamodelTokenType}.
 */
public class MetamodelElementTokenizer extends EcoreSwitch<MetamodelTokenType> implements ModelingElementTokenizer {

    @Override
    public TokenType element2Token(EObject modelElement) {
        return doSwitch(modelElement);
    }

    @Override
    public MetamodelTokenType caseEAnnotation(EAnnotation eAnnotation) {
        return MetamodelTokenType.ANNOTATION;
    }

    @Override
    public MetamodelTokenType caseEAttribute(EAttribute eAttribute) {
        if (eAttribute.isID()) {
            return MetamodelTokenType.ID_ATTRIBUTE;
        } else {
            return MetamodelTokenType.ATTRIBUTE;
        }
    }

    @Override
    public MetamodelTokenType caseEClass(EClass eClass) {
        if (eClass.isInterface()) {
            return MetamodelTokenType.INTERFACE;
        } else if (eClass.isAbstract()) {
            return MetamodelTokenType.ABSTRACT_CLASS;
        } else {
            return MetamodelTokenType.CLASS;
        }
    }

    @Override
    public MetamodelTokenType caseEDataType(EDataType eDataType) {
        return MetamodelTokenType.DATATYPE;
    }

    @Override
    public MetamodelTokenType caseETypeParameter(ETypeParameter eTypeParameter) {
        return MetamodelTokenType.TYPE_PARAMETER;
    }

    @Override
    public MetamodelTokenType caseEParameter(EParameter eParameter) {
        return MetamodelTokenType.PARAMETER;
    }

    @Override
    public MetamodelTokenType caseEOperation(EOperation eOperation) {
        return MetamodelTokenType.OPERATION;
    }

    @Override
    public MetamodelTokenType caseEPackage(EPackage ePackage) {
        return MetamodelTokenType.PACKAGE;
    }

    @Override
    public MetamodelTokenType caseEEnumLiteral(EEnumLiteral eEnumLiteral) {
        return MetamodelTokenType.ENUM_LITERAL;
    }

    @Override
    public MetamodelTokenType caseEEnum(EEnum eEnum) {
        return MetamodelTokenType.ENUM;
    }

    @Override
    public MetamodelTokenType caseEReference(EReference eReference) {
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
    }

    @Override
    public Set<TokenType> allTokenTypes() {
        return Set.of(MetamodelTokenType.values());
    }

}
