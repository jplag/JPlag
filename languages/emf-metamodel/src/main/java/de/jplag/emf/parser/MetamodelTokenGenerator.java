package de.jplag.emf.parser;

import static de.jplag.emf.MetamodelTokenType.ABSTRACT_CLASS;
import static de.jplag.emf.MetamodelTokenType.ABSTRACT_CLASS_END;
import static de.jplag.emf.MetamodelTokenType.ANNOTATION;
import static de.jplag.emf.MetamodelTokenType.ATTRIBUTE;
import static de.jplag.emf.MetamodelTokenType.BOUND;
import static de.jplag.emf.MetamodelTokenType.CLASS;
import static de.jplag.emf.MetamodelTokenType.CLASS_END;
import static de.jplag.emf.MetamodelTokenType.CONTAINMENT;
import static de.jplag.emf.MetamodelTokenType.DATATYPE;
import static de.jplag.emf.MetamodelTokenType.ENUM;
import static de.jplag.emf.MetamodelTokenType.ENUM_END;
import static de.jplag.emf.MetamodelTokenType.ENUM_LITERAL;
import static de.jplag.emf.MetamodelTokenType.ID_ATTRIBUTE;
import static de.jplag.emf.MetamodelTokenType.INTERFACE;
import static de.jplag.emf.MetamodelTokenType.INTERFACE_END;
import static de.jplag.emf.MetamodelTokenType.OPERATION;
import static de.jplag.emf.MetamodelTokenType.OPERATION_END;
import static de.jplag.emf.MetamodelTokenType.PACKAGE;
import static de.jplag.emf.MetamodelTokenType.PACKAGE_END;
import static de.jplag.emf.MetamodelTokenType.PARAMETER;
import static de.jplag.emf.MetamodelTokenType.REFERENCE;
import static de.jplag.emf.MetamodelTokenType.RETURN_TYPE;
import static de.jplag.emf.MetamodelTokenType.SUPER_TYPE;
import static de.jplag.emf.MetamodelTokenType.THROWS_DECLARATION;
import static de.jplag.emf.MetamodelTokenType.TYPE_PARAMETER;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;

import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Visits a metamodel containment tree and extracts the relevant token.
 * @author Timur Saglam
 */
public class MetamodelTokenGenerator extends AbstractMetamodelVisitor {
    private EcoreParser parser;

    /**
     * Creates the visitor.
     * @param parser is the parser which receives the generated tokens.
     */
    public MetamodelTokenGenerator(EcoreParser parser) {
        super(true);
        this.parser = parser;
    }

    @Override
    protected void visitEAnnotation(EAnnotation eAnnotation) {
        parser.addToken(ANNOTATION, eAnnotation);
    }

    @Override
    protected void visitEAttribute(EAttribute eAttribute) {
        if (eAttribute.isID()) {
            parser.addToken(ID_ATTRIBUTE, eAttribute);
        } else {
            parser.addToken(ATTRIBUTE, eAttribute);
        }
    }

    @Override
    protected void visitEClass(EClass eClass) {
        if (eClass.isInterface()) {
            parser.addToken(INTERFACE, eClass);
        } else if (eClass.isAbstract()) {
            parser.addToken(ABSTRACT_CLASS, eClass);
        } else {
            parser.addToken(CLASS, eClass);
        }
        eClass.getESuperTypes().forEach(it -> parser.addToken(SUPER_TYPE, eClass, " extends "));
    }

    @Override
    protected void visitEDataType(EDataType eDataType) {
        if (!(eDataType instanceof EEnum)) {
            parser.addToken(DATATYPE, eDataType);
        }
    }

    @Override
    protected void visitEEnum(EEnum eEnum) {
        parser.addToken(ENUM, eEnum);
    }

    @Override
    protected void visitEEnumLiteral(EEnumLiteral eEnumLiteral) {
        parser.addToken(ENUM_LITERAL, eEnumLiteral);
    }

    @Override
    protected void visitEOperation(EOperation eOperation) {
        parser.addToken(OPERATION, eOperation);
        if (eOperation.getEType() != null) {
            parser.addToken(RETURN_TYPE, eOperation);
        }
        eOperation.getEExceptions().forEach(it -> parser.addToken(THROWS_DECLARATION, it));
    }

    @Override
    protected void visitEPackage(EPackage ePackage) {
        parser.addToken(PACKAGE, ePackage);
    }

    @Override
    protected void visitEParameter(EParameter eParameter) {
        parser.addToken(PARAMETER, eParameter);
    }

    @Override
    protected void visitEReference(EReference eReference) {
        if (eReference.isContainment()) {
            parser.addToken(CONTAINMENT, eReference);
        } else {
            parser.addToken(REFERENCE, eReference);
        }
    }

    @Override
    protected void visitETypeParameter(ETypeParameter eTypeParameter) {
        parser.addToken(TYPE_PARAMETER, eTypeParameter);
        eTypeParameter.getEBounds().forEach(it -> parser.addToken(BOUND, it));
    }

    @Override
    protected void leaveEClass(EClass eClass) {
        if (eClass.isInterface()) {
            parser.addToken(INTERFACE_END, eClass);
        } else if (eClass.isAbstract()) {
            parser.addToken(ABSTRACT_CLASS_END, eClass);
        } else {
            parser.addToken(CLASS_END, eClass);
        }
    }

    @Override
    protected void leaveEPackage(EPackage ePackage) {
        parser.addToken(PACKAGE_END, ePackage);
    }

    @Override
    protected void leaveEEnum(EEnum eEnum) {
        parser.addToken(ENUM_END, eEnum);
    }

    @Override
    protected void leaveEOperation(EOperation eOperation) {
        parser.addToken(OPERATION_END, eOperation);
    }

}
