package de.jplag.emf.parser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenAttribute;

/**
 * Tokenizer for EMF modeling elements. Maps any {@link EObject} to a {@link TokenAttribute}.
 */
public interface ModelingElementTokenizer {

    /**
     * Returns the corresponding token type for a model element.
     * @param modelElement is the model element.
     * @return the token type or null if no token is extracted for that element.
     */
    TokenAttribute element2Token(EObject modelElement);

    /**
     * Returns the corresponding the token types for a list of model elements. See
     * {@link ModelingElementTokenizer#element2Token(EObject)}.
     * @param modelElements contains the model elements.
     * @return the list of corresponding token types, might contain less entries than elements.
     */
    default List<TokenAttribute> elements2Tokens(List<EObject> modelElements) {
        return modelElements.stream().map(this::element2Token).filter(Objects::nonNull).toList();
    }

    /**
     * Returns the corresponding token type for a model element. See
     * {@link ModelingElementTokenizer#element2Token(EObject)}.
     * @param modelElement is the model element.
     * @return the optional token type.
     */
    default Optional<TokenAttribute> element2OptionalToken(EObject modelElement) {
        return Optional.ofNullable(element2Token(modelElement));
    }

    /**
     * @return the set of all known token types.
     */
    Set<TokenAttribute> allTokenTypes();
}
