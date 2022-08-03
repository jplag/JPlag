package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FilesOfSubmission(@JsonProperty("file_name") String fileName, @JsonProperty("lines") List<Long> lines) {
    public FilesOfSubmission(String fileName, List<Long> lines) {
        this.fileName = fileName;
        this.lines = List.copyOf(lines);
    }
}
