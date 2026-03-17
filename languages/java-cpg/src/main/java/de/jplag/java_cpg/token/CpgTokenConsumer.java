package de.jplag.java_cpg.token;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation;
import de.fraunhofer.aisec.cpg.sarif.Region;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * A {@link CpgTokenConsumer} can create appropriate {@link CpgToken}s for {@link TokenType}s.
 */
public abstract class CpgTokenConsumer implements TokenConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CpgTokenConsumer.class);
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

    /**
     * Adds a new {@link Token} for the given {@link TokenType} and {@link Node}.
     * @param type the {@link TokenType}
     * @param node the represented {@link Node}
     * @param isEndToken true iff the token represents the end of a block
     */
    public void addToken(TokenType type, Node node, boolean isEndToken) {
        logger.debug("{} / {}", type, node);
        PhysicalLocation location = node.getLocation();

        File file;
        Region region;
        if (Objects.nonNull(location)) {
            file = new File(location.getArtifactLocation().getUri());
            region = location.getRegion();
            currentFile = file;
        } else if (node instanceof Declaration) {
            // implicit node, e.g., declaration of API classes - we do not want tokens for those.
            return;
        } else {
            // nodes created and _inserted_ during transformations (new nodes which _replace_ others get their location)
            file = currentFile;
            region = new Region();
        }

        Region newRegion;
        if (node instanceof Block) {
            int line = isEndToken ? region.getEndLine() : region.startLine;
            int column = isEndToken ? region.getEndColumn() - 1 : region.startColumn;
            newRegion = new Region(line, column, line, column + 1);
        } else {

            List<Region> childRegions = node.getAstChildren().stream().map(Node::getLocation).filter(Objects::nonNull)
                    .map(PhysicalLocation::getRegion).filter(childRegion -> regionContains(region, childRegion)).sorted().toList();

            if (childRegions.isEmpty()) {
                newRegion = region;
            } else if (!isEndToken) {
                // location encompasses the whole node AND all child nodes, not the syntactic element that represents the node.
                // As an approximation for the missing values, we use the beginning and end of the child nodes, respectively, which is
                // not ideal.
                Region nextTokenRegion = childRegions.getFirst();
                newRegion = new Region(region.startLine, region.startColumn, nextTokenRegion.startLine,
                        IntStream.of(1, nextTokenRegion.startColumn - 1, region.startColumn).max().getAsInt());
            } else {
                Region previousTokenRegion = childRegions.getLast();
                newRegion = new Region(previousTokenRegion.getEndLine(), previousTokenRegion.getEndColumn(), region.getEndLine(),
                        region.getEndColumn());
            }
        }

        Name name = node.getName();
        addToken(type, file, newRegion, calculateLength(newRegion), name);
    }

    private boolean regionContains(Region outer, Region inner) {
        boolean startContained = outer.startLine < inner.startLine || (outer.startLine == inner.startLine && outer.startColumn <= inner.startColumn);
        boolean endContained = outer.getEndLine() > inner.getEndLine()
                || (outer.getEndLine() == inner.getEndLine() && outer.getEndColumn() >= inner.getEndColumn());
        return startContained && endContained;
    }

}
