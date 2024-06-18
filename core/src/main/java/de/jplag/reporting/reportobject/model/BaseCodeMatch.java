package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BaseCodeMatch(@JsonProperty("file_name") String fileName, @JsonProperty("start") CodePosition start,
        @JsonProperty("end") CodePosition end, @JsonProperty("tokens") int tokens) {
}
