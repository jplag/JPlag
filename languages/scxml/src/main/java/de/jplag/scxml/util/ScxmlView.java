package de.jplag.scxml.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.scxml.ScxmlToken;
import de.jplag.scxml.ScxmlTokenType;

/**
 * A utility for generating a textual representation of SCXML statecharts. The contents of the view file are assembled
 * iteratively as new tokens are enhanced with positional information (line and column numbers) used by the JPlag report
 * viewer.
 */
public class ScxmlView {

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
     * Enhances the given token by adding information about the line and column numbers in the view file. At the same time,
     * the contents of the file are constructed.
     * @param token the token to enhance and add to the view file
     * @param depth current depth in the statechart to determine the indent in the view file
     * @return the input token enhanced with view-related information
     */
    public ScxmlToken enhanceToken(ScxmlToken token, int depth) {
        String prefix = "  ".repeat(depth);
        ScxmlTokenType type = (ScxmlTokenType) token.getType();
        String element = token.getStatechartElement() == null ? "" : token.getStatechartElement().toString();
        String content = type.isEndToken() ? "}" : element;
        builder.append(prefix).append(content).append("\n");
        return new ScxmlToken(token.getType(), token.getFile(), line++, prefix.length() + 1, content.length(), token.getStatechartElement());
    }
}
