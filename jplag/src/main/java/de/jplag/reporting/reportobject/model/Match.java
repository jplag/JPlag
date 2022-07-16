package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Match(@JsonProperty("first_file_name") String firstFileName, @JsonProperty("second_file_name") String secondFileName,
        @JsonProperty("start_in_first") int startInFirst, @JsonProperty("end_in_first") int endInFirst,
        @JsonProperty("start_in_second") int startInSecond, @JsonProperty("end_in_second") int endInSecond, @JsonProperty("tokens") int tokens) {

    public Match(String firstFileName, String secondFileName, int startInFirst, int endInFirst, int startInSecond, int endInSecond, int tokens) {
        this.firstFileName = firstFileName;
        this.secondFileName = secondFileName;
        this.startInFirst = startInFirst;
        this.endInFirst = endInFirst;
        this.startInSecond = startInSecond;
        this.endInSecond = endInSecond;
        this.tokens = tokens;
    }

    @Override
    public String firstFileName() {
        return firstFileName;
    }

    @Override
    public String secondFileName() {
        return secondFileName;
    }

    @Override
    public int startInFirst() {
        return startInFirst;
    }

    @Override
    public int endInFirst() {
        return endInFirst;
    }

    @Override
    public int startInSecond() {
        return startInSecond;
    }

    @Override
    public int endInSecond() {
        return endInSecond;
    }

    @Override
    public int tokens() {
        return tokens;
    }
}
