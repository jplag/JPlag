package de.jplag.emf.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.ENamedElement;

import de.jplag.emf.MetamodelToken;

/**
 * Simplcisitc tree view representation of an EMF metamodel.
 * @author Timur Saglam
 */
public class MetamodelTreeView {
    private final String fileName;
    private final List<MetamodelToken> tokens;
    private int lineIndex;
    private int columnIndex;
    private final StringBuilder viewBuilder;
    private final static String INDENTATION = "  ";

    /**
     * Creates a tree view for a metamodel.
     * @param fileName is the file where the metamodel is persisted.
     */
    public MetamodelTreeView(String fileName) {
        this.fileName = fileName;
        tokens = new ArrayList<>();
        viewBuilder = new StringBuilder();
    }

    /**
     * Adds a token to the view, thus adding the index information to the token.
     * @param token is the token to add.
     * @param treeDepth is the current containment tree depth, required for the indentation.
     */
    public void addToken(MetamodelToken token, int treeDepth) {
        token.getEObject().ifPresent(it -> {
            tokens.add(token);

            String tokenText = token.toString();
            if (it instanceof ENamedElement element) {
                tokenText = element.getName() + " : " + tokenText;
            }
            token.setLength(tokenText.length());

            String prefix = ""; // TS TODO implement prefix for non new lines
            if (prefix.isEmpty()) {
                for (int i = 0; i < treeDepth; i++) {
                    tokenText = INDENTATION + tokenText;
                    columnIndex += INDENTATION.length();
                }
            } else {
                tokenText = prefix + tokenText;
                columnIndex += prefix.length();
            }
            viewBuilder.append(tokenText);

            token.setLine(lineIndex + 1);
            token.setColumn(columnIndex + 1);

            lineIndex++;
            columnIndex = 0;
            viewBuilder.append(System.lineSeparator());
        });

    }

    /**
     * Writes the tree view into a file.
     * @param directory is the output directory.
     * @param suffix is the suffix of the file to be written.
     */
    public void writeToFile(File directory, String suffix) {
        File treeViewFile = fileName.isEmpty() ? new File(directory.toString() + suffix) : new File(directory, fileName + suffix);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(treeViewFile));) {
            if (!treeViewFile.exists()) {
                treeViewFile.createNewFile();
            }
            writer.append(viewBuilder.toString());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
