package de.jplag.emf.dynamic;

import java.io.File;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;
import de.jplag.emf.MetamodelToken;

/**
 * EMF metamodel token which uses dynamically created tokens.
 * @author Timur Saglam
 */
public class DynamicMetamodelToken extends MetamodelToken {

    public DynamicMetamodelToken(TokenType type, File file, EObject eObject) {
        super(type, file, NO_VALUE, NO_VALUE, NO_VALUE, Optional.of(eObject));
    }

    public DynamicMetamodelToken(TokenType type, File file) {
        super(type, file);
    }
}
