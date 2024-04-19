package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Match(@JsonProperty("file1") String firstFileName, @JsonProperty("file2") String secondFileName,
        @JsonProperty("start1") int startInFirst, @JsonProperty("start1_col") int startColumnInFirst, @JsonProperty("end1") int endInFirst,
        @JsonProperty("end1_col") int endColumnInFirst, @JsonProperty("start2") int startInSecond,
        @JsonProperty("start2_col") int startColumnInSecond, @JsonProperty("end2") int endInSecond, @JsonProperty("end2_col") int endColumnInSecond,
        @JsonProperty("tokens") int tokens) {
}
