package de.jplag.emf.dynamic.parser;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import de.jplag.TokenType;
import de.jplag.emf.MetamodelToken;
import de.jplag.emf.dynamic.DynamicMetamodelToken;
import de.jplag.emf.normalization.ModelSorter;
import de.jplag.emf.parser.EcoreParser;
import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Parser for EMF metamodels based on dynamically created tokens. This means each metaclass corresponds to a token type.
 * @author Timur Saglam
 */
public class DynamicEcoreParser extends EcoreParser {

    private final DynamicElementTokenizer tokenizer;

    /**
     * Creates the parser and the corresponding tokenizer.
     */
    public DynamicEcoreParser() {
        tokenizer = new DynamicElementTokenizer();
    }

    @Override
    protected AbstractMetamodelVisitor createMetamodelVisitor() {
        return new DynamicMetamodelTokenGenerator(this, tokenizer);
    }

    @Override
    protected void normalizeOrder(Resource modelResource) {
        ModelSorter.sort(modelResource, tokenizer);
    }

    @Override
    public void addToken(TokenType type, EObject source) {
        MetamodelToken token = new DynamicMetamodelToken(type, currentFile, source);
        MetamodelToken metadataEnrichedToken = treeView.convertToMetadataEnrichedToken(token);
        tokens.add(metadataEnrichedToken);
    }
}
