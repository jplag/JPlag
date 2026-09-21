package de.jplag.emf.model;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;

/**
 * Dynamic model token which can be created on-the-fly. This means every metaclass corresponds to one unique token.
 * @param eClass is the metaclass that determines the token type.
 */
public record EmfModelTokenType(EClass eClass) implements TokenType {

    /**
     * Creates a token type for any given model element.
     * @param eObject is the model element, whose metaclass is used for the token type.
     */
    public EmfModelTokenType(EObject eObject) {
        this(eObject.eClass());
    }

    @Override
    public String getDescription() {
        return eClass.getName();
    }
}
