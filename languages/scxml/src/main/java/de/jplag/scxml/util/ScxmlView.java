package de.jplag.scxml.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenTrace;
import de.jplag.scxml.ScxmlTokenType;
import de.jplag.scxml.parser.model.StatechartElement;

/**
 * A utility for generating a textual representation of SCXML statecharts. The contents of the view file are assembled
 * iteratively as new tokens are enhanced with positional information (line and column numbers) used by the JPlag report
 * viewer.
 */
public class ScxmlView {

    private static final String VIEW_INDENTATION = "  ";
    private static final String END_TOKEN_SYMBOL = "}";
    private final File file;
    private final StringBuilder builder;
    private final Logger logger;
    private int line;

    /**
     * Constructs a new ScxmlView that turns tokens into a textual representation. The provided input file determines the
     * path of the output view file.
     * @param file the input file corresponding to this view
     */
    public ScxmlView(File file) {
        this.file = file;
        this.builder = new StringBuilder();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.line = 1;
    }

    /**
     * Writes the current view file contents to the file specified in the constructor.
     * @param fileExtension the extension to use for the name of the view file
     */
    public void writeToFile(String fileExtension) {
        File viewFile = new File(file.toString() + fileExtension);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(viewFile))) {
            if (!viewFile.createNewFile()) {
                logger.warn("Overwriting statechart view file: {}", viewFile);
            }
            writer.append(builder.toString());
        } catch (IOException exception) {
            logger.error("Could not write statechart view file!", exception);
        }
    }

    /**
     * Appends a SCXML model element to the view and returns the corresponding tracing information.
     * @param tokenType is the type of token corresponding to the model element.
     * @param statechartElement is the model element to be added.
     * @param depth current tree depth in the statechart to determine the indent in the view file.
     * @return the view-related information regarding line and column indices.
     */
    public TokenTrace appendElement(ScxmlTokenType tokenType, StatechartElement statechartElement, int depth) {
        String prefix = VIEW_INDENTATION.repeat(depth);
        String textualRepresentation = tokenType.isEndToken() ? END_TOKEN_SYMBOL : Objects.toString(statechartElement, "");
        builder.append(prefix).append(textualRepresentation).append(System.lineSeparator());
        return new TokenTrace(line++, prefix.length() + 1, textualRepresentation.length());
    }
}
