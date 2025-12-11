package de.jplag.emf.dynamic;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;

/**
 * Dynamic metamodel token which can be created on-the-fly. This means every metaclass corresponds to one unique token.
 * @param eClass is the metaclass that determines the token type.
 */
public record DynamicMetamodelTokenType(EClass eClass) implements TokenType {

    /**
     * Creates a token type for any given model element.
     * @param eObject is the metamodel element, whose metaclass is used for the token type.
     */
    public DynamicMetamodelTokenType(EObject eObject) {
        this(eObject.eClass());
    }

    @Override
    public String getDescription() {
        return eClass.getName();
    }
}
