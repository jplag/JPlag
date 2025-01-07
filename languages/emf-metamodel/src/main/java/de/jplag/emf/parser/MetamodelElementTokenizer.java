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

import de.jplag.TokenAttribute;
import de.jplag.emf.MetamodelTokenAttribute;

/**
 * Tokenizer for metamodel elements. Maps any {@link EObject} to a {@link MetamodelTokenAttribute}.
 */
public class MetamodelElementTokenizer extends EcoreSwitch<MetamodelTokenAttribute> implements ModelingElementTokenizer {

    @Override
    public TokenAttribute element2Token(EObject modelElement) {
        return doSwitch(modelElement);
    }

    @Override
    public MetamodelTokenAttribute caseEAnnotation(EAnnotation eAnnotation) {
        return MetamodelTokenAttribute.ANNOTATION;
    }

    @Override
    public MetamodelTokenAttribute caseEAttribute(EAttribute eAttribute) {
        if (eAttribute.isID()) {
            return MetamodelTokenAttribute.ID_ATTRIBUTE;
        }
        return MetamodelTokenAttribute.ATTRIBUTE;
    }

    @Override
    public MetamodelTokenAttribute caseEClass(EClass eClass) {
        if (eClass.isInterface()) {
            return MetamodelTokenAttribute.INTERFACE;
        }
        if (eClass.isAbstract()) {
            return MetamodelTokenAttribute.ABSTRACT_CLASS;
        }
        return MetamodelTokenAttribute.CLASS;
    }

    @Override
    public MetamodelTokenAttribute caseEDataType(EDataType eDataType) {
        return MetamodelTokenAttribute.DATATYPE;
    }

    @Override
    public MetamodelTokenAttribute caseETypeParameter(ETypeParameter eTypeParameter) {
        return MetamodelTokenAttribute.TYPE_PARAMETER;
    }

    @Override
    public MetamodelTokenAttribute caseEParameter(EParameter eParameter) {
        return MetamodelTokenAttribute.PARAMETER;
    }

    @Override
    public MetamodelTokenAttribute caseEOperation(EOperation eOperation) {
        return MetamodelTokenAttribute.OPERATION;
    }

    @Override
    public MetamodelTokenAttribute caseEPackage(EPackage ePackage) {
        return MetamodelTokenAttribute.PACKAGE;
    }

    @Override
    public MetamodelTokenAttribute caseEEnumLiteral(EEnumLiteral eEnumLiteral) {
        return MetamodelTokenAttribute.ENUM_LITERAL;
    }

    @Override
    public MetamodelTokenAttribute caseEEnum(EEnum eEnum) {
        return MetamodelTokenAttribute.ENUM;
    }

    @Override
    public MetamodelTokenAttribute caseEReference(EReference eReference) {
        if (eReference.isContainment()) {
            if (eReference.getUpperBound() == 1) {
                return MetamodelTokenAttribute.CONTAINMENT;
            }
            return MetamodelTokenAttribute.CONTAINMENT_MULT;
        }
        if (eReference.getUpperBound() == 1) {
            return MetamodelTokenAttribute.REFERENCE;
        }
        return MetamodelTokenAttribute.REFERENCE_MULT;
    }

    @Override
    public Set<TokenAttribute> allTokenTypes() {
        return Set.of(MetamodelTokenAttribute.values());
    }

}
