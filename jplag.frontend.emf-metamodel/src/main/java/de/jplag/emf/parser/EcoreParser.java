package de.jplag.emf.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import de.jplag.AbstractParser;
import de.jplag.Token;
import de.jplag.TokenConstants;
import de.jplag.emf.Language;
import de.jplag.emf.MetamodelToken;
import de.jplag.emf.util.AbstractMetamodelVisitor;
import de.jplag.emf.util.EMFUtil;
import de.jplag.emf.util.MetamodelTreeView;

/**
 * Parser for EMF metamodels.
 * @author Timur Saglam
 */
public class EcoreParser extends AbstractParser {
    protected List<Token> tokens;
    protected String currentFile;
    protected MetamodelTreeView treeView;
    protected AbstractMetamodelVisitor visitor;

    /**
     * Creates the parser.
     */
    public EcoreParser() {
        EMFUtil.registerEcoreExtension();
    }

    /**
     * Parses all tokens form a list of files.
     * @param directory is the base directory.
     * @param fileNames is the list of file names.
     * @return the list of parsed tokens.
     */
    public List<Token> parse(File directory, List<String> fileNames) {
        errors = 0;
        tokens = new ArrayList<>();
        for (String fileName : fileNames) {
            currentFile = fileName;
            String filePath = fileName.isEmpty() ? directory.toString() : directory.toString() + File.separator + fileName;
            parseModelFile(filePath);
        }
        return tokens;
    }

    /**
     * Loads a metamodel from a file and parses it.
     * @param filePath is the path to the metamodel file.
     */
    protected void parseModelFile(String filePath) {
        treeView = new MetamodelTreeView(filePath);
        List<EObject> model = EMFUtil.loadModel(filePath);
        if (model == null) {
            errors++;
        } else {
            for (EObject root : model) {
                visitor = createMetamodelVisitor();
                visitor.visit(root);
            }
            tokens.add(new MetamodelToken(TokenConstants.FILE_END, currentFile));
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

    public void addToken(int type, EObject source, String prefix) {
        MetamodelToken token = new MetamodelToken(type, currentFile, source);
        treeView.addToken(token, visitor.getCurrentTreeDepth(), prefix);
        tokens.add(token);
    }

    public void addToken(int type, EObject source) {
        addToken(type, source, "");
    }
}
