package de.jplag.emf;

import java.io.File;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.jplag.LanguageLoader;
import de.jplag.Token;
import de.jplag.TokenAttribute;
import de.jplag.TokenTrace;

/**
 * EMF metamodel token.
 * @author Timur Saglam
 */
public class MetamodelToken extends Token {

    private final Optional<EObject> eObject;

    /**
     * Creates an Ecore metamodel token that corresponds to an EObject.
     * @param type is the type of the token.
     * @param file is the source model file.
     * @param eObject is the corresponding eObject in the model from which this token was extracted.
     */
    public MetamodelToken(TokenAttribute type, File file, EObject eObject) {
        this(type, file, new TokenTrace(), Optional.of(eObject));
    }

    /**
     * Creates an Ecore metamodel token.
     * @param type is the type of the token.
     * @param file is the source model file.
     */
    public MetamodelToken(TokenAttribute type, File file) {
        this(type, file, new TokenTrace(), Optional.empty());
    }

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the source code file.
     * @param trace is the tracing information of the token, meaning line, column, and length.
     * @param eObject is the corresponding eObject in the model from which this token was extracted
     */
    public MetamodelToken(TokenAttribute type, File file, TokenTrace trace, Optional<EObject> eObject) {
        super(type, file, trace, LanguageLoader.getLanguage(EmfLanguage.class).get());
        this.eObject = eObject;
    }

    /**
     * @return the optional corresponding EObject of the token.
     */
    public Optional<EObject> getEObject() {
        return eObject;
    }
}
