package de.jplag.emf;

import java.util.List;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public abstract class AbstractMetamodelVisitor {

    private int currentTreeDepth;

    public final void visit(EObject eObject) {
        String name = "";
        if (eObject instanceof ENamedElement el)
            name += " " + el.getName();
        System.out.println(eObject.eClass().getName() + name + " -> " + EcoreUtil.getURI(eObject));
        if (eObject instanceof EPackage ePackage) {
            visitEPackage(ePackage);
        }
        if (eObject instanceof EAnnotation eAnnotation) {
            visitEAnnotation(eAnnotation);
        }
        if (eObject instanceof EClassifier eClassifier) {
            visitEClassifier(eClassifier);
        }
        if (eObject instanceof EClass eClass) {
            visitEClass(eClass);
        }
        if (eObject instanceof EDataType eDataType) {
            visitEDataType(eDataType);
        }
        if (eObject instanceof EStructuralFeature eStructuralFeature) {
            visitEStructuralFeature(eStructuralFeature);
        }
        if (eObject instanceof EAttribute eAttribute) {
            visitEAttribute(eAttribute);
        }
        if (eObject instanceof EReference eReference) {
            visitEReference(eReference);
        }
        if (eObject instanceof EOperation eOperation) {
            visitEOperation(eOperation);
        }
        if (eObject instanceof EEnum eEnum) {
            visitEEnum(eEnum);
        }
        if (eObject instanceof EEnumLiteral eEnumLiteral) {
            visitEEnumLiteral(eEnumLiteral);
        }
        if (eObject instanceof EFactory eFactory) {
            visitEFactory(eFactory);
        }
        if (eObject instanceof EParameter eParameter) {
            visitEParameter(eParameter);
        }

        currentTreeDepth++;
        for (EObject child : eObject.eContents()) {

            visit(child);
        }
        currentTreeDepth--;
    }

    public int getCurrentTreeDepth() {
        return currentTreeDepth;
    }

    protected void visitEAnnotation(EAnnotation eAnnotation) {
    }

    protected void visitEAttribute(EAttribute eAttribute) {
    }

    protected void visitEClass(EClass eClass) {
    }

    protected void visitEClassifier(EClassifier eClassifier) {
    }

    protected void visitEDataType(EDataType eDataType) {
    }

    protected void visitEEnum(EEnum eEnum) {
    }

    protected void visitEEnumLiteral(EEnumLiteral eEnumLiteral) {
    }

    protected void visitEFactory(EFactory eFactory) {
    }

    protected void visitEOperation(EOperation eOperation) {
    }

    protected void visitEPackage(EPackage ePackage) {
    }

    protected void visitEParameter(EParameter eParameter) {
    }

    protected void visitEReference(EReference eReference) {
    }

    protected void visitEStructuralFeature(EStructuralFeature eStructuralFeature) {
    }

    public static void main(String[] args) {
        List<String> types = List.of("EPackage", "EAnnotation", "EClassifier", "EClass", "EDataType", "EStructuralFeature", "EAttribute",
                "EReference", "EOperation", "EEnum", "EEnumLiteral", "EParameter");

        for (String type : types) {
            System.out.println("if (eObject instanceof " + type + " e" + type.substring(1) + ") {");
            System.out.println("    visit" + type + "(e" + type.substring(1) + ");");
            System.out.println("}");
        }

        for (String type : types) {
            System.out.println("protected void visit" + type + "(" + type + " e" + type.substring(1) + ") {}");
        }
    }

}
