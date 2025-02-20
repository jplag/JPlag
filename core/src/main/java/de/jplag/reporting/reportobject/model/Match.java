package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Match(@JsonProperty("firstFile") String firstFileName, @JsonProperty("secondFile") String secondFileName,
        @JsonProperty("startInFirst") CodePosition startInFirst, @JsonProperty("endInFirst") CodePosition endInFirst,
        @JsonProperty("startInSecond") CodePosition startInSecond, @JsonProperty("endInSecond") CodePosition endInSecond,
        @JsonProperty("tokens") int tokens) {
}
