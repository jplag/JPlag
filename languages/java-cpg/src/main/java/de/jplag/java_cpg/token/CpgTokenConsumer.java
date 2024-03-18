package de.jplag.java_cpg.token;

import java.io.File;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation;
import de.fraunhofer.aisec.cpg.sarif.Region;
import de.jplag.TokenType;

/**
 * A {@link CpgTokenConsumer} can create appropriate {@link CpgToken}s for {@link TokenType}s.
 */
public abstract class CpgTokenConsumer implements TokenConsumer {

    public static final Logger logger = LoggerFactory.getLogger(CpgTokenConsumer.class);
    /**
     * This is used as the Token's length if the token spans multiple lines. The value is supposed to be greater than the
     * length of any sensible line of code.
     */
    private static final int MULTILINE_TOKEN_LENGTH = 1024;
    private File currentFile;

    private static int calculateLength(Region region) {
        if (region.getEndLine() == region.startLine) {
            return region.getEndColumn() - region.startColumn + 1;
        } else
            return MULTILINE_TOKEN_LENGTH;
    }

    public void addToken(TokenType type, Node node, boolean isEndToken) {
        logger.debug(type.toString() + "/" + node.toString());
        PhysicalLocation location = node.getLocation();

        File file;
        Region region;
        int length;
        if (Objects.isNull(location)) {
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
        Name name = node.getName();
        addToken(type, file, line, column, length, name);
    }

}
