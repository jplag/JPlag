package de.jplag.emf.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.emf.MetamodelToken;

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
     * Creates a token with tracing information based on an existing one without. The token information may also be used to
     * build up the model view. This means a model view may be only complete after passing every token to the view to
     * enrich.
     * @param token is the existing token without tracing information.
     * @return the enriched token, with the tracing information corresponding to this view.
     */
    public abstract MetamodelToken convertToMetadataEnrichedToken(MetamodelToken token);

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