package de.jplag.emf;

import java.io.File;

import org.eclipse.emf.ecore.EObject;

import de.jplag.Token;
import de.jplag.TokenTrace;
import de.jplag.TokenType;

/**
 * EMF metamodel token.
 * @author Timur Saglam
 */
public class MetamodelToken extends Token {

    private final EObject eObject;

    /**
     * Creates an Ecore metamodel token that corresponds to an EObject.
     * @param type is the type of the token.
     * @param file is the source model file.
     * @param eObject is the corresponding eObject in the model from which this token was extracted.
     */
    public MetamodelToken(TokenType type, File file, EObject eObject) {
        this(type, file, new TokenTrace(), eObject);
    }

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the source code file.
     * @param trace is the tracing information of the token, meaning line, column, and length.
     * @param eObject is the corresponding eObject in the model from which this token was extracted
     */
    public MetamodelToken(TokenType type, File file, TokenTrace trace, EObject eObject) {
        super(type, file, trace);
        this.eObject = eObject;
    }

    /**
     * @return the corresponding EObject of the token.
     */
    public EObject getEObject() {
        return eObject;
    }
}
