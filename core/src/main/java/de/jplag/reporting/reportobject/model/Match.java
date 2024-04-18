package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Match(@JsonProperty("file1") String firstFileName, @JsonProperty("file2") String secondFileName,
        @JsonProperty("start1") int startInFirst, @JsonProperty("end1") int endInFirst, @JsonProperty("startToken1") int startTokenInFirst,
        @JsonProperty("endToken1") int endTokenInFirst, @JsonProperty("start2") int startInSecond, @JsonProperty("end2") int endInSecond,
        @JsonProperty("startToken2") int startTokenInSecond, @JsonProperty("endToken2") int endTokenInSecond, @JsonProperty("tokens") int tokens) {
}
