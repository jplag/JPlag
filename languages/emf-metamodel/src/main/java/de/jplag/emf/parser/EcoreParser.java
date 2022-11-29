package de.jplag.emf.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.emf.Language;
import de.jplag.emf.MetamodelToken;
import de.jplag.emf.MetamodelTokenType;
import de.jplag.emf.util.AbstractMetamodelVisitor;
import de.jplag.emf.util.EMFUtil;
import de.jplag.emf.util.EmfaticModelView;

/**
 * Parser for EMF metamodels.
 * @author Timur Saglam
 */
public class EcoreParser extends AbstractParser {
    protected List<Token> tokens;
    protected File currentFile;
    protected EmfaticModelView treeView;
    protected AbstractMetamodelVisitor visitor;

    /**
     * Creates the parser.
     */
    public EcoreParser() {
        EMFUtil.registerEcoreExtension();
    }

    /**
     * Parses all tokens from a set of files.
     * @param files is the set of files.
     * @return the list of parsed tokens.
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            parseModelFile(file);
        }
        return tokens;
    }

    /**
     * Loads a metamodel from a file and parses it.
     * @param file is the metamodel file.
     */
    protected void parseModelFile(File file) throws ParsingException {
        currentFile = file;
        Resource model = EMFUtil.loadModelResource(file);
        if (model == null) {
            throw new ParsingException(file, "failed to load model");
        } else {
            treeView = new EmfaticModelView(file, model);
            for (EObject root : model.getContents()) {
                visitor = createMetamodelVisitor();
                visitor.visit(root);
            }
            tokens.add(Token.fileEnd(currentFile));
            treeView.writeToFile(Language.VIEW_FILE_SUFFIX);
        }
    }

    /**
     * Extension point for subclasses to employ different token generators.
     * @return a token generating metamodel visitor.
     */
    protected AbstractMetamodelVisitor createMetamodelVisitor() {
        return new MetamodelTokenGenerator(this);
    }

    public void addToken(MetamodelTokenType type, EObject source, String prefix) {
        MetamodelToken token = new MetamodelToken(type, currentFile, source);
        MetamodelToken metadataEnrichedToken = treeView.convertToMetadataEnrichedToken(token);
        tokens.add(metadataEnrichedToken);
    }

    public void addToken(MetamodelTokenType type, EObject source) {
        addToken(type, source, "");
    }
}
