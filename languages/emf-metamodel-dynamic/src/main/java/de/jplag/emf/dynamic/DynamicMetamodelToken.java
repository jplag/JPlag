package de.jplag.emf.dynamic;

import java.io.File;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenTrace;
import de.jplag.TokenType;
import de.jplag.emf.MetamodelToken;

/**
 * EMF metamodel token which uses dynamically created tokens. This means every metaclass corresponds to one unique token
 * type.
 * @author Timur Saglam
 */
public class DynamicMetamodelToken extends MetamodelToken {

    /**
     * Creates a metamodel token for a model element of a given metamodel.
     * @param type is the token type.
     * @param file is metamodel file.
     * @param eObject is the metamodel element.
     */
    public DynamicMetamodelToken(TokenType type, File file, EObject eObject) {
        super(type, file, new TokenTrace(), Optional.of(eObject));
    }

    /**
     * Creates a metamodel token for a given metamodel.
     * @param type is the token type.
     * @param file is metamodel file.
     */
    public DynamicMetamodelToken(TokenType type, File file) {
        super(type, file);
    }
}
