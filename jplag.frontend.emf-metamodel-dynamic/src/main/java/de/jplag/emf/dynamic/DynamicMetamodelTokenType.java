package de.jplag.emf.dynamic;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;

public record DynamicMetamodelTokenType(EClass eClass) implements TokenType {
    public DynamicMetamodelTokenType(EObject eObject) {
        this(eObject.eClass());
    }

    public String getDescription() {
        return eClass.getName();
    }
}
