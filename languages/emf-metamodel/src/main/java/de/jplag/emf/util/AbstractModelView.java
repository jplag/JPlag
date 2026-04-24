package de.jplag.emf.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenTrace;
import de.jplag.TokenType;

/**
 * Textual representation of a model for the depiction of matches in submissions.
 */
public abstract class AbstractModelView {

    protected final File file;
    protected final Logger logger;
    protected final StringBuilder viewBuilder;

    protected AbstractModelView(File file) {
        this.file = file;
        logger = LoggerFactory.getLogger(this.getClass());
        viewBuilder = new StringBuilder();
    }

    /**
     * Generates token tracing information for the view based on a model element.
     * @param modelElement is the model element for which the tracing information is required.
     * @param tokenType is the type of token corresponding to the model element.
     * @return the view-related information regarding line and column indices.
     */
    public abstract TokenTrace getTokenTrace(EObject modelElement, TokenType tokenType);

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