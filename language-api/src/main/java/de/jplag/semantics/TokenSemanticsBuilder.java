package de.jplag.semantics;

import java.util.HashSet;
import java.util.Set;

/**
 * A builder class for the TokenSemantics record.
 */
public class TokenSemanticsBuilder {
    private boolean critical;
    private boolean control;
    private boolean blockBegin;
    private boolean blockEnd;
    private boolean blockIsLoop;
    private Set<Variable> reads;
    private Set<Variable> writes;

    public TokenSemanticsBuilder() {
        this.reads = new HashSet<>();
        this.writes = new HashSet<>();
    }

    public TokenSemantics build() {
        return new TokenSemantics(critical, control, blockBegin, blockEnd, blockIsLoop, reads, writes);
    }

    public TokenSemanticsBuilder critical() {
        this.critical = true;
        return this;
    }

    public TokenSemanticsBuilder control() {
        this.control = true;
        return this;
    }

    public TokenSemanticsBuilder blockBegin() {
        this.blockBegin = true;
        return this;
    }

    public TokenSemanticsBuilder blockEnd() {
        this.blockEnd = true;
        return this;
    }

    public TokenSemanticsBuilder blockIsLoop() {
        this.blockIsLoop = true;
        return this;
    }
}
