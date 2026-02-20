package de.jplag.java_cpg.token;

import java.io.File;
import java.util.Objects;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.sarif.Region;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * This class represents a Token in the context of the CPG module of JPlag.
 */
public class CpgToken extends Token {
    private final Name name;

    /**
     * Creates a new {@link CpgToken}.
     * @param tokenType the {@link TokenType}
     * @param file the {@link File} that contains the represented piece of code
     * @param region the {@link Region} representing the Token location
     * @param length the length of the represented code
     * @param name the name of the represented CPG node
     */
    public CpgToken(TokenType tokenType, File file, Region region, int length, Name name) {
        super(tokenType, file, region.startLine, region.startColumn, region.getEndLine(), region.getEndColumn(), length);
        this.name = name;
    }

    @Override
    public String toString() {
        return Objects.isNull(name) ? super.toString() : "%s(%s)".formatted(this.getType(), this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CpgToken otherToken)) {
            return false;
        }
        return this.getType().equals(otherToken.getType());
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }
}
