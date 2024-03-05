package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BaseCodeMatch(@JsonProperty("file_name") String fileName, @JsonProperty("start") int start, @JsonProperty("end") int end,
        @JsonProperty("tokens") int tokens) {
}
