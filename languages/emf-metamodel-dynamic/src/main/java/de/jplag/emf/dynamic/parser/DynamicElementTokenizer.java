package de.jplag.emf.dynamic.parser;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;
import de.jplag.emf.dynamic.DynamicMetamodelTokenType;
import de.jplag.emf.parser.ModelingElementTokenizer;

/**
 * Tokenizes any {@link EObject} via its {@link EClass}. Tracks all known tokens.
 */
public class DynamicElementTokenizer implements ModelingElementTokenizer {

    private final Set<TokenType> knownTokenTypes;

    /**
     * Creates the tokenizer, initially with an empty token set.
     */
    public DynamicElementTokenizer() {
        knownTokenTypes = new HashSet<>();
    }

    @Override
    public TokenType element2Token(EObject modelElement) {
        DynamicMetamodelTokenType token = new DynamicMetamodelTokenType(modelElement);
        knownTokenTypes.add(token);
        return token;
    }

    @Override
    public Set<TokenType> allTokenTypes() {
        return Set.copyOf(knownTokenTypes);
    }
}
