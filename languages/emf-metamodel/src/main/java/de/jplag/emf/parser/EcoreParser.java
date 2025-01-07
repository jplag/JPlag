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
import de.jplag.TokenAttribute;
import de.jplag.emf.EmfLanguage;
import de.jplag.emf.MetamodelToken;
import de.jplag.emf.normalization.ModelSorter;
import de.jplag.emf.util.AbstractMetamodelVisitor;
import de.jplag.emf.util.AbstractModelView;
import de.jplag.emf.util.EMFUtil;
import de.jplag.emf.util.EmfaticModelView;

/**
 * Parser for EMF metamodels.
 */
public class EcoreParser extends AbstractParser {
    protected List<Token> tokens;
    protected File currentFile;
    protected AbstractModelView treeView;
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
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            parseModelFile(file, normalize);
        }
        return tokens;
    }

    /**
     * Loads a metamodel from a file and parses it.
     * @param file is the metamodel file.
     */
    protected void parseModelFile(File file, boolean normalize) throws ParsingException {
        currentFile = file;
        Resource model = EMFUtil.loadModelResource(file);
        if (model == null) {
            throw new ParsingException(file, "failed to load model");
        }
        if (normalize) {
            normalizeOrder(model);
        }
        treeView = createView(file, model);
        visitor = createMetamodelVisitor();
        for (EObject root : model.getContents()) {
            visitor.visit(root);
        }
        tokens.add(Token.fileEnd(currentFile));
        treeView.writeToFile(getCorrespondingViewFileSuffix());
    }

    /**
     * @return the correct view file suffix for the model view. Can be overriden in subclasses for alternative views.
     */
    protected String getCorrespondingViewFileSuffix() {
        return EmfLanguage.VIEW_FILE_SUFFIX;
    }

    /**
     * Creates a model view. Can be overriden in subclasses for alternative views.
     * @param file is the path for the view file to be created.
     * @param modelResource is the resource containing the metamodel.
     * @return the view implementation.
     * @throws ParsingException if view could not be created due to an invalid model.
     */
    protected AbstractModelView createView(File file, Resource modelResource) throws ParsingException {
        return new EmfaticModelView(file, modelResource);
    }

    /**
     * Extension point for subclasses to employ different normalization.
     */
    protected void normalizeOrder(Resource modelResource) {
        ModelSorter.sort(modelResource, new MetamodelElementTokenizer());
    }

    /**
     * Extension point for subclasses to employ different token generators.
     * @return a token generating metamodel visitor.
     */
    protected AbstractMetamodelVisitor createMetamodelVisitor() {
        return new MetamodelTokenGenerator(this);
    }

    /**
     * Adds an token to the parser.
     * @param type is the token type.
     * @param source is the corresponding {@link EObject} for which the token is added.
     */
    protected void addToken(TokenAttribute type, EObject source) {
        MetamodelToken token = new MetamodelToken(type, currentFile, source);
        tokens.add(treeView.convertToMetadataEnrichedToken(token));
    }
}
