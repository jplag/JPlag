package de.jplag.semantics;

import java.util.HashSet;
import java.util.Set;

/**
 * A builder class for the TokenSemantics record.
 */
public class TokenSemanticsBuilder {
    private boolean critical;
    private boolean control;
    private boolean loopBegin;
    private boolean loopEnd;
    private Set<String> writes;
    private Set<String> reads;

    public TokenSemanticsBuilder() {
        this.writes = new HashSet<>();
        this.reads = new HashSet<>();
    }

    public TokenSemantics build() {
        if (loopBegin && loopEnd) {
            throw new IllegalStateException("Token can't mark both the beginning and end of a loop");
        }
        return new TokenSemantics(critical, control, loopBegin, loopEnd, writes, reads);
    }

    public TokenSemanticsBuilder critical() {
        this.critical = true;
        return this;
    }

    public TokenSemanticsBuilder control() {
        this.control = true;
        return this;
    }

    public TokenSemanticsBuilder loopBegin() {
        this.loopBegin = true;
        this.control = true;
        return this;
    }

    public TokenSemanticsBuilder loopEnd() {
        this.loopEnd = true;
        this.control = true;
        return this;
    }
}
