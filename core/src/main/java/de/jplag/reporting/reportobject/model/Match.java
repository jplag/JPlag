package de.jplag.reporting.reportobject.model;

public record Match(String firstFileName, String secondFileName, CodePosition startInFirst, CodePosition endInFirst, CodePosition startInSecond,
        CodePosition endInSecond, int lengthOfFirst, int lengthOfSecond, boolean isComment) {
    public Match(String firstFileName, String secondFileName, CodePosition startInFirst, CodePosition endInFirst, CodePosition startInSecond,
            CodePosition endInSecond, int lengthOfFirst, int lengthOfSecond) {
        this(firstFileName, secondFileName, startInFirst, endInFirst, startInSecond, endInSecond, lengthOfFirst, lengthOfSecond, false);
    }

    public Match asComment() {
        return new Match(this.firstFileName, this.secondFileName, this.startInFirst, this.endInFirst, this.startInSecond, this.endInSecond,
                this.lengthOfFirst, this.lengthOfSecond, true);
    }
}
