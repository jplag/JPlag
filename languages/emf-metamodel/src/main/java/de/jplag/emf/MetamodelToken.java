package de.jplag.emf;

import java.io.File;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.jplag.Token;
import de.jplag.TokenType;

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
    public MetamodelToken(MetamodelTokenType type, File file, EObject eObject) {
        this(type, file, NO_VALUE, NO_VALUE, NO_VALUE, Optional.of(eObject));
    }

    /**
     * Creates an Ecore metamodel token.
     * @param type is the type of the token.
     * @param file is the source model file.
     */
    public MetamodelToken(TokenType type, File file) {
        this(type, file, NO_VALUE, NO_VALUE, NO_VALUE, Optional.empty());
    }

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the source code file.
     * @param line is the line index in the source code where the token resides. Cannot be smaller than 1.
     * @param column is the column index, meaning where the token starts in the line.
     * @param length is the length of the token in the source code.
     * @param eObject is the corresponding eObject in the model from which this token was extracted
     */
    public MetamodelToken(TokenType type, File file, int line, int column, int length, Optional<EObject> eObject) {
        super(type, file, line, column, length);
        this.eObject = eObject;
    }

    /**
     * @return the optional corresponding EObject of the token.
     */
    public Optional<EObject> getEObject() {
        return eObject;
    }
}
