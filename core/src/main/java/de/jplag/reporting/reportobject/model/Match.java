package de.jplag.reporting.reportobject.model;

public record Match(String firstFileName, String secondFileName, CodePosition startInFirst, CodePosition endInFirst, CodePosition startInSecond,
        CodePosition endInSecond, int tokens, boolean isComment) {
    public Match(String firstFileName, String secondFileName, CodePosition startInFirst, CodePosition endInFirst, CodePosition startInSecond,
            CodePosition endInSecond, int tokens) {
        this(firstFileName, secondFileName, startInFirst, endInFirst, startInSecond, endInSecond, tokens, false);
    }

    public Match asComment() {
        return new Match(this.firstFileName, this.secondFileName, this.startInFirst, this.endInFirst, this.startInSecond, this.endInSecond,
                this.tokens, true);
    }
}
