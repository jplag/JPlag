package de.jplag.emf.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.emf.ecore.ENamedElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.emf.MetamodelToken;

/**
 * Simplistic tree view representation of an EMF metamodel.
 * @author Timur Saglam
 */
public class MetamodelTreeView {
    private final String filePath;
    private int lineIndex;
    private int columnIndex;
    private final StringBuilder viewBuilder;
    private final static String INDENTATION = "  ";
    public final Logger logger;

    /**
     * Creates a tree view for a metamodel.
     * @param filePath is the path to the file where the metamodel is persisted.
     */
    public MetamodelTreeView(String filePath) {
        this.filePath = filePath;
        logger = LoggerFactory.getLogger(this.getClass());
        viewBuilder = new StringBuilder();
    }

    /**
     * Adds a token to the view, thus adding the index information to the token.
     * @param token is the token to add.
     * @param treeDepth is the current containment tree depth, required for the indentation.
     */
    public void addToken(MetamodelToken token, int treeDepth, String prefix) {
        token.getEObject().ifPresent(it -> {
            if (prefix.isEmpty() && treeDepth > 0) {
                lineIndex++;
                columnIndex = 0;
                viewBuilder.append(System.lineSeparator());
            }

            String tokenText = token.toString();
            if (it instanceof ENamedElement element) {
                tokenText = element.getName() + " : " + tokenText;
            }
            token.setLength(tokenText.length());

            if (prefix.isEmpty()) {
                String indentedText = tokenText;
                for (int i = 0; i < treeDepth; i++) {
                    indentedText = INDENTATION + indentedText;
                    columnIndex += INDENTATION.length();
                }
                viewBuilder.append(indentedText);
            } else {
                viewBuilder.append(prefix + tokenText);
                columnIndex += prefix.length();
            }

            token.setLine(lineIndex + 1);
            token.setColumn(columnIndex + 1);

            columnIndex += tokenText.length();

        });
    }

    /**
     * Writes the tree view into a file.
     * @param suffix is the suffix of the file to be written.
     */
    public void writeToFile(String suffix) {
        File treeViewFile = new File(filePath + suffix);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(treeViewFile));) {
            if (!treeViewFile.createNewFile()) {
                logger.warn("Overwriting tree view file: " + treeViewFile);
            }
            writer.append(viewBuilder.toString());
        } catch (IOException exception) {
            logger.error("Could not write tree view file!", exception);
        }
    }

}
