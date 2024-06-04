package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CodePosition(@JsonProperty("line") int lineNumber, @JsonProperty("column") int column,
        @JsonProperty("tokenListIndex") int tokenListIndex) {
}
