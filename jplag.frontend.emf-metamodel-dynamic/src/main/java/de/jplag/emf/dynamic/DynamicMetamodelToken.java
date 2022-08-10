package de.jplag.emf.dynamic;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.MetamodelToken;

/**
 * EMF metamodel token which uses dynamically created tokens.
 * @author Timur Saglam
 */
public class DynamicMetamodelToken extends MetamodelToken {

    public DynamicMetamodelToken(int type, String file, EObject eObject) {
        super(type, file, eObject);
    }

    public DynamicMetamodelToken(int type, String file) {
        super(type, file);
    }

    @Override
    protected String type2string() {
        if (type < DynamicMetamodelTokenConstants.TOKEN_TYPE_START) {
            return super.type2string();
        }
        return DynamicMetamodelTokenConstants.getTokenString(type);
    }

}
