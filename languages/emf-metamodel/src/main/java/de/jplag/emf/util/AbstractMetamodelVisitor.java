package de.jplag.emf.util;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;

/**
 * Visitor for the containment tree of an EMF Metamodel.
 * @author Timur Saglam
 */
public abstract class AbstractMetamodelVisitor {

    private final boolean sortContainmentsByType;

    protected AbstractMetamodelVisitor(boolean sortContainmentsByType) {
        this.sortContainmentsByType = sortContainmentsByType;
    }

    private int currentTreeDepth;

    /**
     * Returns the current depth in the containment tree from the starting point.
     * @return the depth in tree node levels.
     */
    public int getCurrentTreeDepth() {
        return currentTreeDepth;
    }

    /**
     * Visits an EObject and all nodes in the containment tree below. Note that multiple visitor method may be called for a
     * single element. For example <code>visitEClass()</code> and <code>visitEObject()</code>.
     * @param eObject is the EObject to visit.
     */
    public final void visit(EObject eObject) {
        visitEObject(eObject);
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
        if (eObject instanceof EGenericType eGenericType) {
            visitEGenericType(eGenericType);
        }
        if (eObject instanceof ETypeParameter eTypeParameter) {
            visitETypeParameter(eTypeParameter);
        }
        if (eObject instanceof ENamedElement eNamedElement) {
            visitENamedElement(eNamedElement);
        }

        var children = new ArrayList<>(eObject.eContents());
        if (sortContainmentsByType) {
            children.sort((first, second) -> first.eClass().getName().compareTo(second.eClass().getName()));
        }

        currentTreeDepth++;
        for (EObject child : children) {
            visit(child);
        }
        currentTreeDepth--;

        if (eObject instanceof EPackage ePackage) {
            leaveEPackage(ePackage);
        }
        if (eObject instanceof EClass eClass) {
            leaveEClass(eClass);
        }
        if (eObject instanceof EOperation eOperation) {
            leaveEOperation(eOperation);
        }
        if (eObject instanceof EEnum eEnum) {
            leaveEEnum(eEnum);
        }
    }

    protected void visitENamedElement(ENamedElement eNamedElement) {
    }

    /**
     * Visit method that gets called for all annotation nodes.
     * @param eAnnotation is the node that is visited.
     */
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

    protected void visitEGenericType(EGenericType eGenericType) {
    }

    protected void visitEObject(EObject eObject) {

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

    protected void visitETypeParameter(ETypeParameter eTypeParameter) {
    }

    protected void leaveEEnum(EEnum eEnum) {
    }

    protected void leaveEOperation(EOperation eOperation) {
    }

    protected void leaveEClass(EClass eClass) {
    }

    protected void leaveEPackage(EPackage ePackage) {
    }
}
