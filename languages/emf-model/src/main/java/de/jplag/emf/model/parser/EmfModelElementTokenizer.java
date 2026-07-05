package de.jplag.emf.model.parser;

import java.util.LinkedHashSet;
import java.util.SequencedSet;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;
import de.jplag.emf.model.EmfModelTokenType;
import de.jplag.emf.parser.ModelingElementTokenizer;

/**
 * Tokenizes any {@link EObject} via its {@link EClass}. Tracks all known tokens.
 */
public class EmfModelElementTokenizer implements ModelingElementTokenizer {

    private static final SequencedSet<TokenType> knownTokenTypes = new LinkedHashSet<>();

    @Override
    public TokenType element2Token(EObject modelElement) {
        EmfModelTokenType token = new EmfModelTokenType(modelElement);
        knownTokenTypes.add(token);
        return token;
    }

    @Override
    public SequencedSet<TokenType> allTokenTypes() {
        return new LinkedHashSet<>(knownTokenTypes);
    }
}
