package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CodePosition(
        // 1-based
        @JsonProperty("line") int lineNumber,
        // 0-based
        @JsonProperty("column") int column,
        // 0-based
        @JsonProperty("tokenListIndex") int tokenListIndex) {
}
