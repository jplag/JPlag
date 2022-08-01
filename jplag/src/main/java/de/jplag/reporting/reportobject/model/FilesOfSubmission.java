package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilesOfSubmission {

    @JsonProperty("file_name")
    private final String fileName;

    @JsonProperty("lines")
    private final List<String> lines;

    public FilesOfSubmission(String fileName, List<String> fileCode) {
        this.fileName = fileName;
        this.lines = List.copyOf(fileCode);
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getLines() {
        return lines;
    }

}
