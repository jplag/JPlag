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
    private Set<Variable> reads;
    private Set<Variable> writes;

    public TokenSemanticsBuilder() {
        this.reads = new HashSet<>();
        this.writes = new HashSet<>();
    }

    public TokenSemantics build() {
        return new TokenSemantics(critical, control, loopBegin, loopEnd, reads, writes);
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
        return this;
    }

    public TokenSemanticsBuilder loopEnd() {
        this.loopEnd = true;
        return this;
    }
}
