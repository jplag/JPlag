package de.jplag.emf.dynamic.parser;

import java.util.LinkedHashSet;
import java.util.SequencedSet;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenAttribute;
import de.jplag.emf.dynamic.DynamicMetamodelTokenAttribute;
import de.jplag.emf.parser.ModelingElementTokenizer;

/**
 * Tokenizes any {@link EObject} via its {@link EClass}. Tracks all known tokens.
 */
public class DynamicElementTokenizer implements ModelingElementTokenizer {

    private static final SequencedSet<TokenAttribute> knownTokenTypes = new LinkedHashSet<>();

    @Override
    public TokenAttribute element2Token(EObject modelElement) {
        DynamicMetamodelTokenAttribute token = new DynamicMetamodelTokenAttribute(modelElement);
        knownTokenTypes.add(token);
        return token;
    }

    @Override
    public SequencedSet<TokenAttribute> allTokenTypes() {
        return new LinkedHashSet<>(knownTokenTypes);
    }
}
