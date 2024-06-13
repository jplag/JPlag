package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BaseCodeMatch(@JsonProperty("file_name") String fileName, @JsonProperty("startLine") int startLine,
        @JsonProperty("startCol") int startCol, @JsonProperty("startIndex") int startIndex, @JsonProperty("endLine") int endLine,
        @JsonProperty("endCol") int endCol, @JsonProperty("endIndex") int endIndex, @JsonProperty("tokens") int tokens) {
}
