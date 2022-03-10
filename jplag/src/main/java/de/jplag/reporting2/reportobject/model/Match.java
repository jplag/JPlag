package de.jplag.reporting2.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Match {

    @JsonProperty("first_file_name")
    private final String firstFileName;

    @JsonProperty("second_file_name")
    private final String secondFileName;

    @JsonProperty("start_in_first")
    private final int startInFirst;

    @JsonProperty("end_in_first")
    private final int endInFirst;

    @JsonProperty("start_in_second")
    private final int startInSecond;

    @JsonProperty("end_in_second")
    private final int endInSecond;

    @JsonProperty("tokens")
    private final int tokens;

    public Match(String firstFileName, String secondFileName, int startInFirst, int endInFirst, int startInSecond, int endInSecond, int tokens) {
        this.firstFileName = firstFileName;
        this.secondFileName = secondFileName;
        this.startInFirst = startInFirst;
        this.endInFirst = endInFirst;
        this.startInSecond = startInSecond;
        this.endInSecond = endInSecond;
        this.tokens = tokens;
    }

    public String getFirstFileName() {
        return firstFileName;
    }

    public String getSecondFileName() {
        return secondFileName;
    }

    public int getStartInFirst() {
        return startInFirst;
    }

    public int getEndInFirst() {
        return endInFirst;
    }

    public int getStartInSecond() {
        return startInSecond;
    }

    public int getEndInSecond() {
        return endInSecond;
    }

    public int getTokens() {
        return tokens;
    }
}
