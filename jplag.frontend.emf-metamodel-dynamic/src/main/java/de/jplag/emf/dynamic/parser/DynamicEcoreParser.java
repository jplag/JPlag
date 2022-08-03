package de.jplag.emf.dynamic.parser;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.MetamodelToken;
import de.jplag.emf.dynamic.DynamicMetamodelToken;
import de.jplag.emf.dynamic.Language;
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

    @Override
    public void addToken(int type, EObject source) {
        MetamodelToken token = new DynamicMetamodelToken(type, currentFile + Language.VIEW_FILE_SUFFIX, source);
        treeView.addToken(token, visitor.getCurrentTreeDepth());
        tokens.addToken(token);
    }
}
