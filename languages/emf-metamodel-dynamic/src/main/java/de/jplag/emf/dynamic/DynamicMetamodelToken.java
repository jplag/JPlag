package de.jplag.emf.dynamic;

import java.io.File;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenAttribute;
import de.jplag.TokenTrace;
import de.jplag.emf.MetamodelToken;

/**
 * EMF metamodel token which uses dynamically created tokens.
 * @author Timur Saglam
 */
public class DynamicMetamodelToken extends MetamodelToken {

    public DynamicMetamodelToken(TokenAttribute type, File file, EObject eObject) {
        super(type, file, new TokenTrace(), Optional.of(eObject));
    }

    public DynamicMetamodelToken(TokenAttribute type, File file) {
        super(type, file);
    }
}
