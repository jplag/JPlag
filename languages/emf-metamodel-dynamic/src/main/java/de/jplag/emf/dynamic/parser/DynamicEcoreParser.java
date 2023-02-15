package de.jplag.emf.dynamic.parser;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.MetamodelToken;
import de.jplag.emf.dynamic.DynamicMetamodelToken;
import de.jplag.emf.dynamic.DynamicMetamodelTokenType;
import de.jplag.emf.parser.EcoreParser;
import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Parser for EMF metamodels based on dynamically created tokens.
 * @author Timur Saglam
 */
public class DynamicEcoreParser extends EcoreParser {

    @Override
    protected AbstractMetamodelVisitor createMetamodelVisitor() {
        return new DynamicMetamodelTokenGenerator(this);
    }

    public void addToken(DynamicMetamodelTokenType type, EObject source) {
        MetamodelToken token = new DynamicMetamodelToken(type, currentFile, source);
        MetamodelToken metadataEnrichedToken = treeView.convertToMetadataEnrichedToken(token);
        tokens.add(metadataEnrichedToken);
    }
}
