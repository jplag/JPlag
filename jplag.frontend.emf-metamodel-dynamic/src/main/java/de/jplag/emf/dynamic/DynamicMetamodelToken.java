package de.jplag.emf.dynamic;

import static de.jplag.Token.NO_VALUE;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;
import de.jplag.emf.MetamodelToken;

/**
 * EMF metamodel token which uses dynamically created tokens.
 * @author Timur Saglam
 */
public class DynamicMetamodelToken extends MetamodelToken {

    public DynamicMetamodelToken(TokenType type, String file, EObject eObject) {
        super(type, file, NO_VALUE, NO_VALUE, NO_VALUE, Optional.of(eObject));
    }

    public DynamicMetamodelToken(TokenType type, String file) {
        super(type, file);
    }
}
