package de.jplag.emf.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.emf.MetamodelToken;

/**
 * Simplistic tree view representation of an EMF metamodel.
 * @author Timur Saglam
 */
public class MetamodelTreeView {
    private final File file;
    private int lineIndex;
    private int columnIndex;
    private final StringBuilder viewBuilder;
    private static final String INDENTATION = "  ";
    public final Logger logger;

    /**
     * Creates a tree view for a metamodel.
     * @param file is the file where the metamodel is persisted.
     */
    public MetamodelTreeView(File file) {
        this.file = file;
        logger = LoggerFactory.getLogger(this.getClass());
        viewBuilder = new StringBuilder();
    }

    /**
     * Adds a token to the view, thus adding the index information to the token. Returns a new token enriched with the index
     * metadata.
     * @param token is the token to add.
     * @param treeDepth is the current containment tree depth, required for the indentation.
     */
    public MetamodelToken convertToMetadataEnrichedTokenAndAdd(MetamodelToken token, int treeDepth, String prefix) {
        int length = Token.NO_VALUE;
        int line = Token.NO_VALUE;
        int column = Token.NO_VALUE;
        Optional<EObject> optionalEObject = token.getEObject();
        if (optionalEObject.isPresent()) {
            EObject eObject = optionalEObject.get();
            if (prefix.isEmpty() && treeDepth > 0) {
                lineIndex++;
                columnIndex = 0;
                viewBuilder.append(System.lineSeparator());
            }

            String tokenText = token.getType().getDescription();
            if (eObject instanceof ENamedElement element) {
                tokenText = element.getName() + " : " + tokenText;
            }
            length = tokenText.length();

            if (prefix.isEmpty()) {
                for (int i = 0; i < treeDepth; i++) {
                    viewBuilder.append(INDENTATION);
                    columnIndex += INDENTATION.length();
                }
                viewBuilder.append(tokenText);
            } else {
                viewBuilder.append(prefix + tokenText);
                columnIndex += prefix.length();
            }

            line = lineIndex + 1;
            column = columnIndex + 1;

            columnIndex += tokenText.length();
        }
        return new MetamodelToken(token.getType(), token.getFile(), line, column, length, token.getEObject());
    }

    /**
     * Writes the tree view into a file.
     * @param suffix is the suffix of the file to be written.
     */
    public void writeToFile(String suffix) {
        File treeViewFile = new File(file + suffix);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(treeViewFile));) {
            if (!treeViewFile.createNewFile()) {
                logger.warn("Overwriting tree view file: {}", treeViewFile);
            }
            writer.append(viewBuilder.toString());
        } catch (IOException exception) {
            logger.error("Could not write tree view file!", exception);
        }
    }

}
