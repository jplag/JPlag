package de.jplag.emf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.ENamedElement;

public class MetamodelTreeView {
    private final String fileName;
    private final List<MetamodelToken> tokens;
    private int lineIndex;
    private int columnIndex;
    private final StringBuilder viewBuilder;
    private final static String INDENTATION = "  ";

    public MetamodelTreeView(String fileName) {
        this.fileName = fileName;
        tokens = new ArrayList<>();
        viewBuilder = new StringBuilder();
    }

    public void addToken(MetamodelToken token, int treeDepth) {
        token.getEObject().ifPresent(it -> {
            tokens.add(token);

            String tokenText = token.type2string();
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
