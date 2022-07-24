package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Match(@JsonProperty("first_file_name") String firstFileName, @JsonProperty("second_file_name") String secondFileName,
        @JsonProperty("start_in_first") int startInFirst, @JsonProperty("end_in_first") int endInFirst,
        @JsonProperty("start_in_second") int startInSecond, @JsonProperty("end_in_second") int endInSecond, @JsonProperty("tokens") int tokens) {
}
