package de.jplag.java_cpg.token;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation;
import de.fraunhofer.aisec.cpg.sarif.Region;
import de.jplag.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

/**
 * {@link CpgTokenConsumer}s can create appropriate {@link CpgToken}s for CPG {@link Node}s.
 */
public abstract class CpgTokenConsumer implements TokenConsumer {


    public static final Logger logger = LoggerFactory.getLogger(CpgTokenConsumer.class);
    /**
     * This is used as the Token's length if the token spans multiple lines.
     * The value is supposed to be greater than the length of any sensible line of code.
     * <p>
     *  TODO: Determine how other modules handle this
     */
    private static final int MULTILINE_TOKEN_LENGTH = 1024;
    private File currentFile;

    private static int calculateLength(Region region) {
        if (region.getEndLine() == region.startLine) {
            return region.getEndColumn() - region.startColumn + 1;
        } else return MULTILINE_TOKEN_LENGTH;
    }

    public void addToken(TokenType type, Node node, boolean isEndToken) {
        logger.debug(type.toString() + "/" + node.toString());
        PhysicalLocation location = node.getLocation();

        File file;
        Region region;
        int length;
        if (Objects.isNull(location)) {
            if (!includeNonLocal(type)) {
                // This is a library element, not part of the submission
                return;
            }
            file = currentFile;
            region = new Region();
            length = 0;
        } else {
            file = new File(location.getArtifactLocation().getUri());
            currentFile = file;
            region = location.getRegion();
            length = calculateLength(region);
        }
        int line;
        int column;
        if (isEndToken) {
            line = region.getEndLine();
            column = region.getEndColumn();
        } else {
            line = region.startLine;
            column = region.startColumn;
        }
        addToken(type, file, line, column, length);
    }

    public boolean includeNonLocal(TokenType type) {
        // MUST return true for FILE_END, otherwise JPlag will fail
        return true;
        //return List.of(FILE_END).contains(type);
    }
}
