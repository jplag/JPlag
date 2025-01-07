package de.jplag.emf.dynamic;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenAttribute;

public record DynamicMetamodelTokenAttribute(EClass eClass) implements TokenAttribute {
    public DynamicMetamodelTokenAttribute(EObject eObject) {
        this(eObject.eClass());
    }

    @Override
    public String getDescription() {
        return eClass.getName();
    }
}
