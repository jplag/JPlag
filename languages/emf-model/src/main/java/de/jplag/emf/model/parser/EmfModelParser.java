package de.jplag.emf.model.parser;

import java.io.File;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.TokenTrace;
import de.jplag.TokenType;
import de.jplag.emf.EmfLanguage;
import de.jplag.emf.MetamodelToken;
import de.jplag.emf.model.EmfModelLanguage;
import de.jplag.emf.normalization.ModelSorter;
import de.jplag.emf.parser.EcoreParser;
import de.jplag.emf.util.AbstractMetamodelVisitor;
import de.jplag.emf.util.AbstractModelView;
import de.jplag.emf.util.EMFUtil;
import de.jplag.emf.util.GenericEmfTreeView;

/**
 * Parser for EMF model instances based on dynamically created tokens. Each model element corresponds to a token whose
 * type is derived from the EMF type of that model element.
 */
public class EmfModelParser extends EcoreParser {
    private static final Logger logger = LoggerFactory.getLogger(EmfModelParser.class);

    private static final String VIEW_FILE_WARNING = "Skipping view file {} as submission!";
    private static final String METAMODEL_WARNING = "Metamodel files like {} should be specified via the language options!";
    private static final String ALL_EXTENSIONS = "*";

    private final EmfModelElementTokenizer tokenizer;

    /**
     * Creates the parser.
     */
    public EmfModelParser() {
        tokenizer = new EmfModelElementTokenizer();
        EMFUtil.registerModelExtension(ALL_EXTENSIONS);
    }

    @Override
    protected void parseModelFile(File file, boolean normalize) throws ParsingException {
        if (file.getName().endsWith(EmfLanguage.FILE_ENDING)) {
            logger.warn(METAMODEL_WARNING, file.getName());
        } else if (file.getName().endsWith(EmfModelLanguage.VIEW_FILE_EXTENSION)) {
            logger.warn(VIEW_FILE_WARNING, file.getName());
        } else {
            super.parseModelFile(file, normalize);
        }
    }

    @Override
    protected String getCorrespondingViewFileExtension() {
        return EmfModelLanguage.VIEW_FILE_EXTENSION;
    }

    @Override
    protected AbstractModelView createView(File file, Resource modelResource) {
        return new GenericEmfTreeView(file, modelResource);
    }

    @Override
    protected AbstractMetamodelVisitor createMetamodelVisitor() {
        return new EmfModelTokenGenerator(this, tokenizer);
    }

    @Override
    protected void normalizeOrder(Resource modelResource) {
        ModelSorter.sort(modelResource, tokenizer);
    }

    @Override
    public void addToken(TokenType type, EObject source) {
        TokenTrace trace = treeView.getTokenTrace(source, type);
        tokens.add(new MetamodelToken(type, currentFile, trace, source));
    }
}
