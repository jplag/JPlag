package de.jplag.java_cpg.token;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation;
import de.fraunhofer.aisec.cpg.sarif.Region;
import de.jplag.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static de.jplag.SharedTokenType.FILE_END;

/**
 *  {@link CpgTokenConsumer}s can create appropriate {@link CpgToken}s for CPG {@link Node}s.
 */
public interface CpgTokenConsumer extends TokenConsumer {

    default void addToken(TokenType type, Node node, boolean isEndToken) {
        logger.info(type.toString() + "/" + node.toString() );
        PhysicalLocation location = node.getLocation();

        File file;
        Region region;
        if (Objects.isNull(location)) {
            if (includeNonLocal(type)) {

            } else {
                // This is a library element, not part of the submission
                return;
            }
        }
        file = new File(location.getArtifactLocation().getUri());
        region = location.getRegion();

        int line;
        int column;
        if (isEndToken) {
            line = region.getEndLine();
            column = region.getEndColumn();
        } else {
            line = region.startLine;
            column = region.startColumn;
        }
        addToken(type, file, line, column, 0);
    }

    default boolean includeNonLocal(TokenType type) {
        return List.of(FILE_END).contains(type);
    }

    /**
     *  This is used as the Token's length if the token spans multiple lines.
     *  The value is supposed to be greater than the length of any sensible line of code.
     *
     *  TODO: Determine how other modules handle this
     */
    int MULTILINE_TOKEN_LENGTH = 1024;

    private static int calculateLength(Region region) {
        if (region.getEndLine() == region.startLine) {
            return region.getEndColumn() - region.startColumn + 1;
        } else return MULTILINE_TOKEN_LENGTH;
    }

    Logger logger = LoggerFactory.getLogger(CpgTokenConsumer.class);
}
