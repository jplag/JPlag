package de.jplag.java_cpg.token;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import de.fraunhofer.aisec.cpg.graph.Name;
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
     * @param startLine the starting line of the represented code
     * @param startColumn the starting column of the represented code
     * @param length the length of the represented code
     * @param name the name of the represented CPG node
     */
    public CpgToken(TokenType tokenType, File file, int startLine, int startColumn, int length, Name name) {
        super(tokenType, file, startLine, startColumn, length);
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
        return Optional.of(name).map(Name::hashCode).orElse(1) * getType().hashCode();
    }
}
