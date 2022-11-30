package de.jplag.emf.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Textual representation of a model for the depiction of matches in submissions.
 * @author Timur Saglam
 */
public class AbstractModelView {

    protected final File file;
    protected final Logger logger;
    protected final StringBuilder viewBuilder;

    public AbstractModelView(File file) {
        this.file = file;
        logger = LoggerFactory.getLogger(this.getClass());
        viewBuilder = new StringBuilder();
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